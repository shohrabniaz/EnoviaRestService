package com.bjit.common.rest.app.service.controller.itemhistory;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.itemhistory.errors.ItemHistoryErrorResponse;
import com.bjit.common.rest.app.service.controller.itemhistory.errors.ValidationErrors;
import com.bjit.common.rest.app.service.controller.itemhistory.model.Constraint;
import com.bjit.common.rest.app.service.controller.itemhistory.model.Data;
import com.bjit.common.rest.app.service.controller.itemhistory.model.ItemSearchRequestModel;
import com.bjit.common.rest.app.service.controller.itemhistory.service.pid.ItemsBasicDetailsFetcher;
import com.bjit.common.rest.app.service.controller.itemhistory.service.pid.ItemsBasicDetailsFetcherByPID;
import com.bjit.common.rest.app.service.controller.itemhistory.service.pid.ItemsBasicDetailsFetcherByTNR;
import com.bjit.common.rest.app.service.controller.itemhistory.validator.DescIdValidator;
import com.bjit.common.rest.app.service.controller.itemhistory.validator.TNROrPhysicalIdExistenceValidator;
import com.bjit.common.rest.app.service.controller.itemhistory.validator.Validator;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.JSON;

import javax.servlet.http.HttpServletRequest;

import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Touhidul Islam
 */
@Controller
@RequestMapping(path = "/api/v1/item-history")
public class ItemHistoryController {

    private static final Logger LOGGER = Logger.getLogger(ItemHistoryController.class);

    @RequestMapping(value = "/fetch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity fetch(HttpServletRequest httpRequest, @RequestBody final String rootObject) {
        LOGGER.debug(" ++++++++++++ start to fetch history +++++++++++++++++ ");
        LOGGER.info(rootObject);
        ItemSearchRequestModel reqData = new JSON().deserialize(rootObject, ItemSearchRequestModel.class);
        LOGGER.debug("Parsed Request Model-");
        LOGGER.debug(new JSON().serialize(reqData));
        List<Data> dataList = reqData.getData();
        Constraint constraint = reqData.getConstraint();
        LOGGER.debug(new JSON().serialize(reqData));

        // START: do valid request data
        // {@Link PhysicalIdValidator} is not used cause of poor runtime performance
        Validator validator = new DescIdValidator();
        Validator tnrOrPhysicalIdExistenceValidator = new TNROrPhysicalIdExistenceValidator();
        validator.setNextValidator(tnrOrPhysicalIdExistenceValidator);
        // others validator can be occured. ie- SourceValidator/Constraint Validator etc, so on

        List<String> errors = validator.validate(dataList);
        if (!errors.isEmpty()) {
            LOGGER.info("Found Service uses Wrong");
            LOGGER.info("errors: " + errors);

            ItemHistoryErrorResponse errorResponse = new ValidationErrors();
            return errorResponse.getResponse(errors);
        }
        //END: do valid request data. Now req payload is valid


        CreateContext createContext = new CreateContext();
        Context context = null;
        try {
            context = createContext.getAdminContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IResponse responseBuilder = new CustomResponseBuilder();
        List<String> exceptions = new ArrayList<>();
        Status STATUS = Status.OK;
        List<Data> newStrongDataList = new ArrayList<>();
        //fetch items history by physicalid
        ItemsBasicDetailsFetcher fetchByPID = new ItemsBasicDetailsFetcherByPID(constraint.getHistoryOrder());
        try {
            newStrongDataList.addAll(fetchByPID.fetch(dataList, context));
        } catch (Exception e) {
            e.printStackTrace();
            exceptions.add(e.getMessage());
        }

        //fetch items details by TNR
        ItemsBasicDetailsFetcher fetchByTNR = new ItemsBasicDetailsFetcherByTNR(constraint.getHistoryOrder());
        try {
            newStrongDataList.addAll(fetchByTNR.fetch(dataList, context));
        } catch (Exception e) {
            e.printStackTrace();
            exceptions.add(e.getMessage());
        }

        responseBuilder.setData(newStrongDataList).setStatus(Status.OK);
        if (!exceptions.isEmpty()) {
            responseBuilder.setStatus(Status.FAILED).setErrorMessage(new ArrayList<Object>(exceptions));
            return new ResponseEntity<>(responseBuilder.buildResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseBuilder.buildResponse(), HttpStatus.OK);
    }
}
