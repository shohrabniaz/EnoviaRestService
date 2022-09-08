/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

/**
 *
 * @author BJIT / Omour Faruq
 */
public class BusinessObjectUtility {

    private static final String TYPE_PATTERN = "template.object.type";
    private static final String CAD_TYPE_PATTERN = "discipline.cat.type.object";
    public static Map<String, String> PACKAGE_MAP;
    public static Map<String, String> TYPE_MAP;

    private static final org.apache.log4j.Logger BUSINESS_OBJECT_UTILITY_LOGGER = org.apache.log4j.Logger.getLogger(BusinessObjectUtility.class);

    public String searchByTypeName(Context context, String type, String name) throws MatrixException {

        try {
            String errorMessage;
            this.checkContext(context);
            if (NullOrEmptyChecker.isNullOrEmpty(type)) {
                errorMessage = "'Type' is Null or Empty";
                BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            if (NullOrEmptyChecker.isNullOrEmpty(name)) {
                errorMessage = "'Name' of '" + type + "' is Null or Empty";
                BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);

            }

            String[] constructor = {null};
            HashMap params = new HashMap();
            params.put("name", name);
            params.put("type", type);

            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "searchByTypeName";

            BUSINESS_OBJECT_UTILITY_LOGGER.debug("JPO class name is : " + jpoClassName + " method name is : " + jpoMethodName);

            String objectId = (String) JPO.invoke(context, jpoClassName, constructor, jpoMethodName, JPO.packArgs(params), String.class);

            return objectId;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String getCollaborationSpace(String collaborationSpace) throws MatrixException {
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(collaborationSpace)) {
                String errorMessage = "'Collaborative Space' is Null or Empty";
                BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            String[] roleParts = collaborationSpace.split("\\.");

            BUSINESS_OBJECT_UTILITY_LOGGER.debug("Role Parts are : " + Arrays.toString(roleParts));
            BUSINESS_OBJECT_UTILITY_LOGGER.debug("Role Parts length : " + roleParts.length);
            String rolePart = roleParts[roleParts.length - 1];
            BUSINESS_OBJECT_UTILITY_LOGGER.debug("Role Part is : " + rolePart);

            return rolePart;

        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String getAutoName(Context context, String objectType, String templateObjectId, String packageType) throws MatrixException, Exception {

        BUSINESS_OBJECT_UTILITY_LOGGER.info("Auto-name generation process has been started");

        //String autoName = packageType == null ? getAutoName(context, templateObjectId) : getAutoName(context, objectType, packageType);
        String autoName = NullOrEmptyChecker.isNullOrEmpty(packageType) ? getAutoName(context, templateObjectId) : getAutoName(context, objectType, packageType);
        BUSINESS_OBJECT_UTILITY_LOGGER.debug("AutoName : " + autoName);
        BUSINESS_OBJECT_UTILITY_LOGGER.debug("Auto-name generation process has been completed");
        return autoName;
    }

    public String getAutoName(Context context, String templateObjectId) throws MatrixException {
        try {
            this.checkContext(context);
            this.checkObjectId(templateObjectId);

            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("objectId", templateObjectId);

            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "getAutoNameJpo";

            BUSINESS_OBJECT_UTILITY_LOGGER.debug("JPO class name is : " + jpoClassName + " method name is : " + jpoMethodName);

            String autoName = (String) JPO.invoke(context, jpoClassName, initargs, jpoMethodName, JPO.packArgs(jpoParameters), String.class);
            return autoName;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp);
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String getAutoName(Context context, String objectType, String packageType) throws MatrixException {
        String errorMessage;
        try {
            this.checkContext(context);
            if (NullOrEmptyChecker.isNullOrEmpty(objectType)) {
                errorMessage = "Object Type is Null or Empty";
                BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            if (NullOrEmptyChecker.isNullOrEmpty(packageType)) {
                errorMessage = "Package Type is Null or Empty";
                BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
                throw new NullPointerException(errorMessage);
            }

            String initargs[] = {};
            HashMap jpoParameters = new HashMap();
            jpoParameters.put("type", objectType);
            jpoParameters.put("typePackage", packageType);

            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "getAutoNameOfManObjectType";

            BUSINESS_OBJECT_UTILITY_LOGGER.debug("JPO class name is : " + jpoClassName + " method name is : " + jpoMethodName);

            String autoName = (String) JPO.invoke(context, jpoClassName, initargs, jpoMethodName, JPO.packArgs(jpoParameters), String.class);
            return autoName;
        } catch (MatrixException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp);
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public String getLastHistoryLine(BusinessObject businessObject, Context context) {
        try {
            List historyList = businessObject.getHistory(context);
            return historyList.get(historyList.size() - 1).toString();
        } catch (Exception e) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error("Could not find history of " + e.getMessage());
        }
        return null;
    }

    public String getChangeIdFromHistory(BusinessObject businessObject, Context context) {

        String result = "";
        try {
            List historyList = businessObject.getHistory(context);
            /*List historyList = new ArrayList();
            historyList.add("modify - user: User Agent  time: 6/9/2022 9:39:13 AM  state: Review  Change Id: D8B906390000F3E062A1954300023159  was: ");
            historyList.add("promote - user: User Agent  time: 6/9/2022 9:42:01 AM  state: Release");
            historyList.add("modify - user: User Agent  time: 6/9/2022 9:42:01 AM  state: Release  Change Id:   was: D8B906390000F3E062A1954300023159");
            historyList.add(" history = modify - user: User Agent  time: 6/9/2022 9:39:13 AM  state: Review  Change Id: D8B906390000F3E062A1954300023159  was: ");*/

            for (int i = historyList.size() - 1; i >= 0; i--) {
                if (historyList.get(i).toString().contains("user: User Agent") && historyList.get(i).toString().contains("state: RELEASED") && historyList.get(i).toString().toString().contains("Change Id:   was:")) {
                    result = historyList.get(i).toString();
                    break;
                }
            }
            String[] temp = result.split(" ");
            result = temp[temp.length - 1];
        } catch (Exception e) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error("Could not find history of " + e.getMessage());
        }
        if ("".equals(result)) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error("History is empty!");
        }
        return result;
    }
    
    public String getUserFromHistory(BusinessObject businessObject, Context context, String toState) {

        String user = "";
        try {
            List historyList = businessObject.getHistory(context);
//            history.between = promote - user: jklalrahab  time: 7/7/2022 3:09:17 PM  state: Review
            for (int i = historyList.size() - 1; i >= 0; i--) {
                if (historyList.get(i).toString().contains("promote") && historyList.get(i).toString().contains("state: " + toState)) {
                    user = historyList.get(i).toString();
                    break;
                }
            }
            String[] temp = user.split(" ");
            for(int i=0; i < temp.length; i++){
                if(temp[i].equals("user:")){
                    user = temp[i + 1];
                    break;
                }
            }
        } catch (Exception e) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error("Could not find historylist for getting user " + e.getMessage());
        }
        if ("".equals(user)) {
            BUSINESS_OBJECT_UTILITY_LOGGER.error("Could not find any user from history");
        }
        return user;
    }

    public List<String> getUserInformation(Context context, String changeId) throws FrameworkException {
        List<String> user = new ArrayList();
        String queryResult = "";
        String queryBuilder = "expand bus " + changeId + " withroots rel \"Change Reviewer\" dump <>";

        BUSINESS_OBJECT_UTILITY_LOGGER.info("MQL Query: " + queryBuilder);

        queryResult = MqlUtil.mqlCommand(context, queryBuilder);
        /* queryResult = "0<><><>Change Action<>CA-EPS1-00000729<>-\n"
                + "1<>Change Reviewer<>to<>Person<>rftuser<>-";*/
        if (queryResult.indexOf("Change Reviewer") > 0 && queryResult.indexOf("Person") > 0) {
            String[] a = queryResult.split("\n");
            for (String iter : a) {
                String[] b = iter.split("<>");
                if(b[0].equalsIgnoreCase("1")) {
                    user.add(b[b.length - 2]);
                }
            }
        }
        BUSINESS_OBJECT_UTILITY_LOGGER.info("Query Result of expanding changeID : " + queryResult);

        /*
		  0|||Change Action|CA-EPS1-00000729|-
		  1|Change Reviewer|to|Person|rftuser|-
         */
        return user;
    }

    public void checkContext(Context context) {
        if (NullOrEmptyChecker.isNull(context)) {
            String errorMessage = "Context is Null";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    public void checkObject(BusinessObject object) {
        if (NullOrEmptyChecker.isNull(object)) {
            String errorMessage = "Object is Null";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    public void checkObjectId(String objectId) {
        if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
            String errorMessage = "Object Id is Null or Empty";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    public void checkVault(String valut) {
        if (NullOrEmptyChecker.isNullOrEmpty(valut)) {
            String errorMessage = "Vault is Null or Empty";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }
    }

    public String getSkeletonId(String type) throws Exception {
        populateTypeMap();

        if (NullOrEmptyChecker.isNullOrEmpty(type)) {
            String errorMessage = "Type is Null or Empty";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        String skeletonId = TYPE_MAP.get(type);
        if (NullOrEmptyChecker.isNullOrEmpty(skeletonId)) {

            StringBuilder allowedTypeBuilder = new StringBuilder();

            TYPE_MAP.forEach((key, value) -> {
                allowedTypeBuilder.append("'").append(key).append("'").append(",").append(" ");
            });
            String allowedTypes = "Allowed types : " + allowedTypeBuilder.toString().replaceAll(", $", "");

            BUSINESS_OBJECT_UTILITY_LOGGER.error(allowedTypes);
            throw new Exception(allowedTypes);
        }
        return skeletonId;
    }

    public String getPackageType(String packageType) throws Exception {
        populatePackageMap();

        if (NullOrEmptyChecker.isNullOrEmpty(packageType)) {
            String errorMessage = "'Package' is Null or Empty";
            BUSINESS_OBJECT_UTILITY_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        packageType = PACKAGE_MAP.get(packageType);
        /*if (NullOrEmptyChecker.isNullOrEmpty(packageType)) {

            StringBuilder allowedPackageBuilder = new StringBuilder();

            PACKAGE_MAP.forEach((key, value) -> {
                allowedPackageBuilder.append("'").append(key).append("'").append(",").append(" ");
            });
            String allowedPackages = "Allowed package : " + allowedPackageBuilder.toString().replaceAll(", $", "");

            BUSINESS_OBJECT_UTILITY_LOGGER.error(allowedPackages);
            throw new Exception(allowedPackages);
        }*/
        return packageType;
    }

    private void populateTypeMap() {
        if (TYPE_MAP == null || TYPE_MAP.size() < 1) {
            TYPE_MAP = PropertyReader.getProperties(TYPE_PATTERN, true);
        }
    }

    private void populatePackageMap() {
        if (PACKAGE_MAP == null || PACKAGE_MAP.size() < 1) {
            PACKAGE_MAP = PropertyReader.getProperties(CAD_TYPE_PATTERN, true);
        }
    }
}
