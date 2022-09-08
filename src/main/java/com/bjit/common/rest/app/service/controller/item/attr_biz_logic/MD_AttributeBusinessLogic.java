/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.item.attr_biz_logic;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.mapper.mapproject.util.Constants;
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
public class MD_AttributeBusinessLogic extends MV_AttributeBusinessLogic {

    private static final Logger logger = Logger.getLogger(MD_AttributeBusinessLogic.class);

    /**
     * Basic Business logic is implemented here
     *
     * @param mapper xml item object mapper
     * @param createObjectBean item data is kept here
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    @Override
    public HashMap<String, String> businessLogic(ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        HashMap<String, String> newObjectAttributeMap = new HashMap<>();
        HashMap<String, String> objectAttributeMap = createObjectBean.getAttributes();
        ObjectDefaultValues objectDefaultValues = new ObjectDefaultValues();

        logger.info("Processing '" + createObjectBean.getTnr().getType() + "' type object. Name is '" + createObjectBean.getTnr().getName() + "' and Revision is '" + createObjectBean.getTnr().getRevision() + "'");

        switch (createObjectBean.getTnr().getType()) {
            case Constants.MEDICAL_DEVICE_PRODUCT:
                objectAttributeMap = super.itemAttributeBusinessLogic(createObjectBean, mapper, objectAttributeMap, newObjectAttributeMap);
                objectDefaultValues.getObjectUpdateMap(createObjectBean);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

    /**
     * During Item create some business logic are implemented here
     *
     * @param context
     * @param businessObjectUtil
     * @param mapper
     * @param createObjectBean
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     */
    @Override
    public HashMap<String, String> businessLogic(Context context, BusinessObjectUtil businessObjectUtil, ItemImportMapping mapper, CreateObjectBean createObjectBean) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, FrameworkException {

        HashMap<String, String> objectAttributeMap = this.businessLogic(mapper, createObjectBean);

        switch (createObjectBean.getTnr().getType()) {
            case Constants.MEDICAL_DEVICE_PRODUCT:
                String organization = businessObjectUtil.getPersonOrganization(context, objectAttributeMap.get("project"));
                objectAttributeMap.put("organization", organization);
                break;
            default:
                throw new RuntimeException("Provided Type '" + createObjectBean.getTnr().getType() + "' is not supported by the system");
        }

        return objectAttributeMap;
    }

}
