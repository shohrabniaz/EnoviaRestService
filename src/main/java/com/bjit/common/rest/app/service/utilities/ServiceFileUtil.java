/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.model.export.product.Attribute;
import com.bjit.common.rest.app.service.model.export.product.AttributeList;
import com.bjit.common.rest.app.service.model.export.product.AttributesForm;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Class Contains Service file related functionalities
 *
 * @author Suvonkar Kundu
 */
public class ServiceFileUtil {

    /**
     * Logger to write the error message in log file
     */
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ServiceFileUtil.class);

    //** Service File*/
    private File serviceFile;

    /**
     * Attribute List
     */
    AttributeList attributeList;

    Map<String, String> attributeMap;

    /**
     * attributes actual name in db and display name from UI map
     */
    Map<String, String> attrActualNameAndDisplayNameMap;

    /**
     * Property display name list
     */
    private List<String> propertyNameList = new ArrayList<>();

    /**
     * not Property And Attribute name list eg: Classification Path
     */
    private List<String> notPropertyAndAttributeNameList;

    /**
     * Default selected attribute list
     */
    private List<String> selectedAttributeList;



    public ServiceFileUtil() {
    }
    private File getAttributeConfFile(String attributeFile)  {
        File attributeConfFile = new File(getClass().getResource("/" + attributeFile).getFile());
        if (!attributeConfFile.exists()) {
            logger.error(" File doesn't exists !! ");
        }
        return attributeConfFile;
    }



    public void readAttributes() throws CustomException {
        String attributeFile = "common/AttributesL.conf";
        File attributeConfFile = getAttributeConfFile(attributeFile);
        attributeList = unMarshalingAttributeConf(attributeConfFile);
        attrActualNameAndDisplayNameMap = new HashMap<>();
        propertyNameList = new ArrayList<>();
        notPropertyAndAttributeNameList = new ArrayList<>();
        selectedAttributeList = new ArrayList<>();
        for (Attribute attribute : attributeList.getList()) {
            attrActualNameAndDisplayNameMap.put(attribute.getAttributeValue(), attribute.getAttributeName());
            if (attribute.isProperty() == true) {
                propertyNameList.add(attribute.getAttributeName());
            }
            if (attribute.isNotPropertyAndAttribute() == true) {
                notPropertyAndAttributeNameList.add(attribute.getAttributeName());
            }

            if (attribute.isSelected()) {
                selectedAttributeList.add(attribute.getAttributeName());
            }
        }
        System.out.println("==========");
    }
    
    /**
     * This method is using to read attributes value from attributes.conf file
     * and property value set true
     *
     * @param File it is attribute file
     * @return AttributeList that contain attribute value
     */
    private AttributeList unMarshalingAttributeConf(File attributeConfFile) throws CustomException {
        try {
            AttributesForm attributesForm = new AttributesForm();
            List<String> propertyList = attributesForm.getPropertyNamesWS();
            AttributeList attributeList = new AttributeList();
            attributesForm.readValues("common/"+attributeConfFile.getName());
            Map<String, String> attributesMap = attributesForm.getAttributeName();
            Map<String, String> attributeNameMap = attributesForm.getAttributeName();
            Set<String> keys = attributesMap.keySet();
            for (String key : keys) {
                String attributeValue = attributeNameMap.get(key);
                Attribute attribute = new Attribute();
                attribute.setAttributeName(key);
                attribute.setAttributeValue(attributeValue);
                if (propertyList.contains(key)) {
                    attribute.setProperty(true);
                }
                attributeList.add(attribute);
            }
            return attributeList;
        } catch (Exception ex) {
            Logger.getLogger(ServiceFileUtil.class.getName()).log(Level.SEVERE, null, ex);
            String errorMessage = "Attribute Configuration file unmershal exception occured. File Name : " + attributeConfFile.getName();
            logger.error(errorMessage);
            throw new CustomException(ex.getMessage(), errorMessage);
        }
    }


    public Map<String, String> getAttrActualNameAndDisplayNameMap() {
        return attrActualNameAndDisplayNameMap;
    }
    public List<String> getPropertyNameList() {
        return propertyNameList;
    }

    public List<String> getNotPropertyAndAttributeNameList() {
        return notPropertyAndAttributeNameList;
    }

    public File getServiceFile() {
        return serviceFile;
    }

    public List<String> getSelectedAttributeList() {
        return selectedAttributeList;
    }

}
