/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.creator;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.BusinessObjectUtility;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.HashMap;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public class CloneItem {

    private static final Logger CLONE_ITEM_LOGGER = Logger.getLogger(CloneItem.class);

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
            CLONE_ITEM_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String cloneObject(Context context, CreateObjectBean createObjectBean, HashMap objectCloneParametersMap, BusinessObjectUtility businessObjectUtility) throws Exception {
        businessObjectUtility.checkContext(context);
        String errorMessage;

        if (NullOrEmptyChecker.isNull(createObjectBean)) {

            errorMessage = "CreateObjectBean is Null";
            CLONE_ITEM_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (NullOrEmptyChecker.isNull(objectCloneParametersMap)) {

            errorMessage = "ObjectCloneParameterMap is Null";
            CLONE_ITEM_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        if (objectCloneParametersMap.isEmpty()) {

            errorMessage = "ObjectCloneParameterMap is Empty";
            CLONE_ITEM_LOGGER.error(errorMessage);
            throw new NullPointerException(errorMessage);
        }

        HashMap objectCreationParamatersMap = new HashMap();
        
        objectCreationParamatersMap.put("objectId", createObjectBean.getTemplateBusinessObjectId());
        CLONE_ITEM_LOGGER.debug("TemplateObject Id : " + createObjectBean.getTemplateBusinessObjectId());

        objectCreationParamatersMap.put("paramList", objectCloneParametersMap);
        CLONE_ITEM_LOGGER.debug("Parameter List : " + objectCloneParametersMap);

        if (!NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getCs())) {
            objectCreationParamatersMap.put("collaborationSpace", businessObjectUtility.getCollaborationSpace(createObjectBean.getCs()));
        }

        String initargs[] = {};
        String clonedObjectId;
        try {
            String jpoClassName = "CloneObjectUtil";
            String jpoMethodName = "cloneObject";

            CLONE_ITEM_LOGGER.info("JPO class name : " + jpoClassName);
            CLONE_ITEM_LOGGER.info("JPO method name : " + jpoMethodName);

            clonedObjectId = (String) JPO.invoke(context, jpoClassName, initargs,
                    jpoMethodName, JPO.packArgs(objectCreationParamatersMap), String.class);

            CLONE_ITEM_LOGGER.debug("Cloned object Id : " + clonedObjectId);
            return clonedObjectId;
        } catch (MatrixException exp) {
            CLONE_ITEM_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            CLONE_ITEM_LOGGER.error(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            CLONE_ITEM_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }
}
