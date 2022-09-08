/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.bjit.common.rest.app.service.constants.AutoNameTypePolicyConstants;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;

import matrix.db.*;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT / Omour Faruq
 */
public class BusinessObjectOperations {

    private static final org.apache.log4j.Logger BUSINESS_OBJECT_OPERATIONS_LOGGER = org.apache.log4j.Logger.getLogger(BusinessObjectOperations.class);
    private BusinessObjectUtility businessObjectUtility;
    public static Map<String, String> DISCIPLINE_MAP;
    private static final String OBJECT_DISCIPLINE_PATTERN = "discipline.cat.type.object";
    
    public Boolean checkName = Boolean.TRUE;
    public Boolean checkRevision = Boolean.TRUE;

    public BusinessObjectUtility getBusinessObjectUtility() {
        return businessObjectUtility;
    }

    public BusinessObject getObject(String objectId) throws MatrixException {

        try {
            businessObjectUtility.checkObjectId(objectId);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Object id : " + objectId);

            BusinessObject businessObject = new BusinessObject(objectId);
            return businessObject;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }

    }

    public BusinessObject getObject(TNR tnr, String vault, Boolean checkRevision) throws MatrixException {
        try {
            validateTNR(tnr, checkRevision);
            businessObjectUtility.checkVault(vault);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Object type : " + tnr.getType() + " name : " + tnr.getName() + " revision : " + tnr.getRevision() + " vault : " + vault);
            BusinessObject businessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), vault);
            return businessObject;

        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String getObjectId(Context context, BusinessObject businessObject) throws MatrixException {
        try {
            businessObjectUtility.checkContext(context);
            businessObjectUtility.checkObject(businessObject);

            businessObject.open(context);
            String objectId = businessObject.getObjectId();
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Object Id : " + objectId);

            return objectId;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            businessObject.close(context);
        }
    }

    public String getObjectId(Context context, TNR tnr, String vault) throws MatrixException {
        try {
            BusinessObject object = getObject(tnr, vault, Boolean.TRUE);
            String objectId = getObjectId(context, object);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Object Id : " + objectId);

            return objectId;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public TNR getObjectTNR(Context context, BusinessObject businessObject) throws MatrixException {
        try {
            businessObjectUtility.checkContext(context);
            businessObjectUtility.checkObject(businessObject);

            businessObject.open(context);
            TNR tnr = new TNR();

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Object Type : " + businessObject.getTypeName() + " Name : " + businessObject.getName() + " Revision : " + businessObject.getRevision());

            tnr.setType(businessObject.getTypeName());
            tnr.setName(businessObject.getName());
            tnr.setRevision(businessObject.getRevision());

            return tnr;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } finally {
            businessObject.close(context);
        }
    }

    public TNR getObjectTNR(Context context, String objectId) throws MatrixException {
        try {
            businessObjectUtility.checkContext(context);
            businessObjectUtility.checkObjectId(objectId);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Object Id : " + objectId);

            BusinessObject object = getObject(objectId);
            TNR tnr = getObjectTNR(context, object);
            return tnr;

        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void validateTNR(TNR tnr) {
        String errorMessage;
        if (NullOrEmptyChecker.isNull(tnr)) {

            errorMessage = "TNR is Null";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(tnr.getType())) {

            errorMessage = "Type is Null or Empty";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

    }

    public void validateTNR(TNR tnr, Boolean checkName) {
        validateTNR(tnr);
        String errorMessage;

        if (checkName) {
            if (NullOrEmptyChecker.isNullOrEmpty(tnr.getName())) {

                errorMessage = "Error in Type : '" + tnr.getType() + "' where 'Name' is Null or Empty";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }
        }
    }

    public void validateTNR(TNR tnr, Boolean checkName, Boolean checkRevision) {
        validateTNR(tnr, checkName);
        String errorMessage;

        if (checkRevision) {
            if (NullOrEmptyChecker.isNullOrEmpty(tnr.getRevision())) {

                errorMessage = "Error in Type : '" + tnr.getType() + "' and Name : '" + tnr.getName() + "' where 'Revision' is Null or Empty";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }
        }
    }

    public void validateTNR(String type, String name, String revision, Boolean checkName) {

        TNR tnr = new TNR();
        tnr.setType(type);
        tnr.setName(name);
        tnr.setRevision(revision);

        validateTNR(tnr, checkName);
    }

    public void validateTNR(String type, String name, String revision, Boolean checkName, Boolean checkRevision) {

        TNR tnr = new TNR();
        tnr.setType(type);
        tnr.setName(name);
        tnr.setRevision(revision);

        validateTNR(tnr, checkName, checkRevision);
    }

    public String createObject(Context context, TNR tnr, String vault, String policy) throws MatrixException {
        validateTNR(tnr, checkName, checkRevision);
        if (NullOrEmptyChecker.isNullOrEmpty(vault)) {
            vault = "vplm";
        }

        try {
            BusinessObject businessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), vault);

            if (businessObject.exists(context)) {
                businessObject.open(context);
                String objectId = businessObject.getObjectId();
                businessObject.close(context);
                return objectId;
            }

            if (NullOrEmptyChecker.isNullOrEmpty(policy)) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error("Policy is missing for creating the object. " + tnr.toString());
                throw new NullPointerException("Policy is missing for creating the object. " + tnr.toString());
            }

            businessObject.create(context, policy);
            String objectId = businessObject.getObjectId();
            businessObject.close(context);
            return objectId;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String createObject(Context context, TNR objectTnr, String objectVault) throws MatrixException {
        return createObject(context, objectTnr, objectVault, null);
    }

    public String createObject(Context context, TNR objectTnr) throws MatrixException {
        return createObject(context, objectTnr, null);
    }

    public String cloneObject(Context context, CreateObjectBean createObjectBean, HashMap objectCloneParametersMap) throws Exception {
        businessObjectUtility.checkContext(context);
        String errorMessage;

        if (NullOrEmptyChecker.isNull(createObjectBean)) {

            errorMessage = "CreateObjectBean is Null";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNull(objectCloneParametersMap)) {

            errorMessage = "ObjectCloneParameterMap is Null";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (objectCloneParametersMap.isEmpty()) {

            errorMessage = "ObjectCloneParameterMap is Empty";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        HashMap objectCreationParamatersMap = new HashMap();

        objectCreationParamatersMap.put("objectId", createObjectBean.getTemplateBusinessObjectId());
        BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("TemplateObject Id : " + createObjectBean.getTemplateBusinessObjectId());

        objectCreationParamatersMap.put("paramList", objectCloneParametersMap);
        BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Parameter List : " + objectCloneParametersMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getCs())) {
            objectCreationParamatersMap.put("collaborationSpace", businessObjectUtility.getCollaborationSpace(createObjectBean.getCs()));
        }

        String initargs[] = {};
        String clonedObjectId;
        try {
            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "cloneObject";

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO class name : " + jpoClassName);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO method name : " + jpoMethodName);

            clonedObjectId = (String) JPO.invoke(context, jpoClassName, initargs,
                    jpoMethodName, JPO.packArgs(objectCreationParamatersMap), String.class);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Cloned object Id : " + clonedObjectId);
            return clonedObjectId;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public Boolean updateObject(Context context, TNR tnr, String vault, String propertyName, String propertyValue) throws FrameworkException, MatrixException, InterruptedException {
        try {
            BusinessObject object = getObject(tnr, vault, true);
            String objectId = getObjectId(context, object);
            updateObject(context, objectId, propertyName, propertyValue);

            return true;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (InterruptedException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public Boolean updateObject(Context context, String objectId, String propertyName, String propertyValue) throws FrameworkException, InterruptedException {

        try {
            businessObjectUtility.checkContext(context);
            businessObjectUtility.checkObjectId(objectId);

            if (NullOrEmptyChecker.isNullOrEmpty(propertyName)) {
                String errorMessage = "Property Name is Null or Empty";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Property Name : " + propertyName);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Property Value : " + propertyValue);

            String updateQuery = "mod bus " + objectId + " \"" + propertyName + "\" \"" + propertyValue + "\"";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Modify Query : " + updateQuery);

            //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
            String mqlCommand = MqlUtil.mqlCommand(context, updateQuery);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Returned Result : " + mqlCommand);

            return true;
        } catch (FrameworkException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }


	/**
     * Update item objects attribute values
     *
     * @param context
     * @param objectId
     * @param propertyMap
     * @return Boolean
     * @throws FrameworkException, InterruptedException, Exception
     */
    public Boolean updateObject(Context context, String objectId, HashMap<String, String> propertyMap) throws FrameworkException, InterruptedException, Exception {
        String errorMessage;
        JSON json = new JSON(true, true);

        try {
            businessObjectUtility.checkContext(context);
            businessObjectUtility.checkObjectId(objectId);

            if (NullOrEmptyChecker.isNull(propertyMap)) {
                errorMessage = "Attribute or Property Map is Null";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            if (propertyMap.isEmpty()) {
                errorMessage = "Attribute or Property Map is Empty";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Attribute or Property Map : " + propertyMap);

            StringBuilder modQueryBuilder = new StringBuilder();
            modQueryBuilder.append("escape modify bus ").append(objectId).append(" ");

            propertyMap.forEach((String key, String value) -> {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Enovia Attribute : " + key);
                Boolean hasEscapeSequenceCharacters = hasEscapeSequenceCharacters(value);
                value = hasEscapeSequenceCharacters ? json.serialize(value) : value;
                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("PDM Value : " + value);
                if (hasEscapeSequenceCharacters) {
                    modQueryBuilder.append("\"").append(key).append("\"").append(" ").append(value).append(" ");
                } else {
                    modQueryBuilder.append("\"").append(key).append("\"").append(" ").append("\"").append(value).append("\" ");
                }
            });

            String updateQuery = modQueryBuilder.toString();
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Modify Query : " + updateQuery);

            //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
            String queryResult = MqlUtil.mqlCommand(context, updateQuery);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Returned Result : " + queryResult);

            checkWarningInExecutingQuery(queryResult);

            return true;
        } catch (FrameworkException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public Boolean hasEscapeSequenceCharacters(String value) {
        Pattern p = Pattern.compile(PropertyReader.getProperty("pattern.escape.sequence"));
        Matcher matcher = p.matcher(value);

        return matcher.find();
    }

    public void addInterface(Context context, String objectId, String interfaceName, String vault) throws MatrixException {
        interfaceName = interfaceName.trim();
        try {
            BusinessObject businessObject = validateInterface(objectId, context, interfaceName);

            if (checkInterfaceAlreadyAdded(businessObject, context, interfaceName, objectId)) {
                return;
            }

            addInterfaceToTheBusinessObject(vault, interfaceName, businessObject, context);
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (RuntimeException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void addInterface(Context context, String objectId, String interfaceName, String vault, Boolean checkInterfaceExistence) throws MatrixException {
        interfaceName = interfaceName.trim();
        if (checkInterfaceExistence) {
            addInterface(context, objectId, interfaceName, vault);
        } else {
            try {
                BusinessObject businessObject = validateInterface(objectId, context, interfaceName);

                addInterfaceToTheBusinessObject(vault, interfaceName, businessObject, context);
            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (NullPointerException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            }
        }
    }

    public void addInterface(Context context, String objectId, List<String> interfaceFromMapList, String vault, Boolean checkInterfaceExistence) {
        try {
            businessObjectUtility.checkObjectId(objectId);
            BusinessObject businessObject = getObject(objectId);

            List<String> businessObjectInterfacesList = getBusinessObjectInterfaces(context, businessObject);

            interfaceFromMapList.replaceAll(String::trim);

            Collections.sort(interfaceFromMapList);
            Collections.sort(businessObjectInterfacesList);

            List<String> copyInterfaceFromMapList = interfaceFromMapList.stream().collect(Collectors.toList());

            interfaceFromMapList.removeAll(businessObjectInterfacesList);
            businessObjectInterfacesList.removeAll(copyInterfaceFromMapList);

            removeInterface(context, objectId, businessObjectInterfacesList, vault);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Interfaces " + interfaceFromMapList + " to be added");
            interfaceFromMapList.stream().parallel().forEach(interfaceName -> {
                try {
                    addInterfaceToTheBusinessObject(vault, interfaceName, businessObject, context);
                } catch (MatrixException exp) {
                    BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                    throw new RuntimeException(exp);
                } catch (NullPointerException exp) {
                    BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                    throw exp;
                } catch (RuntimeException exp) {
                    BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                    throw exp;
                } catch (Exception exp) {
                    BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                    throw exp;
                }
            });

        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (RuntimeException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void removeInterface(Context context, String objectId, String interfaceName, String vault) throws MatrixException {

        try {
            removeInterface(context, getObject(objectId), interfaceName, vault);
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void removeInterface(Context context, String objectId, List<String> interfaceList, String vault) {

        interfaceList.stream().parallel().forEach((String interfaceName) -> {
            try {
                removeInterface(context, getObject(objectId), interfaceName, vault);
            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });
    }

    public void removeInterface(Context context, TNR tnr, String interfaceName, String vault) throws MatrixException {

        try {
            removeInterface(context, getObject(tnr, "", Boolean.FALSE), interfaceName, vault);
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void removeInterface(Context context, TNR tnr, List<String> interfaceList, String vault) {
        interfaceList.stream().parallel().forEach((String interfaceName) -> {
            try {
                removeInterface(context, getObject(tnr, "", Boolean.FALSE), interfaceName, vault);
            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });
    }

    public void removeInterface(Context context, BusinessObject businessObject, String interfaceName, String vault) throws MatrixException {
        try {
            businessObject.open(context);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Removing interface '" + interfaceName + "' from '" + businessObject.getTypeName() + "' '" + businessObject.getName() + "' '" + businessObject.getRevision() + "' object");

            interfaceName = interfaceName.trim();
            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            businessObject.removeBusinessInterface(context, businessInterface);

            businessObject.close(context);
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void removeInterface(Context context, BusinessObject businessObject, List<String> interfaceList, String vault) {
        interfaceList.stream().parallel().forEach((String interfaceName) -> {
            try {
                removeInterface(context, businessObject, interfaceName, vault);
            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            }
        });
    }

    public BusinessObject changeObjectType(Context context, BusinessObject businessObject, String type, String name, String revision, String vault, String policy) throws MatrixException {
        businessObject.open(context);
        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Business object '" + businessObject.getTypeName() + "' '" + businessObject.getName() + "' '" + businessObject.getRevision() + "' is going to change into '" + type + "' '" + name + "' '" + revision + "' object");

        BusinessObject changedBusinessObject = businessObject.change(context, type, name, revision, vault, policy);

        businessObject.close(context);
        return changedBusinessObject;
    }

    public BusinessObject validateInterface(String objectId, Context context, String interfaceName) throws NullPointerException, MatrixException {
        businessObjectUtility.checkContext(context);
        businessObjectUtility.checkObjectId(objectId);
        BusinessObject businessObject = getObject(objectId);
        if (NullOrEmptyChecker.isNullOrEmpty(interfaceName)) {
            throw new NullPointerException("Interface is Null or Empty");
        }
        return businessObject;
    }

    public List<String> getBusinessObjectInterfaces(Context context, BusinessObject businessObject) throws MatrixException, RuntimeException {
        BusinessInterfaceList businessInterfaceList = businessObject.getBusinessInterfaces(context, true);
        List<String> businessObjectInterfaceList = new ArrayList<>();
        for (BusinessInterface businessObjectInterface : businessInterfaceList) {
            try {
                businessObjectInterface.open(context);
                String existingInterface = businessObjectInterface.getName();
                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Existing Interface : '" + existingInterface + "'");
                businessObjectInterfaceList.add(existingInterface);

            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            } catch (NullPointerException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } finally {
                businessObjectInterface.close(context);
            }
        }
        return businessObjectInterfaceList;
    }

    public boolean checkInterfaceAlreadyAdded(BusinessObject businessObject, Context context, String interfaceName, String objectId) throws MatrixException, RuntimeException {
        BusinessInterfaceList businessInterfaceList = businessObject.getBusinessInterfaces(context, true);
        for (BusinessInterface businessObjectInterface : businessInterfaceList) {
            try {
                businessObjectInterface.open(context);
                String existingInterface = businessObjectInterface.getName();
                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Existing Interface : '" + existingInterface + "'");
                if (existingInterface.equalsIgnoreCase(interfaceName)) {
                    BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("'" + interfaceName + "' has already added with the object '" + objectId + "'");
                    return true;
                }
            } catch (MatrixException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw new RuntimeException(exp);
            } catch (NullPointerException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (RuntimeException exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } catch (Exception exp) {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
                throw exp;
            } finally {
                businessObjectInterface.close(context);
            }
        }
        return false;
    }

    public void addInterfaceToTheBusinessObject(String vault, String interfaceName, BusinessObject businessObject, Context context) throws MatrixException {
        try {
            interfaceName = interfaceName.trim();
            Vault vaultObject = new Vault(vault);
            BusinessInterface businessInterface = new BusinessInterface(interfaceName, vaultObject);
            businessObject.addBusinessInterface(context, businessInterface);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Added interface is : '" + interfaceName + "'");
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void checkWarningInExecutingQuery(String queryResult) {
        String warningPattern = PropertyReader.getProperty("pattern.mql.warning");

        Pattern pattern = Pattern.compile(warningPattern);
        Matcher matcher = pattern.matcher(queryResult);
        if (matcher.find()) {
            throw new RuntimeException(queryResult);
        }
    }

    public void addFileToTheObject(Context context, HashMap<String, Object> params) throws MatrixException {
        try {
            businessObjectUtility.checkContext(context);
            if (NullOrEmptyChecker.isNull(params) || params.isEmpty()) {
                String errorMessage = "Param map is Null or Empty";
                BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            String[] initargs = {};

            String jpoClassName = "emxDnDBase";
            String jpoMethodName = "checkinFile";

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO class name : " + jpoClassName);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO method name : " + jpoMethodName);

            String resultReturnedFromEmxDNDBase = JPO.invoke(context, jpoClassName, initargs, jpoMethodName, JPO.packArgs(params), String.class);

            if (resultReturnedFromEmxDNDBase.contains("ERRORYou cannot check in")) {
                resultReturnedFromEmxDNDBase = resultReturnedFromEmxDNDBase.replace("ERRORYou", "You");
                throw new MatrixException(resultReturnedFromEmxDNDBase);
            }

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("RETURNED RESULT: " + resultReturnedFromEmxDNDBase);

        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void addFileToTheObject(Context context, HashMap<String, Object> params, Boolean ignoreFileAttachException) throws MatrixException {
        try {
            addFileToTheObject(context, params);
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            if (!ignoreFileAttachException) {
                throw exp;
            }
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            if (!ignoreFileAttachException) {
                throw exp;
            }
        }
    }

    public Boolean attachDocument(Context context, String baseObjectId, String[] strTableRowIds) throws Exception {
        HashMap programMap = new HashMap();
        String errorMessage;

        if (NullOrEmptyChecker.isNull(context)) {
            errorMessage = "Context is null";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(baseObjectId)) {
            errorMessage = "Object id is Null or Empty";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNullOrEmpty(strTableRowIds)) {
            errorMessage = "Document ids are Null or Empty";
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        programMap.put("objectId", baseObjectId);
        programMap.put("documentIds", strTableRowIds);

        try {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Object Id : " + programMap);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Document Ids : " + Arrays.deepToString(strTableRowIds));

            String jpoClassName = "CheckInCheckOutUtil";
            String jpoMethodName = "attachDocument";

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO class name : " + jpoClassName);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("JPO method name : " + jpoMethodName);

            String attachment = (String) JPO.invoke(context, jpoClassName, null, jpoMethodName, JPO.packArgs(programMap), String.class);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.info(attachment);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Document Attached Successfully");

            return Boolean.TRUE;

        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String abbreviate(String str, int maxWidth, String paddingChar) {
        if (str.length() <= maxWidth) {
            return str;
        }
        return paddingChar.length() > 0 ? str.substring(0, maxWidth - paddingChar.length()) + paddingChar : str.substring(0, maxWidth);
    }

//    public String getAutoGenerateObjectName(Context context, String objectType, String templateObjectId) throws MatrixException, Exception {
//
//        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been started");
//
//        populatePackageMapMap();
//
//        String typePackage = DISCIPLINE_MAP.get(objectType);
//        String autoName;
//
//        try {
//            if (typePackage == null) {
//                String initargs[] = {};
//                HashMap jpoParameters = new HashMap();
//                jpoParameters.put("objectId", templateObjectId);
//                autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameJpo", JPO.packArgs(jpoParameters), String.class);
//
//                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto-name generation process has been completed");
//                BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto generated name is : " + autoName);
//                return autoName;
//            }
//
//            String initargs[] = {};
//            HashMap jpoParameters = new HashMap();
//            jpoParameters.put("type", objectType);
//            jpoParameters.put("typePackage", typePackage);
//            autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(jpoParameters), String.class);
//            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been completed");
//            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto generated name is : " + autoName);
//            return autoName;
//        } catch (MatrixException exp) {
//            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp);
//            throw exp;
//        }
//    }
    public String getAutoGenerateObjectName(Context context, String objectType) throws MatrixException, Exception {

        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been started");

        populatePackageMapMap();

        String typePackage = DISCIPLINE_MAP.get(objectType);

        if (NullOrEmptyChecker.isNullOrEmpty(typePackage)) {
            throw new NullPointerException("Discipline for '" + objectType + "' type is not configurred in the RestService configuration");
        }
        try {
            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("type", objectType);
            jpoParameters.put("typePackage", typePackage);
            String autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(jpoParameters), String.class);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been completed");
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto generated name is : " + autoName);
            return autoName;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp);
            throw exp;
        }
    }

    public String getAutoGenerateObjectName(Context context, BusinessObject businessObject) throws MatrixException, Exception {

        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been started");

        try {
            businessObject.open(context);
            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("objectId", businessObject.getObjectId());
            String autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameJpo", JPO.packArgs(jpoParameters), String.class);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto-name generation process has been completed");
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto generated name is : " + autoName);
            return autoName;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp);
            throw exp;
        }
    }

    /**
     * Generate auto name depend on objectType.
     *
     * @param context
     * @param objectType
     * @return autoname as String
     * @throws MatrixException
     * @throws Exception
     */
    public String getAutoName(Context context, String objectType) throws MatrixException, Exception {

        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been started");

        try {
            String catiaSupportedTypes = PropertyReader.getProperty("generate.auto_name.type.support.catia");
            List<String> catiaSupportedTypeList = Arrays.asList(catiaSupportedTypes.split(","));

            String otherSupportedTypes = PropertyReader.getProperty("generate.auto_name.type.support.other");
            List<String> otherSupportedTypeList = Arrays.asList(otherSupportedTypes.split(","));

            if(catiaSupportedTypeList.contains(objectType)) {
                return getAutoNameForCatiaType(context, objectType);
            } else if (otherSupportedTypeList.contains(objectType)){
                return getAutoNameForOtherType(context, objectType);
            } else {
                BUSINESS_OBJECT_OPERATIONS_LOGGER.warn("This type: " + objectType + " is not supported or wrong.");
                throw new Exception("This type: " + objectType + " is not supported or wrong.");
            }
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp);
            throw exp;
        }
    }

    /**
     * Generate auto name for Catia type object depend on objectType.
     *
     * @param context
     * @param objectType
     * @return autoname as String
     * @throws MatrixException
     * @throws Exception
     */
    public String getAutoNameForCatiaType(Context context, String objectType) throws MatrixException, Exception {

        String typePackage = AutoNameTypePolicyConstants.getCADTypeDisciplinePackageMap(objectType);
        String autoName = "";
        try {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Type : " + objectType+ ", package: " + typePackage);
            String initargs[] = {};
            HashMap params = new HashMap();
            params.put("type", objectType);
            params.put("typePackage", typePackage);
            autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(params), String.class);
            BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Generated Auto name : " + autoName);
        } catch (Exception e) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(e.getMessage());
            throw e;
        }
        return autoName;
    }

    /**
     * Generate auto name for other type object depend on objectType.
     *
     * @param context
     * @param objectType
     * @return autoname as String
     * @throws MatrixException
     * @throws Exception
     */
    public String getAutoNameForOtherType(Context context, String objectType) throws MatrixException, Exception {
        return getAutoName(context, AutoNameTypePolicyConstants.getObjectSymbolicType(objectType), AutoNameTypePolicyConstants.getObjectSymbolicPolicy(objectType));
    }

    public String getAutoName(Context context, String symbolicTypeName, String symbolicPolicyName) throws MatrixException, Exception {

        BUSINESS_OBJECT_OPERATIONS_LOGGER.info("Auto-name generation process has been started");

        try {
            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("symbolicTypeName", symbolicTypeName);
            jpoParameters.put("symbolicPolicyName", symbolicPolicyName);
            String autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameBySymbolicTypePolicyJpo", JPO.packArgs(jpoParameters), String.class);

            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto-name generation process has been completed");
            BUSINESS_OBJECT_OPERATIONS_LOGGER.debug("Auto generated name is : " + autoName);
            return autoName;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_OPERATIONS_LOGGER.error(exp);
            throw exp;
        }
    }

    public HashMap<String, String> getPreviousBOAttributesListOfRevisedItem(Context context, BusinessObject revisedtItem) throws MatrixException {
        HashMap<String,String> attributeListMap = new HashMap<>();
        BusinessObject previousItem = revisedtItem.getPreviousRevision(context);
        AttributeList attributeList = previousItem.getAttributes(context).getAttributes();
        attributeList.forEach(attribute -> {
            attributeListMap.put(attribute.getName(),attribute.getValue());
        });
        return attributeListMap;
    }



    /**
     * Example : attribute[MBOM_MBOMPDM.MBOM_PDM_Owner_Group] Removed the last
     * "]" square bracket and removed first "attribute[" from the string number
     * of chars in "attribute[" is 10
     *
     * @param encodedAttributeName
     * @return
     */
    public String getAttributeName(String encodedAttributeName) {
        try {
            
            if(!encodedAttributeName.contains("attribute[")){
                return encodedAttributeName;
            }
            
            int stringLength = encodedAttributeName.length() - 1;
            encodedAttributeName = encodedAttributeName.substring(0, stringLength).substring(10, stringLength);
            return encodedAttributeName;
        } catch (Exception exp) {
            return encodedAttributeName;
        }
    }

    private void populatePackageMapMap() {
        if (DISCIPLINE_MAP == null || DISCIPLINE_MAP.size() < 1) {
            DISCIPLINE_MAP = PropertyReader.getProperties(OBJECT_DISCIPLINE_PATTERN, true);
        }
    }




    private void setBusinessObjectUtility(BusinessObjectUtility businessObjectUtility) {
        this.businessObjectUtility = businessObjectUtility;
    }



    public BusinessObjectOperations() {
        businessObjectUtility = new BusinessObjectUtility();
    }
}
