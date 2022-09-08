package com.bjit.common.rest.app.service.controller.item.creators;

import com.bjit.common.rest.app.service.classificationPath.ClassificationPathService;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.bjit.common.rest.app.service.classificationPath.ClassificationPathServiceImpl;
import com.bjit.common.rest.app.service.constants.VaultAndPolicyConstants;
import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectAttributes;
import matrix.db.Context;

public class MVItemCreator extends CommonItemCreator {

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger
            .getLogger(MVItemCreator.class);
    protected StringBuilder errorMessages = null;
    protected ClassificationPathService classificationPathService = new ClassificationPathServiceImpl();

    /**
     * processCreateItemOperation
     *
     * @param commonItemParemeters
     * @return HashMap<String, String>
     * @throws com.matrixone.apps.domain.util.FrameworkException
     * @throws Exception
     */
    @Override
    public String processCreateItemOperation(CommonItemParameters commonItemParemeters) throws Exception {
        logger.info("\n+++  processMVCreateItemOperation +++");
        this.commonItemParemeters = commonItemParemeters;
        this.errorMessages = new StringBuilder();
        return this.processCreateItemOperation(commonItemParemeters.getContext(),
                commonItemParemeters.getBusinessObjectUtil(), commonItemParemeters.getBusinessObjectOperations(),
                commonItemParemeters.getCreateObjectBean(), commonItemParemeters.getResponseMessageFormaterBean(),
                commonItemParemeters.getRunTimeInterfaceList(), Boolean.TRUE);
    }

    /**
     * processCreateItemOperation
     *
     * @param context
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createBean
     * @param responseMessageFormatterBean
     * @param interfaceList
     * @param checkInterfacesExistence
     * @return HashMap<String, String>
     * @throws com.matrixone.apps.domain.util.FrameworkException
     * @throws Exception
     */
    public String processCreateItemOperation(Context context, BusinessObjectUtil businessObjectUtil,
            BusinessObjectOperations businessObjectOperations, CreateObjectBean createBean,
            ResponseMessageFormaterBean responseMessageFormatterBean, List<String> interfaceList,
            Boolean checkInterfacesExistence) throws FrameworkException, Exception {

        try {
            this.checkInterfaceExistence = checkInterfacesExistence;
            businessObjectinterfaceList = !NullOrEmptyChecker.isNullOrEmpty(interfaceList) ? interfaceList : null;
            String newlyCreatedItemsObjectId = this.processCreateItemOperation(context, businessObjectUtil,
                    businessObjectOperations, createBean, responseMessageFormatterBean);
            return newlyCreatedItemsObjectId;
        } catch (FrameworkException exp) {
            logger.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            logger.error(exp.getMessage());
            throw exp;
        }
    }

    /**
     * validated requested attributes, in case of invalid attribute add to
     * warning message and remove from request attribute
     *
     * @param requestedAttributesMap
     * @param validAttributes
     * @return HashMap<String, String>
     * @throws Exception
     */
    public HashMap<String, String> getValidAttributesFromRequestedAttributes(HashMap<String, String> requestedAttributesMap, Set<String> validAttributes) throws Exception {
        logger.info("\n+++   getValidAttributesFromRequestedAttributes   +++");
        HashMap<String, String> finalRequestedAttributesMap = (HashMap<String, String>) requestedAttributesMap.clone();
        try {
            Set<String> unAssignedAttributes = new LinkedHashSet<String>();
            Set<String> invalidAttributes = new LinkedHashSet<String>();
            HashMap<String, String> destinationSourceMap = commonItemParemeters.getDestinationSourceMap();
            logger.info("\ndestinationSourceMap: " + destinationSourceMap + "\n\n");

            List<String> alwaysInsertable = commonItemParemeters.getInsertableProperties();
            logger.info("\nalwaysInsertable: " + alwaysInsertable);

            requestedAttributesMap.forEach((attributeOrProperty, attributeOrPropertyValue) -> {
                boolean isValidAttribute = validAttributes.contains(attributeOrProperty);
                boolean isAlwaysInsertable = alwaysInsertable.contains(attributeOrProperty);

                if (!isValidAttribute && !isAlwaysInsertable) {
                    boolean attributeUpdateRequested = !attributeOrPropertyValue.isEmpty();
                    boolean configuredEnoviaAttribute = destinationSourceMap.containsKey(attributeOrProperty) && !destinationSourceMap.get(attributeOrProperty).isEmpty();

                    if (attributeUpdateRequested && configuredEnoviaAttribute) {
                        unAssignedAttributes.add(destinationSourceMap.get(attributeOrProperty));
                        logger.info("\nRemoving Unassigned Attribute : " + attributeOrProperty);
                    } else if (attributeUpdateRequested && !configuredEnoviaAttribute) {
                        invalidAttributes.add(attributeOrProperty);
                        logger.info("\nRemoving Invalid Attribute : " + attributeOrProperty);
                    } else {
                        logger.info("\nRemoving Other Empty value Attribute : " + attributeOrProperty);
                    }

                    finalRequestedAttributesMap.remove(attributeOrProperty);
                }
            });

            if (unAssignedAttributes.size() > 0) {
                errorMessages.append(" Object's unavailable attributes: ");
                for (String unAssignedAttribute : unAssignedAttributes) {
                    errorMessages.append("'" + unAssignedAttribute + "', ");
                }
                errorMessages.append("; ");
            }
            if (invalidAttributes.size() > 0) {
                errorMessages.append(" Unsupported Attributes: ");
                for (String invalidAttribute : invalidAttributes) {
                    errorMessages.append("'" + invalidAttribute + "', ");
                }
                errorMessages.append(". ");
            }

            logger.info("\nfinalRequestedAttributesMap :" + finalRequestedAttributesMap);

        } catch (Exception e) {
            logger
                    .error("getValidAttributesFromRequestedAttributes , error : " + e.getMessage());
            throw e;
        }
        logger.info("\n---   getValidAttributesFromRequestedAttributes   ---");
        return finalRequestedAttributesMap;
    }

    /**
     * Process Create Item Operation for Products
     *
     * @param context
     * @param businessObjectUtil
     * @param businessObjectOperations
     * @param createObjectBean
     * @param responseMessageFormatterBean
     * @return HashMap<String, String>
     * @throws com.matrixone.apps.domain.util.FrameworkException
     * @throws Exception
     */
    public String processCreateItemOperation(final Context context, BusinessObjectUtil businessObjectUtil,
            BusinessObjectOperations businessObjectOperations, CreateObjectBean createObjectBean,
            ResponseMessageFormaterBean responseMessageFormatterBean) throws FrameworkException, Exception {
        try {

            createObjectBean.getTnr()
                    .setName(createObjectBean.getIsAutoName()
                            ? businessObjectOperations.getAutoName(context, createObjectBean.getTnr().getType())
                            : createObjectBean.getTnr().getName());

            Boolean objectIsExists = Optional.ofNullable(commonItemParemeters.getItemExists()).orElse(Boolean.FALSE);
            String objectId = Optional.ofNullable(commonItemParemeters.getObjectId()).orElse("");

            objectId = !objectIsExists ? businessObjectOperations.createObject(context, createObjectBean.getTnr(),
                    VaultAndPolicyConstants.getObjectVault(createObjectBean.getTnr().getType()),
                    VaultAndPolicyConstants.getObjectPolicy(createObjectBean.getTnr().getType())) : objectId;
            String modelVersionType = createObjectBean.getTnr().getType();

            if (!modelVersionType.equals("Model")) {
                MVCreateObjectBean hpCreateObjectBean = (MVCreateObjectBean) createObjectBean;
                String classificationPath = hpCreateObjectBean.getClassificationPath();

                logger.info("\nclassificationPath: " + classificationPath);
                if (classificationPath != null) {
                    String resultClassificationPath = classificationPathService.addClassificationPath(context, objectId,
                            classificationPath);
                    if (!resultClassificationPath.isEmpty()) {
                        errorMessages.append(resultClassificationPath + ", ");
                    }
                    logger.info("\nClassificationPath addition result: " + resultClassificationPath);
                }
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(businessObjectinterfaceList)) {
                businessObjectOperations.addInterface(context, objectId, businessObjectinterfaceList, "",
                        checkInterfaceExistence);
            } else if (!NullOrEmptyChecker.isNullOrEmpty(businessObjectinterfaceName)) {
                businessObjectOperations.addInterface(context, objectId, businessObjectinterfaceName, "",
                        checkInterfaceExistence);
            }

            BusinessObject busObject = new BusinessObject(objectId);
            busObject.open(context);
            BusinessObjectAttributes busAttributes = busObject.getAttributes(context);
            AttributeList attributeList = busAttributes.getAttributes();
            LinkedHashSet<String> validAttributes = new LinkedHashSet<String>();
            for (Attribute attribute : attributeList) {
                String attributeName = attribute.getName();
                validAttributes.add(attributeName);
            }
            busObject.close(context);

            HashMap<String, String> updatedAttributeMap = objectIsExists ? this.validateUpdateAttributeMap(createObjectBean) : this.addDefaultAttributeValues(createObjectBean);

            logger.info("\n updatedAttributeMap " + updatedAttributeMap);
            // validated requested attributes, in case of invalid attribute add to warning message and remove from request attribute
            updatedAttributeMap = this.getValidAttributesFromRequestedAttributes(updatedAttributeMap, validAttributes);
            if (!errorMessages.toString().isEmpty()) {
                responseMessageFormatterBean.setErrorMessage(errorMessages.toString());
            }

            businessObjectOperations.updateObject(context, objectId, updatedAttributeMap);

            logger.info("\n---  processMVCreateItemOperation ---");
            return objectId;
        } catch (FrameworkException exp) {
            logger.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            logger.error(exp.getMessage());
            throw exp;
        }

    }

    /**
     * To validate attribute map during update
     *
     * @param createObjectBean
     * @return
     */
    protected HashMap<String, String> validateUpdateAttributeMap(CreateObjectBean createObjectBean) {
        if (createObjectBean.getAttributes().containsKey("PLMEntity.V_nature")) {
            throw new RuntimeException("Please don't update 'PLMEntity.V_nature'");
        }

        return createObjectBean.getAttributes();
    }

    /**
     * Adding default attribute values
     *
     * @param createObjectBean
     * @return
     * @throws FrameworkException
     * @throws InterruptedException
     */
    protected HashMap<String, String> addDefaultAttributeValues(CreateObjectBean createObjectBean) throws FrameworkException, InterruptedException {
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();
        HashMap<String, String> objectUpdateMap = objectDefaultValues.getObjectUpdateMap(createObjectBean);
        createObjectBean.setAttributes(objectUpdateMap);
        objectUpdateMap = validateUpdateAttributeMap(createObjectBean);
        return objectUpdateMap;
    }
}
