/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comosData.project_structure.utilities;

import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.project_structure.model.CreateObjectBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.log4j.Level;

/**
 *
 * @author BJIT
 */
public class CloneObject {
//    private static final org.apache.log4j.Logger CLONE_OBJECT_LOGGER = org.apache.log4j.Logger.getLogger(CloneObject.class);
//
//    public static synchronized HashMap createObjectCloneParametersMap(Context context, CreateObjectBean createObjectBean) throws Exception {
//        try {
//            HashMap objectCloneParametersMap = new HashMap();
//
//            HashMap folderIdForJPOSourceMap = new HashMap();
//            String newFolderId = (createObjectBean.getFolderId() == null || createObjectBean.getFolderId().equalsIgnoreCase("")) ? "" : createObjectBean.getFolderId();
//
//            folderIdForJPOSourceMap.put("FolderId", newFolderId);
//            objectCloneParametersMap.put("SpecificationMap", folderIdForJPOSourceMap);
//
//            HashMap<String, String> attributeGlobalReadMap = new HashMap<>();
//            String attributeGlobalRead = createObjectBean.getAttributeGlobalRead() == null ? "false" : createObjectBean.getAttributeGlobalRead().toString();
//            attributeGlobalReadMap.put("attribute_GlobalRead", attributeGlobalRead);
//            objectCloneParametersMap.put("AttributeMap", attributeGlobalReadMap);
//
//            System.out.println("attributeGlobalRead : " + attributeGlobalRead);
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "AttributeMap : " + attributeGlobalReadMap);
//
//            String objectType = createObjectBean.getTnr().getType();
//
//            if (objectType == null || objectType.equals("")) {
//                try {
//                    BusinessObject templateObject = new BusinessObject(createObjectBean.getTemplateBusinessObjectId());
//                    templateObject.open(context);
//                    objectType = templateObject.getTypeName();
//                } catch (MatrixException exp) {
//                    CLONE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
//                    throw new Exception("'Skeleton-id' may be wrong. Please provide a valid 'skeleton-id'");
//                }
//            }
//
//            if (createObjectBean.getIsAutoName()) {
//                objectCloneParametersMap.put("Name", generateObjectName(context, objectType, createObjectBean.getTemplateBusinessObjectId()));
//            } else {
//                String objectName = createObjectBean.getTnr().getName();
//                if (!NullOrEmptyChecker.isNullOrEmpty(objectName)) {
//                    objectCloneParametersMap.put("Name", objectName);
//                } else {
//                    CLONE_OBJECT_LOGGER.log(Level.ERROR, "Object name has not been provided and also value of auto-name field is false. System could not create an object without name. Please try to provide one of them");
//                    throw new Exception("Please provide 'Type', 'Name' properly");
//                }
//            }
//
//            if (NullOrEmptyChecker.isNullOrEmpty(objectType)) {
//                CLONE_OBJECT_LOGGER.log(Level.ERROR, "Object type has not been provided. System could not create an object without type. Please try to provide type of object");
//                throw new Exception("Please provide 'Type', 'Name' properly");
//            }
//
//            objectCloneParametersMap.put("Type", objectType);
//
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Type : " + objectType);
//            return objectCloneParametersMap;
//        } catch (Exception exp) {
//            CLONE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
//            throw exp;
//        }
//    }
//
//    public static synchronized String cloneObjectByJPO(Context context, CreateObjectBean createObjectBean, HashMap objectCloneParametersMap) throws Exception {
//        HashMap objectCreationParamatersMap = new HashMap();
//
//        objectCreationParamatersMap.put("objectId", createObjectBean.getTemplateBusinessObjectId());
//        objectCreationParamatersMap.put("paramList", objectCloneParametersMap);
//
//        if (!NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getCs())) {
//            objectCreationParamatersMap.put("collaborationSpace", getCollaborationSpace(createObjectBean));
//        }
//
//        String initargs[] = {};
//        String clonedObjectId;
//        try {
//            String jpoClassName = "CloneObjectUtil";
//            String jpoMethodName = "cloneObject";
//
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "JPO class name : " + jpoClassName);
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "JPO method name : " + jpoMethodName);
//
//            clonedObjectId = (String) JPO.invoke(context, jpoClassName, initargs,
//                    jpoMethodName, JPO.packArgs(objectCreationParamatersMap), String.class);
//
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Cloned object Id : " + clonedObjectId);
//            return clonedObjectId;
//        } catch (MatrixException exp) {
//            CLONE_OBJECT_LOGGER.log(Level.ERROR, exp.getMessage());
//            CLONE_OBJECT_LOGGER.log(Level.TRACE, exp);
//            exp.printStackTrace(System.out);
//            throw new Exception(exp.getMessage());
//        }
//    }
//
//    private static synchronized String getCollaborationSpace(CreateObjectBean createObjectBean) throws MatrixException {
//        try {
//            String role = createObjectBean.getCs();
//            String[] roleParts = role.split("\\.");
//
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Role Parts are : " + Arrays.toString(roleParts));
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Role Parts length : " + roleParts.length);
//            String rolePart = roleParts[roleParts.length - 1];
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Role Part is : " + rolePart);
//
//            return rolePart;
//
//        } catch (Exception ex) {
//            CLONE_OBJECT_LOGGER.log(Level.ERROR, ex.getMessage());
//            throw ex;
//        }
//    }
//
//    public static synchronized String generateObjectName(Context context, String objectType, String templateObjectId) throws MatrixException, Exception {
//
//        CLONE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been started");
//        HashMap<String, String> PACKAGE_MAP = populatePackageMapMap();
//
//        String typePackage = PACKAGE_MAP.get(objectType);
//        String autoName;
//
//        try {
//            if (typePackage == null) {
//                String initargs[] = {};
//                HashMap jpoParameters = new HashMap();
//                jpoParameters.put("objectId", templateObjectId);
//                autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameJpo", JPO.packArgs(jpoParameters), String.class);
//
//                CLONE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been completed");
//                CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Auto generated name is : " + autoName);
//                return autoName;
//            }
//
//            String initargs[] = {};
//            HashMap jpoParameters = new HashMap();
//            jpoParameters.put("type", objectType);
//            jpoParameters.put("typePackage", typePackage);
//            autoName = (String) JPO.invoke(context, "CloneObjectUtil", initargs, "getAutoNameOfManObjectType", JPO.packArgs(jpoParameters), String.class);
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "Auto-name generation process has been completed");
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Auto generated name is : " + autoName);
//            return autoName;
//        } catch (MatrixException exp) {
//            CLONE_OBJECT_LOGGER.log(Level.ERROR, "Error occured during autoname creation process");
//            CLONE_OBJECT_LOGGER.log(Level.TRACE, exp);
//            throw exp;
//        }
//    }
//
//    private static synchronized HashMap<String, String> populatePackageMapMap() {
//        HashMap<String, String> package_map = new HashMap<>();
//        package_map.put("Drawing", "CATDraftingDiscipline");
//        package_map.put("CreateAssembly", "DELAsmAssemblyModelDisciplines");
//        package_map.put("ElementaryEndItem", "DELAsmAssemblyModelDisciplines");
//        package_map.put("ProcessContinuousCreateMaterial", "DELAsmAssemblyModelDisciplines");
//        package_map.put("ProcessContinuousProvide", "DELAsmAssemblyModelDisciplines");
//        package_map.put("Provide", "DELAsmAssemblyModelDisciplines");
//        package_map.put("DELLmiGeneralSystemReference", "DELLmiProductionGeneralSystem");
//        package_map.put("DELLmiProdSystemIOPort", "DELLmiProductionSystemIOPort");
//        package_map.put("PPRContext", "DELPPRContextModelDisciplines");
//        package_map.put("PLMBusinessRule", "PLMKnowHowBusinessRule");
//        package_map.put("VPMReference", "PRODUCTCFG");
//        package_map.put("3DShape", "PRODUCTDiscipline");
//        return package_map;
//    }
//
//    public static synchronized List<Object> updateAttributes(Context context, String objectId, HashMap<String, String> objectBeanAttributes) throws Exception {
//        List<Object> result = null;
//
//        try {
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "Updating attributes of a business object. Id : " + objectId);
//
//            HashMap attributeUpdateParamertes = new HashMap();
//            attributeUpdateParamertes.put("objectId", objectId);
//            attributeUpdateParamertes.put("attributeMap", objectBeanAttributes);
//
//            String jpoClassName = "CloneObjectUtil";
//            //String jpoMethodName = "updateObjectAttribute";
//            String jpoMethodName = "updateObjectMultiAttribute";
//
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "JPO class name : " + jpoClassName);
//            CLONE_OBJECT_LOGGER.log(Level.INFO, "JPO method name : " + jpoMethodName);
//
//            String[] constructor = {null};
//            result = (List<Object>) JPO.invoke(context, jpoClassName, constructor, jpoMethodName, JPO.packArgs(attributeUpdateParamertes), List.class);
//            HashMap<String, String> updated = (HashMap<String, String>) result.get(1);
//            System.out.println("updated : " + updated);
//            CLONE_OBJECT_LOGGER.log(Level.DEBUG, "Updating attributes of a business object. Id : " + objectId + " completed successfully. Result " + result);
//        } catch (Exception exp) {
//            exp.printStackTrace(System.out);
//            CLONE_OBJECT_LOGGER.log(Level.TRACE, exp);
//            throw exp;
//        }
//        return result;
//    }
}
