/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin.processors;

import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.utility.ItemImportUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import matrix.db.BusinessObject;
import matrix.db.Context;
import org.apache.log4j.Priority;
import org.springframework.stereotype.Component;

/**
 *
 * @author BJIT
 */
@Component
public class ObjectCreationProcessor {

    private static final org.apache.log4j.Logger OBJECT_CREATION_PROCESSOR_LOGGER = org.apache.log4j.Logger.getLogger(ObjectCreationProcessor.class);
    private String existingObjectId;
    private String businessObjectinterfaceName;
    private List<String> businessObjectinterfaceList;
    private Boolean checkInterfaceExistence = Boolean.FALSE;

    public String processCreateObjectOperation(Context context, CreateObjectBean createBean, BusinessObjectOperations businessObjectOperations) throws FrameworkException, Exception {

        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);

            BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
            
            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                String objectId = businessObjectUtility.searchByTypeName(context, createObjectBean.getTnr().getType(), createObjectBean.getTnr().getName());

                if (!NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                    this.existingObjectId = objectId;
                    String errorMessage = "Error in creating of object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() + (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "'. Object is exists";
                    OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }

            HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
            OBJECT_CREATION_PROCESSOR_LOGGER.debug("ObjectCloningMap is : " + objectCloningMap);

            String clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
            OBJECT_CREATION_PROCESSOR_LOGGER.debug("ClonedObjectId is : " + clonedObjectId);

            updateObject(createObjectBean, context, businessObjectOperations, clonedObjectId);

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateObjectOperation(Context context, CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations, Boolean ifExistsThenReturnObjectId) throws FrameworkException, Exception {
        try {
            this.existingObjectId = null;
            return processCreateObjectOperation(context, createObjectBean, businessObjectOperations);
        } catch (Exception exp) {
            if (ifExistsThenReturnObjectId) {
                if (!NullOrEmptyChecker.isNullOrEmpty(this.existingObjectId)) {
                    updateObject(createObjectBean, context, businessObjectOperations, this.existingObjectId);
                    return this.existingObjectId;
                }
            }
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean) throws FrameworkException, Exception {
        String clonedObjectId = null;
        Boolean hasNextVersion = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();
        
        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
        
        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            Boolean objectIsExists = false;
            String nextVersion = createObjectBean.getNextVersion();

            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                //String objectId = BusinessObjectUtility.searchByTypeName(context, createObjectBean.getTnr().getType(), createObjectBean.getTnr().getName());
                //BusinessObject latestRevisedBO = ItemImportUtil.getExistingBOorRevisedBO(context, createObjectBean.getTnr());
                hasNextVersion = !NullOrEmptyChecker.isNullOrEmpty(nextVersion);
                BusinessObject latestRevisedBO = ItemImportUtil.getExistingBOorRevisedBO(context, businessObjectUtil, createObjectBean.getTnr(), hasNextVersion);

                String objectId = "";

                if (!NullOrEmptyChecker.isNull(latestRevisedBO)) {
                    objectIsExists = true;
                    objectId = latestRevisedBO.getObjectId();
                }

                clonedObjectId = objectId;

                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
//                    this.existingObjectId = objectId;
//                    String errorMessage = "Error in creating of object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() +  (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "'. Object is exists";
//                    OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
//                    throw new RuntimeException(errorMessage);

                    String lastVersionProperty = commonPropertyReader.getPropertyValue("item.is.last.version");
                    createObjectBean.getAttributes().put(lastVersionProperty, hasNextVersion ? "FALSE" : "TRUE");

                    HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ObjectCloningMap is : " + objectCloningMap);

                    clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ClonedObjectId is : " + clonedObjectId);
                } else {
                    this.existingObjectId = objectId;
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                //final String updatabelNewClonedObject = clonedObjectId;
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceList, "", this.checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                //BusinessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "");
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "", this.checkInterfaceExistence);
            }

            Instant itemUpdateStartTime = Instant.now();
            long createDuration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + createDuration + "' milli-seconds for creating in the DB");

            updateObject(createObjectBean, context, businessObjectOperations, clonedObjectId, objectIsExists);

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + updateDuration + "' milli-seconds for updating in the DB");

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseEnoviaObject(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean) throws FrameworkException, Exception {
        String clonedObjectId = null;
        Boolean hasNextVersion = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();

        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            Boolean objectIsExists = false;
            String nextVersion = createObjectBean.getNextVersion();

            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                hasNextVersion = !NullOrEmptyChecker.isNullOrEmpty(nextVersion);
                BusinessObject latestRevisedBO = ItemImportUtil.getExistingorRevisedItem(context, businessObjectUtil, createObjectBean.getTnr(), hasNextVersion);

                String objectId = "";

                if (!NullOrEmptyChecker.isNull(latestRevisedBO)) {
                    objectIsExists = true;
                    objectId = latestRevisedBO.getObjectId();
                }
                clonedObjectId = objectId;

                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
                    String lastVersionProperty = commonPropertyReader.getPropertyValue("item.is.last.version");
                    createObjectBean.getAttributes().put(lastVersionProperty, hasNextVersion ? "FALSE" : "TRUE");

                    HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ObjectCloningMap is : " + objectCloningMap);

                    clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ClonedObjectId is : " + clonedObjectId);
                } else {
                    this.existingObjectId = objectId;
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceList, "", this.checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "", this.checkInterfaceExistence);
            }

            Instant itemUpdateStartTime = Instant.now();
            long createDuration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + createDuration + "' milli-seconds for creating in the DB");

            updateObject(createObjectBean, context, businessObjectOperations, clonedObjectId, objectIsExists);

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + updateDuration + "' milli-seconds for updating in the DB");

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean) throws FrameworkException, Exception {
        String clonedObjectId = null;
        Boolean hasNextVersion = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        Instant itemImportStartTime = Instant.now();
        
        BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();
        
        try {
            CreateObjectBean createObjectBean = validateCreateObjectBean(context, createBean, businessObjectOperations);
            Boolean objectIsExists = false;
            String nextVersion = createObjectBean.getNextVersion();

            if (createObjectBean.getIsAutoName()) {
                String autoName = businessObjectUtility.getAutoName(context, createObjectBean.getTnr().getType(), createObjectBean.getTemplateBusinessObjectId(), businessObjectUtility.getPackageType(createObjectBean.getTnr().getType()));
                OBJECT_CREATION_PROCESSOR_LOGGER.debug("Autoname is : " + autoName);
                createObjectBean.getTnr().setName(autoName);
            } else {
                businessObjectOperations.validateTNR(createObjectBean.getTnr(), Boolean.TRUE);
                hasNextVersion = !NullOrEmptyChecker.isNullOrEmpty(nextVersion);
                BusinessObject latestRevisedBO = ItemImportUtil.getExistingorRevisedItem(context, businessObjectUtil, createObjectBean.getTnr(), hasNextVersion);

                String objectId = "";

                if (!NullOrEmptyChecker.isNull(latestRevisedBO)) {
                    objectIsExists = true;
                    objectId = latestRevisedBO.getObjectId();
                }

                clonedObjectId = objectId;

                if (NullOrEmptyChecker.isNullOrEmpty(objectId)) {
//                    existingObjectId = objectId;
//                    String errorMessage = "Error in creating of object of Type: '" + createObjectBean.getTnr().getType() + "' Name: '" + createObjectBean.getTnr().getName() +  (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTnr().getRevision()) ? "" : "' Revision: '" + createObjectBean.getTnr().getRevision()) + "'. Object is exists";
//                    OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
//                    throw new RuntimeException(errorMessage);

//                    String lastVersionProperty = commonPropertyReader.getPropertyValue("item.is.last.version");
//                    createObjectBean.getAttributes().put(lastVersionProperty, hasNextVersion ? "FALSE" : "TRUE");

                    HashMap objectCloningMap = createObjectCloningMap(createObjectBean);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ObjectCloningMap is : " + objectCloningMap);

                    clonedObjectId = businessObjectOperations.cloneObject(context, createObjectBean, objectCloningMap);
                    OBJECT_CREATION_PROCESSOR_LOGGER.debug("ClonedObjectId is : " + clonedObjectId);
                } else {
                    this.existingObjectId = objectId;
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceList)) {
                //final String updatabelNewClonedObject = clonedObjectId;
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceList, "", this.checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(this.businessObjectinterfaceName)) {
                //BusinessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "");
                businessObjectOperations.addInterface(context, clonedObjectId, this.businessObjectinterfaceName, "", this.checkInterfaceExistence);
            }

            Instant itemUpdateStartTime = Instant.now();
            long createDuration = DateTimeUtils.getDuration(itemImportStartTime, itemUpdateStartTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + createDuration + "' milli-seconds for creating in the DB");

            updateItem(createObjectBean, context, businessObjectOperations, clonedObjectId, objectIsExists);

            Instant itemUpdateEndTime = Instant.now();
            long updateDuration = DateTimeUtils.getDuration(itemUpdateStartTime, itemUpdateEndTime);
            OBJECT_CREATION_PROCESSOR_LOGGER.log(Boolean.parseBoolean(commonPropertyReader.getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : ODI Item type : '" + createBean.getTnr().getType() + "' name '" + createBean.getTnr().getName() + "' revision '" + createBean.getTnr().getRevision() + "' has taken : '" + updateDuration + "' milli-seconds for updating in the DB");

            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, String interfaceName) throws FrameworkException, Exception {
        try {

            businessObjectinterfaceName = !NullOrEmptyChecker.isNullOrEmpty(interfaceName) ? interfaceName : null;
            String clonedObjectId = processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList) throws FrameworkException, Exception {
        try {

            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String clonedObjectId = processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseObjectOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList, Boolean checkInterfacesExistence) throws FrameworkException, Exception {
        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String clonedObjectId = processCreateReviseObjectOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateReviseEnoviaObject(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList, Boolean checkInterfacesExistence) throws FrameworkException, Exception {
        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String clonedObjectId = processCreateReviseEnoviaObject(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String processCreateItemOperation(Context context, BusinessObjectUtil businessObjectUtil, BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean, List<String> interfaceList, Boolean checkInterfacesExistence) throws FrameworkException, Exception {
        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String clonedObjectId = processCreateItemOperation(context, businessObjectUtil, businessObjectOperations, createBean);
            return clonedObjectId;
        } catch (FrameworkException exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public void updateObject(CreateObjectBean createObjectBean, Context context, BusinessObjectOperations businessObjectOperations, String clonedObjectId) throws FrameworkException, InterruptedException, Exception {
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();
        HashMap<String, String> objectUpdateMap = objectDefaultValues.getObjectUpdateMap(createObjectBean);
        createObjectBean.setAttributes(objectUpdateMap);
        objectUpdateMap = getItemUpdateMap(createObjectBean);
        businessObjectOperations.updateObject(context, clonedObjectId, objectUpdateMap);
    }

    public void updateObject(CreateObjectBean createObjectBean, Context context, BusinessObjectOperations businessObjectOperations, String clonedObjectId, Boolean isExists) throws FrameworkException, InterruptedException, Exception {
        //HashMap<String, String> objectUpdateMap = ObjectDefaultValues.getObjectUpdateMap(createObjectBean);
        //createObjectBean.setAttributes(objectUpdateMap);
        if (isExists) {
            HashMap<String, String> objectUpdateMap = getObjectUpdateMap(createObjectBean);
            businessObjectOperations.updateObject(context, clonedObjectId, objectUpdateMap);
        } else {
            updateObject(createObjectBean, context, businessObjectOperations, clonedObjectId);
        }

    }

    public void updateItem(CreateObjectBean createObjectBean, Context context, BusinessObjectOperations businessObjectOperations, String clonedObjectId, Boolean isExists) throws FrameworkException, InterruptedException, Exception {
        //HashMap<String, String> objectUpdateMap = ObjectDefaultValues.getObjectUpdateMap(createObjectBean);
        //createObjectBean.setAttributes(objectUpdateMap);
        if (isExists) {
            HashMap<String, String> objectUpdateMap = getItemUpdateMap(createObjectBean);
            businessObjectOperations.updateObject(context, clonedObjectId, objectUpdateMap);
        } else {
            updateObject(createObjectBean, context, businessObjectOperations, clonedObjectId);
        }

    }

    public CreateObjectBean validateCreateObjectBean(Context context, CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) throws FrameworkException, InterruptedException, Exception {
        String errorMessage;

        if (NullOrEmptyChecker.isNull(createObjectBean.getIsAutoName())) {
            errorMessage = "Please set 'isAutoName' property correctly";
            OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        resolveSkeletonId(createObjectBean, businessObjectOperations);
        TNR templateObjectTNR = businessObjectOperations.getObjectTNR(context, createObjectBean.getTemplateBusinessObjectId());

        try {
            businessObjectOperations.validateTNR(createObjectBean.getTnr());
            if (!createObjectBean.getTnr().getType().equalsIgnoreCase(templateObjectTNR.getType())) {
                errorMessage = "Template type and Object type is not same";
                OBJECT_CREATION_PROCESSOR_LOGGER.error(errorMessage);
                throw new Exception(errorMessage);
            }
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp);

            TNR tnr = new TNR();
            tnr.setType(templateObjectTNR.getType());
            createObjectBean.setTnr(tnr);
        }

        return createObjectBean;
    }

    public CreateObjectBean resolveSkeletonId(CreateObjectBean createObjectBean, BusinessObjectOperations businessObjectOperations) throws Exception {
        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getTemplateBusinessObjectId())) {
            try {
                businessObjectOperations.validateTNR(createObjectBean.getTnr());
                BusinessObjectUtility businessObjectUtility = businessObjectOperations.getBusinessObjectUtility();

                String skeletonId = businessObjectUtility.getSkeletonId(createObjectBean.getTnr().getType());
                createObjectBean.setTemplateBusinessObjectId(skeletonId);
            } catch (Exception exp) {
                OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
                throw exp;
            }
        }
        return createObjectBean;
    }

    public HashMap createObjectCloningMap(CreateObjectBean createObjectBean) {
        try {
            HashMap objectCloneParametersMap = new HashMap();

            HashMap specificationMap = new HashMap();
            String folderId = NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getFolderId()) ? "" : createObjectBean.getFolderId();

            specificationMap.put("FolderId", folderId);
            objectCloneParametersMap.put("SpecificationMap", specificationMap);

            HashMap<String, String> attributeMap = new HashMap<>();
            String attributeGlobalRead = NullOrEmptyChecker.isNull(createObjectBean.getAttributeGlobalRead()) ? "false" : createObjectBean.getAttributeGlobalRead().toString();

            attributeMap.put("attribute_GlobalRead", attributeGlobalRead);
            objectCloneParametersMap.put("AttributeMap", attributeMap);
            objectCloneParametersMap.put("Type", createObjectBean.getTnr().getType());
            objectCloneParametersMap.put("Name", createObjectBean.getTnr().getName());

            return objectCloneParametersMap;
        } catch (Exception exp) {
            OBJECT_CREATION_PROCESSOR_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public HashMap<String, String> getObjectUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        if (updateAttributes.containsKey("PLMEntity.V_nature")) {
            throw new RuntimeException("Please don't update 'PLMEntity.V_nature'");
        }

        if (!updateAttributes.containsKey("current")) {
            updateAttributes.put("current", "IN_WORK");
        }

        return updateAttributes;
    }

    public HashMap<String, String> getItemUpdateMap(CreateObjectBean createObjectBean) {
//        HashMap<String, String> updateAttributes;

//        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
//            updateAttributes = new HashMap<>();
//        } else {
//            updateAttributes = createObjectBean.getAttributes();
//        }
//
//        if (updateAttributes.containsKey("PLMEntity.V_nature")) {
//            throw new RuntimeException("Please don't update 'PLMEntity.V_nature'");
//        }
//
//        if (!updateAttributes.containsKey("current")) {
//            updateAttributes.put("current", "IN_WORK");
//        }

        return createObjectBean.getAttributes();
    }

}
