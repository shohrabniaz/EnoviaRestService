/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.attrBizLogic;

import com.bjit.common.rest.app.service.comos.defaultvalues.ComosDefaultValues;
import com.bjit.common.rest.app.service.controller.item.attr_biz_logic.CommonAttributeBusinessLogic;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.matrixone.apps.domain.util.FrameworkException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author BJIT
 */
public class ComosAttributeBusinessLogic extends CommonAttributeBusinessLogic {

    private static final Logger COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER = Logger.getLogger(CommonAttributeBusinessLogic.class);

    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ComosDefaultValues objectDefaultValues = new ComosDefaultValues();

        COMMON_ATTRIBUTE_BUSINESS_LOGIC_LOGGER.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case "Plant":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Mill":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Unit":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Sub Unit":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Device Position":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            case "Schema_Log":
                objectAttributeMap = itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    @Override
    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = businessLogic(mapper, createObjectBean);

        String organization = "";
        switch (createObjectBean.getTnr().getType()) {
            case "Plant":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            case "Mill":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            case "Unit":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            case "Sub Unit":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            case "Device Position":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            case "Schema_Log":
//                organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
//                objectAttributeMap.put("organization", organization);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }
}
