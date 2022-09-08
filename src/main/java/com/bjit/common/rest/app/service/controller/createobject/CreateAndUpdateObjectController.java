/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createobject;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.createobject.UpdateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import matrix.db.BusinessObject;
import matrix.db.JPO;
import org.apache.log4j.Level;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author BJIT / Md. Omour Faruq
 */
@Controller
@RequestMapping(path = "/createAndUpdateObject")
public class CreateAndUpdateObjectController {

    private static final String TYPE_PATTERN = "template.object.type";
    private static final String CAD_TYPE_PATTERN = "discipline.cat.type.object";

    public static Map<String, String> PACKAGE_MAP;
    public static Map<String, String> TYPE_MAP;
    HashMap<String, String> updateAttributeResultMap;

    private static final org.apache.log4j.Logger CREATE_AND_UPDATE_OBJECT_LOGGER = org.apache.log4j.Logger.getLogger(CreateAndUpdateObjectController.class);

    /**
     * Takes the header variables from the request. Create context from the
     * header values. Creates auto-name if isAutoName property has true value.
     * Or tries to set the name if it is given in the name property. If both are
     * absent then it raise an exception. Searches for the Cad type and
     * Discipline in "PACKAGE_MAP". If found then it calls
     * getAutoNameOfManObjectType or getAutoNameJpo method in CloneObjectUtil
     * JPO for auto-name operation. Then it populates the JPO variables and
     * clones by calling cloneObject method of emxWorkspaceVault JPO by using
     * the template id given in JSON format and updates the attributes of the
     * newly created object manually by calling updateObjectAttribute method of
     * CLoneObjectUtils.
     *
     * @param httpRequest Headers variables has taken from the request
     * @param createObjectBean JSON object is taken from
     * @return If processed then returns the cloned object id. If failed returns
     * the error message to the client
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/createObject", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity createObject(HttpServletRequest httpRequest, @RequestBody CreateObjectBean createObjectBean) throws Exception {
        CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "######################################## CREATE OBJECT CONTROLLER BEGIN ########################################\n");

        IResponse responseBuilder = new CustomResponseBuilder();
        Context context = null;
        String clonedObjectId;
        TNR tnr;

        try {

            if (createObjectBean.getIsAutoName() == null) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "'isAutoName' field is null");
                String buildResponse = responseBuilder.addErrorMessage("Please provide mandatory fields correctly").setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.EXPECTATION_FAILED);
            }

            context = (Context) httpRequest.getAttribute("context");
            BusinessObject clonedBusinessObject;

            try {
                System.out.println("\n");
                System.out.println("----------- ||| ----------- Object Cloning Process Has Been Started ----------- ||| -----------");
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Creating Object");

                HashMap objectCloneParametersMap;

                try {
                    createObjectBean = ifSkeletonIdNotExistThenCheckTNRsProperties(createObjectBean, context);
                    createObjectBean = checkObjectsExistence(context, createObjectBean);
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Template Object Id : " + createObjectBean.getTemplateBusinessObjectId());

                    objectCloneParametersMap = createObjectCloneParametersMap(context, createObjectBean);

                    /*---------------------------------------- ||| Start Transaction Clone Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Starting Transaction");
                    ContextUtil.startTransaction(context, true);

                    clonedObjectId = cloneObjectByJPO(context, createObjectBean, objectCloneParametersMap);
                    clonedBusinessObject = new BusinessObject(clonedObjectId);

                    tnr = setTNRProperties(context, clonedObjectId);

                } catch (Exception exp) {

                    /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                    ContextUtil.abortTransaction(context);

                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
                    String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.EXPECTATION_FAILED);
                }

                try {

                    clonedBusinessObject.open(context);
                    HashMap<String, String> businessObjectAttributes = validateDefaultAttributes(createObjectBean, clonedBusinessObject);
                    checkErrorInUpdatingAttributes(context, clonedBusinessObject, tnr, businessObjectAttributes, clonedObjectId);
                    clonedBusinessObject.close(context);
                } catch (Exception exp) {
                    throw exp;
                }
            } catch (NullPointerException exp) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.FATAL, exp.getMessage());
                exp.printStackTrace(System.out);

                /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                ContextUtil.abortTransaction(context);

                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.EXPECTATION_FAILED);
            } catch (Exception exp) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.FATAL, exp.getMessage());
                exp.printStackTrace(System.out);

                /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                ContextUtil.abortTransaction(context);

                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.EXPECTATION_FAILED);
            }

            System.out.println("----------- ||| ----------- Object Cloning Process Has Been Completed ----------- ||| -----------");
            System.out.println("\n");

            try {
                /*---------------------------------------- ||| Commit Transaction Clone Business Object||| ----------------------------------------*/
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Committing Transaction");
                ContextUtil.commitTransaction(context);

                String buildResponse = responseBuilder.setData(tnr).addNewProperty("objectId", clonedObjectId).setStatus(Status.OK).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } catch (FrameworkException exp) {
                /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                ContextUtil.abortTransaction(context);
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");

                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception exp) {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            ContextUtil.abortTransaction(context);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
            String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "######################################## CREATE OBJECT CONTROLLER COMPLETE ########################################");
        }
    }

    public CreateObjectBean checkObjectsExistence(Context context, CreateObjectBean createObjectBean) throws Exception {

        if (!createObjectBean.getIsAutoName()) {

            TNR tnr = createObjectBean.getTnr();

            if (tnr == null) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Please set Type Name Revision (TNR)");
                throw new Exception("Please set Type Name Revision (TNR)");
            }

            if (tnr.getName() == null || tnr.getName().equalsIgnoreCase("")) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Please set 'auto-name' true or set the 'Name' of the object");
                throw new Exception("Please set 'auto-name' true or set the 'Name' of the object");
            }

            String preObjectId = getUniqueObjectId(context, tnr.getType(), tnr.getName());
            System.out.println("JPO check: " + preObjectId);
            if (preObjectId != null) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Object with this type name already present");
                throw new Exception("Object with this name already present");
            }
        }
        return createObjectBean;
    }

    public CreateObjectBean ifSkeletonIdNotExistThenCheckTNRsProperties(CreateObjectBean createObjectBean, Context context) throws Exception {
        TNR tnr = createObjectBean.getTnr();

        if (tnr == null) {

            if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTemplateBusinessObjectId())) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Please set Type Name Revision (TNR)");
                throw new Exception("Please set Type Name Revision (TNR)");
            } else {
                BusinessObject templateObject = new BusinessObject(createObjectBean.getTemplateBusinessObjectId());
                templateObject.open(context);
                tnr = new TNR();
                tnr.setType(templateObject.getTypeName());
                createObjectBean.setTnr(tnr);

                return createObjectBean;
            }

        }

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTemplateBusinessObjectId())) {

            if (tnr.getType() == null || tnr.getType().equalsIgnoreCase("")) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Please set Type Name Revision (TNR)");
                throw new Exception("Please set the type of the object");
            }

            populateTypeMap();

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "These are the types" + TYPE_MAP);

            if (TYPE_MAP.get(tnr.getType()) == null || TYPE_MAP.get(tnr.getType()).equalsIgnoreCase("")) {

                StringBuilder allowedTypeBuilder = new StringBuilder();

                TYPE_MAP.forEach((key, value) -> {
                    allowedTypeBuilder.append("'").append(key).append("'").append(",").append(" ");
                });
                String allowedTypes = "Please set type of the object one of the followings : " + allowedTypeBuilder.toString().replaceAll(", $", "");

                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, allowedTypes);
                throw new Exception(allowedTypes);
            }

            createObjectBean.setTemplateBusinessObjectId(TYPE_MAP.get(tnr.getType()));
        }
        return createObjectBean;
    }

    private HashMap<String, String> validateDefaultAttributes(CreateObjectBean createObjectBean, BusinessObject businessObject) {
        HashMap<String, String> businessObjectAttributes = createObjectBean.getAttributes();

        if (businessObjectAttributes == null) {
            businessObjectAttributes = new HashMap<>();
        }

        if (!businessObjectAttributes.containsKey("PLMEntity.V_Name")) {
            businessObjectAttributes.put("PLMEntity.V_Name", businessObject.getName());
        }

        if (!businessObjectAttributes.containsKey("PLMEntity.PLM_ExternalID")) {
            businessObjectAttributes.put("PLMEntity.PLM_ExternalID", businessObject.getName());
        }

        if (!businessObjectAttributes.containsKey("PLMEntity.V_discipline")) {
            businessObjectAttributes.put("PLMEntity.V_discipline", businessObject.getTypeName());
        }

        return businessObjectAttributes;
    }

    public HashMap createObjectCloneParametersMap(Context context, CreateObjectBean createObjectBean) throws Exception {
        try {
            HashMap objectCloneParametersMap = new HashMap();

            HashMap folderIdForJPOSourceMap = new HashMap();
            String newFolderId = (createObjectBean.getFolderId() == null || createObjectBean.getFolderId().equalsIgnoreCase("")) ? "" : createObjectBean.getFolderId();

            System.out.println("new Folder Id is : " + newFolderId);

            folderIdForJPOSourceMap.put("FolderId", newFolderId);
            objectCloneParametersMap.put("SpecificationMap", folderIdForJPOSourceMap);

            HashMap<String, String> attributeGlobalReadMap = new HashMap<>();
            String attributeGlobalRead = createObjectBean.getAttributeGlobalRead() == null ? "false" : createObjectBean.getAttributeGlobalRead().toString();
            attributeGlobalReadMap.put("attribute_GlobalRead", attributeGlobalRead);
            objectCloneParametersMap.put("AttributeMap", attributeGlobalReadMap);

            System.out.println("attributeGlobalRead : " + attributeGlobalRead);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "AttributeMap : " + attributeGlobalReadMap);

            String objectType = createObjectBean.getTnr().getType();

            if (objectType == null || objectType.equals("")) {
                try {
                    BusinessObject templateObject = new BusinessObject(createObjectBean.getTemplateBusinessObjectId());
                    templateObject.open(context);
                    objectType = templateObject.getTypeName();
                } catch (MatrixException exp) {
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
                    throw new Exception("'Skeleton-id' may be wrong. Please provide a valid 'skeleton-id'");
                }
            }

            if (createObjectBean.getIsAutoName()) {
                objectCloneParametersMap.put("Name", generateObjectName(context, objectType, createObjectBean.getTemplateBusinessObjectId()));
            } else {
                String objectName = createObjectBean.getTnr().getName();
                if (!NullOrEmptyChecker.isNullOrEmpty(objectName)) {
                    objectCloneParametersMap.put("Name", objectName);
                } else {
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Object name has not been provided and also value of auto-name field is false. System could not create an object without name. Please try to provide one of them");
                    throw new Exception("Please provide 'Type', 'Name' properly");
                }
            }

            if (NullOrEmptyChecker.isNullOrEmpty(objectType)) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Object type has not been provided. System could not create an object without type. Please try to provide type of object");
                throw new Exception("Please provide 'Type', 'Name' properly");
            }

            objectCloneParametersMap.put("Type", objectType);

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Type : " + objectType);
            return objectCloneParametersMap;
        } catch (Exception exp) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
            throw exp;
        }
    }

    public String cloneObjectByJPO(Context context, CreateObjectBean createObjectBean, HashMap objectCloneParametersMap) throws Exception {
        HashMap objectCreationParamatersMap = new HashMap();

        objectCreationParamatersMap.put("objectId", createObjectBean.getTemplateBusinessObjectId());
        objectCreationParamatersMap.put("paramList", objectCloneParametersMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getCs())) {
            objectCreationParamatersMap.put("collaborationSpace", getCollaborationSpace(createObjectBean));
        }

        String initargs[] = {};
        String clonedObjectId;
        try {
            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "cloneObject";

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "JPO class name : " + jpoClassName);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "JPO method name : " + jpoMethodName);

            clonedObjectId = (String) JPO.invoke(context, jpoClassName, initargs,
                    jpoMethodName, JPO.packArgs(objectCreationParamatersMap), String.class);

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Cloned object Id : " + clonedObjectId);
            return clonedObjectId;
        } catch (MatrixException exp) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.TRACE, exp);
            exp.printStackTrace(System.out);
            throw new Exception(exp.getMessage());
        }
    }

    private void updateCurrentProperty(Context context, BusinessObject clonedBusinessObject, HashMap<String, String> businessObjectAttributes) throws MatrixException {

        try {
            String objectCurrentStatus = businessObjectAttributes.containsKey("current") ? businessObjectAttributes.get("current") : "IN_WORK";
            clonedBusinessObject.current(context, objectCurrentStatus);
        } catch (MatrixException ex) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, ex);
            throw ex;
        }
    }

    private void updateCurrentProperty(Context context, BusinessObject clonedBusinessObject) throws MatrixException {

        try {
            clonedBusinessObject.current(context, "IN_WORK");
        } catch (MatrixException ex) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, ex);
            throw ex;
        }
    }

    private String getCollaborationSpace(CreateObjectBean createObjectBean) throws MatrixException {
        try {
            String role = createObjectBean.getCs();
            String[] roleParts = role.split("\\.");

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Role Parts are : " + Arrays.toString(roleParts));
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Role Parts length : " + roleParts.length);
            String rolePart = roleParts[roleParts.length - 1];
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Role Part is : " + rolePart);

            return rolePart;

        } catch (Exception ex) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, ex.getMessage());
            throw ex;
        }
    }

    @RequestMapping(value = "/updateObject", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateObject(HttpServletRequest httpRequest, @RequestBody UpdateObjectBean updateObjectBean) throws MalformedURLException, MatrixException {
        CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "######################################## UPDATE OBJECT CONTROLLER BEGIN ########################################\n");

        Context context = null;
        IResponse responseBuilder = new CustomResponseBuilder();
        TNR tnr;
        try {
            try {
                context = (Context) httpRequest.getAttribute("context");
            } catch (Exception exp) {
                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }

            try {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "----------- ||| ----------- Object Update Process Has Been Started ----------- ||| -----------");
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Update Object Attributes");

                String businessObjectId = getBusinessObjectId(context, updateObjectBean);
                tnr = setTNRProperties(context, businessObjectId);

                HashMap<String, String> attributeListMap = updateObjectBean.getAttributeListMap();
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Attributes list map " + attributeListMap);

                if (attributeListMap == null || attributeListMap.size() < 1) {
                    String buildResponse = responseBuilder.addErrorMessage("There are not attributes for update.").setStatus(Status.FAILED).buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }

                if (!NullOrEmptyChecker.isNullOrEmpty(businessObjectId)) {

                    /*---------------------------------------- ||| Start Transaction Update Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Starting Transaction");
                    ContextUtil.startTransaction(context, true);

                    List<Object> updateAttributes = updateAttributesByMQLCommand(context, businessObjectId, updateObjectBean.getAttributeListMap());

                    HashMap<String, String> updateAttributesErrorResultData = (HashMap<String, String>) updateAttributes.get(1);

                    if (updateAttributesErrorResultData.size() > 0) {
                        /*---------------------------------------- ||| Abort Transaction Update Business Object||| ----------------------------------------*/
                        CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                        ContextUtil.abortTransaction(context);

                        String buildResponse = responseBuilder.addErrorMessage(updateAttributesErrorResultData).setStatus(Status.FAILED).buildResponse();
                        return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                    }
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Attribute update process completed successfully");
                } else {
                    /*---------------------------------------- ||| Abort Transaction Update Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                    ContextUtil.abortTransaction(context);

                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Attributes of object update process has failed. Id of business object " + businessObjectId);
                }

            } catch (Exception exp) {
                /*---------------------------------------- ||| Abort Transaction Update Business Object||| ----------------------------------------*/
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                ContextUtil.abortTransaction(context);

                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.FATAL, exp.getMessage());

                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.EXPECTATION_FAILED);

            }
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "----------- ||| ----------- Object Update Process Has Been Completed ----------- ||| -----------");
            System.out.println("\n");
            try {
                /*---------------------------------------- ||| Commit Transaction Update Business Object||| ----------------------------------------*/
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Committing Transaction");
                ContextUtil.commitTransaction(context);

                String buildResponse = responseBuilder.setData(tnr).setStatus(Status.OK).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.OK);
            } catch (FrameworkException exp) {
                String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }

        } catch (Exception exp) {
            /*---------------------------------------- ||| Abort Transaction Update Business Object||| ----------------------------------------*/
            ContextUtil.abortTransaction(context);
            String buildResponse = responseBuilder.addErrorMessage(exp.getMessage()).setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
        } finally {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "######################################## UPDATE OBJECT CONTROLLER COMPLETE ########################################");
        }
    }

    /**
     *
     * @param context Context of a user with credentials on specific environment
     * @param objectId Business object id
     * @param objectBeanAttributes Attributes of the Business object
     * @return
     * @throws Exception
     */
    public List<Object> updateAttributesByJPO(Context context, String objectId, HashMap<String, String> objectBeanAttributes) throws Exception {
        List<Object> result = null;

        try {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Updating attributes of a business object. Id : " + objectId);

            if (objectBeanAttributes.containsKey("PLMEntity.V_nature")) {
                objectBeanAttributes.remove("PLMEntity.V_nature");
                throw new Exception("PLMEntity.V_nature is not updatable attribute");
            }

            HashMap attributeUpdateParamertes = new HashMap();
            attributeUpdateParamertes.put("objectId", objectId);
            attributeUpdateParamertes.put("attributeMap", objectBeanAttributes);

            String jpoClassName = "CloneObjectUtil";
            //String jpoMethodName = "updateObjectAttribute";
            String jpoMethodName = "updateObjectMultiAttribute";

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "JPO class name : " + jpoClassName);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "JPO method name : " + jpoMethodName);

            String[] constructor = {null};
            result = (List<Object>) JPO.invoke(context, jpoClassName, constructor, jpoMethodName, JPO.packArgs(attributeUpdateParamertes), List.class);
            updateAttributeResultMap = (HashMap<String, String>) result.get(1);

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Updating attributes of a business object. Id : " + objectId + " completed successfully. Result " + result);
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.TRACE, exp);
            throw exp;
        }
        return result;
    }

    /**
     *
     * @param context Context of a user with credentials on specific environment
     * @param objectId Business object id
     * @param objectBeanAttributes Attributes of the Business object
     * @return
     * @throws Exception
     */
    public List<Object> updateAttributesByMQLCommand(Context context, String objectId, HashMap<String, String> objectBeanAttributes) throws Exception {
        List<Object> result = null;

        try {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Updating attributes of a business object. Id : " + objectId);

            if (objectBeanAttributes.containsKey("PLMEntity.V_nature")) {
                objectBeanAttributes.remove("PLMEntity.V_nature");
                throw new Exception("PLMEntity.V_nature is not updatable attribute");
            }

            List<Object> errorList = new ArrayList();
            errorList.add(objectId);

            StringBuilder updateAttributes = new StringBuilder();

            JSON json = new JSON(true, true);

            BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();

            HashMap<Object, String> errorMap = new HashMap();

            StringBuilder modQueryBuilder = new StringBuilder();
            modQueryBuilder.append("escape modify bus ").append(objectId).append(" ");

            objectBeanAttributes.forEach((String key, String value) -> {
                Boolean hasEscapeSequenceCharacters = businessObjectOperations.hasEscapeSequenceCharacters(objectBeanAttributes.get(key));
                value = hasEscapeSequenceCharacters ? json.serialize(value) : value;

                if (hasEscapeSequenceCharacters) {
                    modQueryBuilder.append("\"").append(key).append("\"").append(" ").append(value).append(" ");
                } else {
                    modQueryBuilder.append("\"").append(key).append("\"").append(" ").append("\"").append(value).append("\" ");
                }
            });

            String updateQuery = modQueryBuilder.toString();

            try {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Modify Query : " + updateQuery);
                //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
                String queryResult = MqlUtil.mqlCommand(context, updateQuery);
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Returned Result : " + queryResult);
                businessObjectOperations.checkWarningInExecutingQuery(queryResult);
            } catch (Exception exp) {
                String exception = exp.getMessage();
                if (exception.contains(": ")) {
                    try {
                        String[] onlyCause = exception.split(": ");
                        errorMap.put("Error", onlyCause[onlyCause.length - 1]);
                    } catch (Exception ex) {
                        errorMap.put("Error", "Error message not found");
                    }
                }
            } finally {
                updateAttributes.setLength(0);
            }

            errorList.add(errorMap);
            errorList.add(updateQuery);

            result = errorList;

            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Updating attributes of a business object. Id : " + objectId + " completed successfully. Result " + result);
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, exp.getMessage());
            throw exp;
        }
        return result;
    }

    public String generateObjectName(Context context, String objectType, String templateObjectId) throws MatrixException, Exception {

        CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been started");

        populatePackageMapMap();

        String typePackage = PACKAGE_MAP.get(objectType);
        String autoName;

        try {
            if (typePackage == null) {
                String initargs[] = {};
                HashMap jpoParameters = new HashMap();
                jpoParameters.put("objectId", templateObjectId);
                autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameJpo", JPO.packArgs(jpoParameters), String.class);

                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been completed");
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Auto generated name is : " + autoName);
                return autoName;
            }

            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("type", objectType);
            jpoParameters.put("typePackage", typePackage);
            autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(jpoParameters), String.class);
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been completed");
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.DEBUG, "Auto generated name is : " + autoName);
            return autoName;
        } catch (MatrixException exp) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, "Error occured during autoname creation process");
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.TRACE, exp);
            throw exp;
        }

    }

    public String getUniqueObjectId(Context context, String type, String name) throws MatrixException, Exception {

        String[] constructor = {null};
        HashMap params = new HashMap();
        params.put("name", name);
        params.put("type", type);

        String objectId = (String) JPO.invoke(context, "CloneObjectUtil", constructor, "searchByTypeName", JPO.packArgs(params), String.class);
        if (objectId.equals("")) {
            return null;
        } else {
            return objectId;
        }
    }

    private void populateTypeMap() {
        if (TYPE_MAP == null || TYPE_MAP.size() < 1) {
            TYPE_MAP = PropertyReader.getProperties(TYPE_PATTERN, true);
        }
    }

    private void populatePackageMapMap() {
        if (PACKAGE_MAP == null || PACKAGE_MAP.size() < 1) {
            PACKAGE_MAP = PropertyReader.getProperties(CAD_TYPE_PATTERN, true);
        }
    }

    private void checkTnrProperty(TNR tnr) {
        String tnrError = "Please set 'object-id' or 'Type', 'Name', 'Revision' (TNR) properly";
        if (tnr == null) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, tnrError);
            throw new NullPointerException(tnrError);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(tnr.getType()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getName()) || NullOrEmptyChecker.isNullOrEmpty(tnr.getRevision())) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, tnrError);
            throw new NullPointerException(tnrError);
        }
    }

    private String getBusinessObjectId(Context context, UpdateObjectBean updateObjectBean) throws MatrixException {
        TNR tnr;
        String businessObjectId = updateObjectBean.getBusinessObjectId();

        if (NullOrEmptyChecker.isNullOrEmpty(businessObjectId)) {
            tnr = updateObjectBean.getTnr();

            checkTnrProperty(tnr);
            populateTypeMap();

            String vault = "";
            if (!NullOrEmptyChecker.isNullOrEmpty(TYPE_MAP.get(tnr.getType()))) {
                BusinessObject businessObject = new BusinessObject(TYPE_MAP.get(tnr.getType()));
                businessObject.open(context);

                vault = businessObject.getVault();
            } else {
                vault = "vplm";
            }

            try {
                BusinessObject updateAbleBusinessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), vault);
                updateAbleBusinessObject.open(context);
                businessObjectId = updateAbleBusinessObject.getObjectId();

            } catch (MatrixException exp) {
                String businessObjectIdFetchingError = exp.getMessage() + " May be invalid 'Type', 'Name', 'Revision' (TNR) information";
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, businessObjectIdFetchingError);
                throw new MatrixException(businessObjectIdFetchingError);
            }
        }
        return businessObjectId;
    }

    private TNR setTNRProperties(Context context, String businessObjectId) throws MatrixException {
        try {
            BusinessObject businessObject = new BusinessObject(businessObjectId);
            businessObject.open(context);

            TNR tnr = new TNR();
            tnr.setName(businessObject.getName());
            tnr.setType(businessObject.getTypeName());
            tnr.setRevision(businessObject.getRevision());

            return tnr;
        } catch (MatrixException exp) {
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
            throw exp;
        }
    }

    private void checkErrorInUpdatingAttributes(Context context, BusinessObject clonedBusinessObject, TNR tnr, HashMap<String, String> businessObjectAttributes, String clonedObjectId) throws Exception {
        if (!NullOrEmptyChecker.isNullOrEmpty(clonedObjectId)) {
            try {
                /*---------------------------------------- ||| Updating Attributes ||| ----------------------------------------*/
                String errorMessage;
                List<Object> updateAttributes = updateAttributesByJPO(context, clonedObjectId, businessObjectAttributes);
                //updateCurrentProperty(context, clonedBusinessObject);
                updateCurrentProperty(context, clonedBusinessObject, businessObjectAttributes);

                if (this.updateAttributeResultMap.size() > 0) {

                    /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                    ContextUtil.abortTransaction(context);

                    errorMessage = tnr.getName() + " object creation failed. Error occurred on updating object's attributes. " + this.updateAttributeResultMap;
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, errorMessage);
                    throw new Exception(errorMessage);
                }

                if (updateAttributes == null) {
                    errorMessage = "Attributes of business object " + tnr.getName() + " has failed to update.";
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, errorMessage);

                    /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
                    CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
                    ContextUtil.abortTransaction(context);
                    throw new Exception(errorMessage);
                }
            } catch (Exception exp) {
                CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
                throw exp;
            }

        } else {
            /*---------------------------------------- ||| Abort Transaction Clone Business Object||| ----------------------------------------*/
            CREATE_AND_UPDATE_OBJECT_LOGGER.log(Level.INFO, "Aborting Transaction");
            ContextUtil.abortTransaction(context);
            throw new Exception("Object cloning process has failed. Cloned Object Id " + clonedObjectId);
        }
    }

    public Boolean hasEscapeSequenceCharacters(String value) {
        Pattern p = Pattern.compile("\"|\'|\\n|\\f|\\r|\\t");
        Matcher matcher = p.matcher(value);

        return matcher.find();
    }

    private void checkWarningInExecutingQuery(String queryResult) {
        String warningPattern = "Warning: #1900075:|Warning: #1500218:|Business object has no attribute|at end of command ignored";

        Pattern pattern = Pattern.compile(warningPattern);
        Matcher matcher = pattern.matcher(queryResult);
        if (matcher.find()) {
            throw new RuntimeException(queryResult);
        }
    }
}
