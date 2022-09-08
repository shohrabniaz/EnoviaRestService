/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.export.product;



import com.bjit.ewc18x.utils.CustomException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Suvonkar Kundu
 */

/**
 * Represents the attributes/lists and necessary configurations needed for
 * attributes. Every list represents different configurations. An attribute can
 * be included in different lists.
 */

public class AttributesForm {
    private int checkFile = 0;
    /**
     * The map that contains key and value of of each attribute. The key is the
     * MQL table header the value is the name to be shown in UI
     */
    private Map< String, String> attributeNameMap = new LinkedHashMap<>();
    
    private List<String> propertyNames = new ArrayList<>();
    private List<String> propertyNamesWS = new ArrayList<>();

    public List<String> getPropertyNamesWS() {
        return propertyNamesWS;
    }

    public void setPropertyNamesWS(List<String> propertyNamesWS) {
        this.propertyNamesWS = propertyNamesWS;
    }
    private List<String> notPropertyNotAttributeNames = new ArrayList<>();
    
    private Map< String, String> attributeName = new LinkedHashMap<>();;

    /**
     * The list that contains attributes to be selected by default
     */
    private List<String> defaultSelectedItem = new ArrayList<>();
    /**
     * The list that contains attributes names from MQL table header.
     */
    private List<String> tableHeaders = new ArrayList<>();
    /**
     * The list that contains attributes names for sending individual request
     * during update object. This list is needed because the web service could
     * not update all attributes if they are sent in list.
     */
    private List<String> singleRequestAttr = new ArrayList<>();
    /**
     * The list that contains attributes names for expand object xls writing.
     * The name of these attributes will not be changed while writing to xls.
     */
    private List<String> unchangeableItems = new ArrayList<>();
    /**
     * The list that contains attributes that cannot be updated via this
     * service.
     */
    private List<String> notUpdatableAttr = new ArrayList<>();
    /**
     * The list that contains properties that can be updated via this service
     * like description.
     */
    private List<String> updatableProperties = new ArrayList<>();

    /**
     * The list that contains properties that cannot be updated via this service
     * like id, TNR, depth.
     */
    private List<String> notUpdatableProperties = new ArrayList<>();

    private HashMap<String, String> relationshipAttr = new HashMap();

    private HashMap<String, String> relationshipAttrRelName = new HashMap();
    
    private List<String> relationshipAttrName = new ArrayList<>();
    
    private final int uiAttributeColumn = 0;
    private final int tableHeaderColumn = 1;
    private final int isProperty = 2;
    private final int isNotPropertyNotAttribute = 3;    
    private final int defaultSelectedColumn = 4;
    private final int unchangeableItemsColumn = 5;

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AttributesForm.class);

    public void readValues(String filename) throws CustomException {
        logger.debug("In readValues() method. File Name : " + filename);
        try {
            logger.debug("Reading File for attribute list.");
            File file = new File(getClass().getResource("/" + filename).getFile());
//            File file = new File(filename);
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                System.out.println(line);
                if (line.charAt(0) == '#') {
                    continue;
                }

                String[] temp = line.split(":");

                attributeNameMap.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
                attributeName.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
                //propertyNames.add(temp[uiAttributeColumn]);
                
                tableHeaders.add(temp[tableHeaderColumn]);
                if (temp[defaultSelectedColumn].equalsIgnoreCase("true")) {
                    defaultSelectedItem.add(temp[tableHeaderColumn]);
                }
                if (temp[unchangeableItemsColumn].equalsIgnoreCase("true")) {
                    unchangeableItems.add(temp[tableHeaderColumn]);
                }

                //the attribute 'state' is actually 'current'
                if(temp[tableHeaderColumn].equalsIgnoreCase("state")) {
                    temp[tableHeaderColumn] = "current";
                }
                if(temp[tableHeaderColumn].equalsIgnoreCase("objectId")) {
                    temp[tableHeaderColumn] = "id";
                }
                if (temp[isProperty].equalsIgnoreCase("TRUE")) {
                    propertyNames.add(temp[uiAttributeColumn]);
                    propertyNamesWS.add(temp[tableHeaderColumn]);
                }
                if (temp[isNotPropertyNotAttribute].equalsIgnoreCase("TRUE")) {
                  notPropertyNotAttributeNames.add(temp[uiAttributeColumn]);
                }                
            }
            input.close();

        } catch (FileNotFoundException | NullPointerException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Attribute configuration not found.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not read attribute configuration.");
        }

    }
    public List<String> getTableHeaders() {
        return tableHeaders;
    }

    public void setTableHeaders(List<String> tableHeaders) {
        this.tableHeaders = tableHeaders;
    }

    public int getCheckFile() {
        return checkFile;
    }

    public void setCheckFile(int value) {
        this.checkFile = value;
    }

    public List<String> getDefaultSelectedItem() {
        return defaultSelectedItem;
    }

    public void setDefaultSelectedItem(List<String> defaultSelectedItem) {
        this.defaultSelectedItem = defaultSelectedItem;
    }

    public Map<String, String> getAttributeNameMap() {
        return attributeNameMap;
    }

    public void setAttributeNameMap(Map<String, String> attributeNameMap) {
        this.attributeNameMap = attributeNameMap;
    }

    public List<String> getUnchangeableItems() {
        return unchangeableItems;
    }

    public void setUnchangeableItems(List<String> unchangeableItems) {
        this.unchangeableItems = unchangeableItems;
    }

    public List<String> getSingleRequestAttr() {
        return singleRequestAttr;
    }

    public void setSingleRequestAttr(List<String> singleRequestAttr) {
        this.singleRequestAttr = singleRequestAttr;
    }

    public List<String> getNotUpdatableAttr() {
        return notUpdatableAttr;
    }

    public void setNotUpdatableAttr(List<String> notUpdatableAttr) {
        this.notUpdatableAttr = notUpdatableAttr;
    }

    /**
     * This function provide the list of properties that can be update
     * @return List<String> updatable properties list
     */
    public List<String> getUpdatableProperties() {
        return updatableProperties;
    }

    /**
     * This function set the list of properties that can be update
     * @param updatableProperties List of properties that can be update
     */
    public void setUpdatableProperties(List<String> updatableProperties) {
        this.updatableProperties = updatableProperties;
    }

    /**
     * This function provide the list of properties that can not be update
     * @return List<String> not updatable properties list
     */
    public List<String> getNotUpdatableProperties() {
        return notUpdatableProperties;
    }

    /**
     * This function set the list of properties that can not be update
     * @param notUpdatableProperties List of properties that can not be update
     */
    public void setNotUpdatableProperties(List<String> notUpdatableProperties) {
        this.notUpdatableProperties = notUpdatableProperties;
    }

    public HashMap<String, String> getRelationshipAttr() {
        return relationshipAttr;
    }

    public void setRelationshipAttr(HashMap<String, String> relationshipAttr) {
        this.relationshipAttr = relationshipAttr;
    }

    public HashMap<String, String> getRelationshipAttrRelName() {
        return relationshipAttrRelName;
    }

    public void setRelationshipAttrRelName(HashMap<String, String> relationshipAttrRelName) {
        this.relationshipAttrRelName = relationshipAttrRelName;
    }

    public List<String> getRelationshipAttrName() {
        return relationshipAttrName;
    }

    public void setRelationshipAttrName(List<String> relationshipAttrName) {
        this.relationshipAttrName = relationshipAttrName;
    }
    
    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(List<String> propertyNames) {
        this.propertyNames = propertyNames;
    }

    public List<String> getNotPropertyNotAttributeNames() {
        return notPropertyNotAttributeNames;
    }

    public void setNotPropertyNotAttributeNames(List<String> notPropertyNotAttributeNames) {
        this.notPropertyNotAttributeNames = notPropertyNotAttributeNames;
    }
    
    
    public Map<String, String> getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(Map<String, String> attributeName) {
        this.attributeName = attributeName;
    }

}
