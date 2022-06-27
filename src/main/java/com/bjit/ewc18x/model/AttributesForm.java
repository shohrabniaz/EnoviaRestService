/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;


import com.bjit.ewc18x.utils.CustomException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Euna
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
    private final int singleRequestAttrColumn = 5;
    private final int unchangeableItemsColumn = 6;
    private final int isUpdatableColumn = 7;
    private final int relationshipAttrColumn = 8;
    private final int relationshipNameColumn = 9;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AttributesForm.class);

     /**
     * reads attribute configurations from file and adds necessary
     * configurations to the lists
     *
     * @param filename
     * @throws com.bjit.ewc.utils.CustomException
     * follow the configuration format
     */
     /**
     * reads attribute configurations from file and adds necessary
     * configurations to the lists
     *
     * @throws FileNotFoundException if configuration file is not found
     * @throws ArrayIndexOutOfBoundsException if configuration file does not
     * follow the configuration format
     */

    public void readValues() throws CustomException {
        logger.debug("In readValues() method.");
        try {
            logger.debug("Reading File for attribute list.");
            File file = new File(getClass().getResource("/Attributes.conf").getFile());
            logger.debug("found attribute list:"+file);
            Scanner input = new Scanner(file);

            attributeNameMap = new LinkedHashMap<>();
            defaultSelectedItem = new ArrayList<>();
            tableHeaders = new ArrayList<>();
            singleRequestAttr =  new ArrayList<>();
            notUpdatableProperties = new ArrayList<>();
            unchangeableItems = new ArrayList<>();
            notUpdatableAttr =  new ArrayList<>();
            while (input.hasNextLine()) {
                String line = input.nextLine();
                logger.debug("Found line: "+line);
                if (line.charAt(0) == '#') {
                    continue;
                }

                String[] temp = line.split(":");

                attributeNameMap.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
                tableHeaders.add(temp[tableHeaderColumn]);
                if (temp[defaultSelectedColumn].equalsIgnoreCase("true")) {
                    defaultSelectedItem.add(temp[uiAttributeColumn]);
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
                //the following lists are needed for update
                if (temp[singleRequestAttrColumn].equalsIgnoreCase("true")) {
                    singleRequestAttr.add(temp[uiAttributeColumn]);
                }
                if (temp[isUpdatableColumn].equalsIgnoreCase("na")) {
                    notUpdatableProperties.add(temp[tableHeaderColumn]);
                }

                else {
                    if (temp[isUpdatableColumn].equalsIgnoreCase("false")) {
                        notUpdatableAttr.add(temp[tableHeaderColumn]);
                    }
                }
            }
            input.close();
            //System.out.println("attributes: "+attributeNameMap.toString());
             //System.out.println("defaultSelectedItem: "+defaultSelectedItem.toString());
           // readExtensionAttributes();
        } catch (FileNotFoundException | NullPointerException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Attribute configuration not found.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not read attribute configuration.");
        }

    }
    
    
    public void readExportBOMAttrValues() throws CustomException {
        logger.debug("In readValues() method.");
        try {
            logger.debug("Reading File for attribute list.");
            File file = new File(getClass().getResource("/common/Attributes_BOM_Export_TEST.conf").getFile());
            logger.debug("found attribute list:"+file);
            Scanner input = new Scanner(file);

            attributeNameMap = new LinkedHashMap<>();
            defaultSelectedItem = new ArrayList<>();
            tableHeaders = new ArrayList<>();
            singleRequestAttr =  new ArrayList<>();
            notUpdatableProperties = new ArrayList<>();
            unchangeableItems = new ArrayList<>();
            notUpdatableAttr =  new ArrayList<>();
            while (input.hasNextLine()) {
                String line = input.nextLine();
                logger.debug("Found line: "+line);
                if (line.charAt(0) == '#') {
                    continue;
                }

                String[] temp = line.split(":");

                attributeNameMap.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
                tableHeaders.add(temp[tableHeaderColumn]);
                if (temp[defaultSelectedColumn].equalsIgnoreCase("true")) {
                    defaultSelectedItem.add(temp[uiAttributeColumn]);
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
                //the following lists are needed for update
                if (temp[singleRequestAttrColumn].equalsIgnoreCase("true")) {
                    singleRequestAttr.add(temp[uiAttributeColumn]);
                }
                if (temp[isUpdatableColumn].equalsIgnoreCase("na")) {
                    notUpdatableProperties.add(temp[tableHeaderColumn]);
                }

                else {
                    if (temp[isUpdatableColumn].equalsIgnoreCase("false")) {
                        notUpdatableAttr.add(temp[tableHeaderColumn]);
                    }
                }
            }
            input.close();
            //System.out.println("attributes: "+attributeNameMap.toString());
             //System.out.println("defaultSelectedItem: "+defaultSelectedItem.toString());
           // readExtensionAttributes();
        } catch (FileNotFoundException | NullPointerException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Attribute configuration not found.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not read attribute configuration.");
        }

    }
    
    

    public void readValues(String filename) throws CustomException, IOException {
        logger.debug("In readValues() method.");
        try {
            logger.debug("Reading File for attribute list.");
            //System.out.println("config file existing path : " + getClass().getResource("/" + filename));
            //File file = new File(getClass().getResource("/" + filename).getFile());
            File file = new File(getClass().getClassLoader().getResource("/common/"+filename).getFile());
            try (Scanner input = new Scanner(file)) {
                while (input.hasNextLine()) {
                    String line = input.nextLine();
                    if (line.charAt(0) == '#') {
                        continue;
                    }
                    String[] temp = line.split(":");
                    System.out.println(">>>>> table header column : " + temp[tableHeaderColumn] + "; ui attr col : " + temp[uiAttributeColumn]);
                    attributeNameMap.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
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
                    //the following lists are needed for update
                    if (temp[singleRequestAttrColumn].equalsIgnoreCase("true")) {
                        singleRequestAttr.add(temp[uiAttributeColumn]);
                    }
                    if (temp[isUpdatableColumn].equalsIgnoreCase("na")) {
                        notUpdatableProperties.add(temp[tableHeaderColumn]);
                    }
                    else {
                        if (temp[isUpdatableColumn].equalsIgnoreCase("false")) {
                            notUpdatableAttr.add(temp[tableHeaderColumn]);
                        }
                    }
                    if (temp[isUpdatableColumn].equalsIgnoreCase("NA") || temp[isUpdatableColumn].equalsIgnoreCase("FALSE")) {
                        propertyNames.add(temp[uiAttributeColumn]);
                        propertyNamesWS.add(temp[tableHeaderColumn]);
                    }
                    if (temp[isNotPropertyNotAttribute].equalsIgnoreCase("TRUE")) {
                        notPropertyNotAttributeNames.add(temp[uiAttributeColumn]);
                    }
                    if (!temp[relationshipAttrColumn].equalsIgnoreCase("NA")) {
                        relationshipAttr.put(temp[tableHeaderColumn], temp[relationshipAttrColumn]);
                    }
                    if (!temp[relationshipNameColumn].equalsIgnoreCase("NA")) {
                        relationshipAttrRelName.put(temp[uiAttributeColumn], temp[relationshipNameColumn]);
                        relationshipAttrName.add(temp[uiAttributeColumn]);
                    }
                }
                
                System.out.println("configuration file reading finished ...");
            }

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

     /**
     * reads Extension attribute from file and adds necessary configurations to
     * the lists
     *
     * @throws FileNotFoundException if configuration file is not found
     * @throws ArrayIndexOutOfBoundsException if configuration file does not
     * follow the configuration format
     */
    public void readExtensionAttributes() throws CustomException {
        logger.debug("In readExtendedAttributes() method.");
        try {
            logger.debug("Reading File for attribute list.");
            File file = new File(getClass().getResource("/ExtensionAttributes.txt").getFile());
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.charAt(0) == '#') {
                    continue;
                }

                String[] temp = line.split(":");

                attributeNameMap.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);
                singleRequestAttr.add(temp[uiAttributeColumn]);
                unchangeableItems.add(temp[tableHeaderColumn]);
                if (temp.length > 2) {
                    if (temp[defaultSelectedColumn].equalsIgnoreCase("true")) {
                        defaultSelectedItem.add(temp[tableHeaderColumn]);
                    }
                    if (temp.length > 3) {
                        if (temp[singleRequestAttrColumn].equalsIgnoreCase("false")) {
                            singleRequestAttr.remove(temp[uiAttributeColumn]);
                        }

                        if (temp.length > 4) {
                            if (temp[unchangeableItemsColumn].equalsIgnoreCase("false")) {
                                unchangeableItems.remove(temp[tableHeaderColumn]);
                            }
                            if (temp.length > 4) {
                                if (temp[isUpdatableColumn].equalsIgnoreCase("na")) {
                                    notUpdatableProperties.add(temp[tableHeaderColumn]);
                                } else if (temp[isUpdatableColumn].equalsIgnoreCase("false")) {
                                    notUpdatableAttr.add(temp[tableHeaderColumn]);
                                }
                            }
                        }
                    }
                }
            }
            input.close();
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Extension attribute configuration not found.");
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not read extension attribute configuration.");
        }
    }

     /**
     * append extension attributes in the respective configuration file at
     * runtime
     * @param key(MQL table header)
     * @param value(attribute's UI value)
     */
    public void appendExtensionAttribute(String key, String Value) throws CustomException {
        appendToFile(key, Value);

//        attributeNameMap.put(key, Value);
//        singleRequestAttr.add(Value);
//        unchangeableItems.add(Value);
    }

     /**
     * append extension attributes in the respective configuration file at
     * runtime
     *
     * @param key(MQL table header)
     * @param value(attribute's UI value)
     *
     * @throws FileNotFoundException if configuration file is not found
     * @throws IOException if I/O operation fails
     */
    public void appendToFile(String key, String Value) throws CustomException {
        try (FileWriter fw = new FileWriter(getClass().getResource("/ExtensionAttributes.txt").getFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
                out.println();
                out.print(key + ":" + Value + ":FALSE:FALSE:TRUE:TRUE:NA");
                out.close();
                bw.close();
                fw.close();
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not append new extension attribute to configuration.");
        } catch (IOException ex) {
           logger.debug(ex.getMessage());
           throw new CustomException("Could not append new extension attribute to configuration.");
        }
    }

     /**
     * reads extension attributes from the respective configuration file
     *
     * @return map with attribute key and ui name
     *
     * @throws FileNotFoundException if configuration file is not found
     */
    public Map<String, String> getExtensionAttributes() throws CustomException {
        logger.debug("In getExtensionAttributes() method.");
        Map<String, String> extensionAttr = new LinkedHashMap<>();
        try {
            logger.debug("Reading File for attribute list.");
            File file = new File(getClass().getResource("/ExtensionAttributes.txt").getFile());
            Scanner input = new Scanner(file);
            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.charAt(0) == '#') {
                    continue;
                }
                String[] temp = line.split(":");
                extensionAttr.put(temp[tableHeaderColumn], temp[uiAttributeColumn]);

            }
            input.close();
        } catch (FileNotFoundException ex) {
            logger.debug(ex.getMessage());
            throw new CustomException("Could not find extension attribute configuration.");
        }
        return extensionAttr;
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

    public List<String> getNotUpdatableProperties() {
        return notUpdatableProperties;
    }

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
    
    
}
