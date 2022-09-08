package com.bjit.common.rest.app.service.controller.drawing;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.common.rest.app.service.model.drawing.request.DrawingDataRequest;
import com.bjit.common.rest.app.service.model.drawing.request.RootDrawingDataRequest;
import com.bjit.common.rest.app.service.model.drawing.response.ItemData;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.search.SearchService;
import com.bjit.common.rest.app.service.search.drawing.DrawingDataServiceImpl;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.bjit.mapper.mapproject.util.Constants;
import matrix.db.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BJIT
 */
@RestController
@RequestMapping("/valmet/enovia/api/")
public class DrawingDataController {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(DrawingDataController.class);
    private static final String COULDN_T_RECOGNIZE_THE_SOURCE = "System couldn't recognize the source ";
    private static final String SOLIDWORKS = "solidworks";
    private static final String USER = "user";
    private static final String PASS = "pass";
    final private SearchService searchService;
    final private DrawingDataServiceImpl drawingDataServiceImpl;
    Context context;

    public DrawingDataController(SearchService searchService, DrawingDataServiceImpl drawingDataServiceImpl) {
        this.searchService = searchService;
        this.drawingDataServiceImpl = drawingDataServiceImpl;
    }

    @PostMapping(value = "/v1/drawing/data", produces = "application/json")
    public ResponseEntity<?> drawingData(HttpServletRequest httpRequest, @RequestBody RootDrawingDataRequest rootDrawingDataRequest) {
        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse;
        try {
            if(rootDrawingDataRequest.getSource().equalsIgnoreCase(SOLIDWORKS)) {
                if(rootDrawingDataRequest.getParams().size() > 0) {
                    //Connect context
                    String user = httpRequest.getHeader(USER);
                    String pass = httpRequest.getHeader(PASS);
                    ResponseEntity<String> CONTEXT_EXCEPTION = connectContext(user, pass);
                    if (CONTEXT_EXCEPTION != null) return CONTEXT_EXCEPTION;

                    //Getting list of item info
                    List<ItemData> data = new ArrayList<>();
                    for (DrawingDataRequest drawingDataRequest: rootDrawingDataRequest.getParams()) {
                        Items items ;
                        if (!NullOrEmptyChecker.isNullOrEmpty(drawingDataRequest.getType()) && !NullOrEmptyChecker.isNullOrEmpty(drawingDataRequest.getName())) {
                            //Get item info from common search with TNR
                            CommonItemSearchBean commonItemSearchBean = new CommonItemSearchBean(drawingDataRequest.getType(), drawingDataRequest.getName(), drawingDataRequest.getRevision());
                            items = searchService.getItems(context, commonItemSearchBean);
                            if (items.getItems().isEmpty()) {
                                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.TYPE_NAME_BE_NULL_EXCEPTION).setStatus(Status.FAILED).buildResponse();
                                LOGGER.error(buildResponse);
                                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                            }
                        } else if (!NullOrEmptyChecker.isNullOrEmpty(drawingDataRequest.getObjectId())) {
                            //Get item info with ObjectId
                            items = drawingDataServiceImpl.getItemByObjectId(context, drawingDataRequest.getObjectId());
                        } else {
                            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.INVALID_ATTRIBUTE_MESSAGE).setStatus(Status.FAILED).buildResponse();
                            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                        }
                        //Get Item List
                        drawingDataServiceImpl.getItemList(context, data, items);
                    }

                    //Setting project and task info list to response data
                    responseBuilder.setData(data);

                    //Building response and return
                    buildResponse = responseBuilder.setStatus(Status.OK).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.OK);
                } else {
                    buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.INVALID_ATTRIBUTE_MESSAGE).setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                String errMsg = COULDN_T_RECOGNIZE_THE_SOURCE + "'" + rootDrawingDataRequest.getSource() + "'";
                buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(errMsg).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception exp) {
            buildResponse = responseBuilder.setStatus(Status.FAILED).addErrorMessage(Constants.INVALID_ATTRIBUTE_MESSAGE).setStatus(Status.FAILED).buildResponse();
            LOGGER.error(buildResponse);
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
    }

    //Connecting context
    private ResponseEntity<String> connectContext(String user, String pass) {
        if (NullOrEmptyChecker.isNullOrEmpty(user) || NullOrEmptyChecker.isNullOrEmpty(pass)) {
            try {
                CreateContext createContext = new CreateContext();
                context = createContext.getAdminContext();
                if (!context.isConnected()) {
                    throw new Exception(Constants.CONTEXT_EXCEPTION);
                }
            } catch (Exception exp) {
                return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            try {
                CreateContext createContext = new CreateContext();
                context = createContext.getContext(user, pass);
                if (!context.isConnected()) {
                    throw new Exception(Constants.CONTEXT_EXCEPTION);
                }
            } catch (Exception exp) {
                return new ResponseEntity<>(Constants.CONTEXT_EXCEPTION, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return null;
    }
}
