package com.bjit.common.rest.app.service.controller.extension;

/**
 *
 * @author BJIT
 */
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.extension.ResponseMessageExtensionBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.Vault;
import matrix.util.MatrixException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

@Controller
public class AddExtensionController {

    private static final org.apache.log4j.Logger ADD_EXTENSION_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(AddExtensionController.class);

    @RequestMapping(value = "/addExtensionToObject", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity AddExtensionController(HttpServletRequest httpRequest,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "rev", required = false) String rev,
            @RequestParam(value = "objectId", required = false) String objectId,
            @RequestParam(value = "extensionList", required = false) String extensionList
    ) {
        ADD_EXTENSION_CONTROLLER_LOGGER.debug("---------------------------------------- ||| ADD EXTENSION CONTROLLER BEGIN ||| ----------------------------------------");
        ADD_EXTENSION_CONTROLLER_LOGGER.debug("#############################################################################################################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        String buildResponse = "";

        try {
            List<String> requestedExtensionList = null;
            if (NullOrEmptyChecker.isNullOrEmpty(extensionList)) {
                throw new Exception("Invalid extensionList parameter in Request");
            }
            requestedExtensionList = new ArrayList<>(Arrays.asList(extensionList.split(",")).stream().map(extensionName -> extensionName.trim()).collect(Collectors.toList()));

            Context context = (Context) httpRequest.getAttribute("context");


            BusinessObject businessObject = null;
            if (!NullOrEmptyChecker.isNullOrEmpty(type) && !NullOrEmptyChecker.isNullOrEmpty(name) && !NullOrEmptyChecker.isNullOrEmpty(rev)) {
                businessObject = new BusinessObject(type, name, rev, "");
            } else {
                throw new Exception("Invalid type or name or rev parameter in Request");
            }

            List<ResponseMessageExtensionBean> successfulExtenstionList = new ArrayList<>();
            List<ResponseMessageExtensionBean> erorExtenstionList = new ArrayList<>();

            BusinessInterfaceList businessInterfaceList = businessObject.getBusinessInterfaces(context, true);
            if (!NullOrEmptyChecker.isNullOrEmpty(businessInterfaceList)) {
                for (BusinessInterface businessInterface : businessInterfaceList) {
                    String interfaceName = businessInterface.getName();
                    if (requestedExtensionList.contains(interfaceName)) {
                        requestedExtensionList.remove(interfaceName);
                        ResponseMessageExtensionBean extension = new ResponseMessageExtensionBean();
                        extension.setExtension(interfaceName);
                        successfulExtenstionList.add(extension);
                    }
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(requestedExtensionList)) {
                Vault vault = new Vault("");
                for (String requestedExtension : requestedExtensionList) {
                    try {
                        BusinessInterface businessInterface = new BusinessInterface(requestedExtension, vault);
                        businessObject.addBusinessInterface(context, businessInterface);
                        ResponseMessageExtensionBean extension = new ResponseMessageExtensionBean();
                        extension.setExtension(requestedExtension);
                        successfulExtenstionList.add(extension);
                    } catch (MatrixException e) {
                        ResponseMessageExtensionBean extension = new ResponseMessageExtensionBean();
                        extension.setExtension(requestedExtension);
                        extension.setErrorMessage(e.toString());
                        erorExtenstionList.add(extension);
                    }
                }
            }

            if (erorExtenstionList.size() > 0) {
                if (successfulExtenstionList.size() > 0) {
                    responseBuilder.addErrorMessage(erorExtenstionList).setData(successfulExtenstionList).setStatus(Status.FAILED);
                } else {
                    responseBuilder.addErrorMessage(erorExtenstionList).setStatus(Status.FAILED);
                }
            } else if (successfulExtenstionList.size() > 0) {
                responseBuilder.setData(successfulExtenstionList).setStatus(Status.OK);
            } else {
                throw new RuntimeException("System error occurred!");
            }
            buildResponse = responseBuilder.buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        } catch (MatrixException e) {
            responseBuilder.addErrorMessage(e.getMessage()).setStatus(Status.FAILED);
            buildResponse = responseBuilder.buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } catch (Exception e) {
            responseBuilder.addErrorMessage(e.getMessage()).setStatus(Status.FAILED);
            buildResponse = responseBuilder.buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            ADD_EXTENSION_CONTROLLER_LOGGER.debug("---------------------------------------- ||| ADD EXTENSION CONTROLLER END ||| ----------------------------------------");
            ADD_EXTENSION_CONTROLLER_LOGGER.debug("###########################################################################################################################\n");
        }
    }
}
