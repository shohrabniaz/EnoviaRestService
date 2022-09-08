/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.autoname.service;

import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @created 2021-04-06
 * @author Sudeepta
 */
@Service
public class AutonameGenerateServiceImpl implements AutonameGenerateService{
    
    /* Logger variable for this class */
    private static final Logger LOGGER = Logger.getLogger(AutonameGenerateServiceImpl.class);

    /* BusinessObjectOperations local Instance variable */
    private BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
    
    /* Context local Instance variable */
    private Context context;

    /**
     * Set user context
     * @param context
     */
    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Generate auto name depend on user parameter and return Autoname as String
     * 
     * @param type Object type
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public String getAutonameByType(String type) throws Exception {
        return getAutonameByType(type, "", "", "");
    }

    /**
     * Generate auto name depend on user parameter and return Autoname as String
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public String getAutonameByType(String type, String prefix) throws Exception {
        return getAutonameByType(type, prefix, "", "");
    }

    /**
     * Generate auto name depend on user parameter and return Autoname as String
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @param affix object name affix
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public String getAutonameByType(String type, String prefix, String affix) throws Exception {
        return getAutonameByType(type, prefix, affix, "");
    }

    /**
     * Generate auto name depend on user parameter and return Autoname as String
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @param affix object name affix
     * @param suffix object name suffix
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public String getAutonameByType(String type, String prefix, String affix, String suffix) throws Exception {
        return getAutonameListByType(type, prefix, affix, suffix, 1).get(0);
    }

    /**
     * Generate auto name depend on user parameter and return List of Autoname
     * 
     * @param type Object type
     * @param count the number of object name will be generated
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public List<String> getAutonameListByType(String type, int count) throws Exception {
        return getAutonameListByType(type, "", "", "", count);
    }

    /**
     * Generate auto name depend on user parameter and return List of Autoname
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @param count the number of object name will be generated
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public List<String> getAutonameListByType(String type, String prefix, int count) throws Exception {
        return getAutonameListByType(type, prefix, "", "", count);
    }

    /**
     * Generate auto name depend on user parameter and return List of Autoname
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @param affix object name affix
     * @param count the number of object name will be generated
     * @return generated autoname
     * @throws Exception
     */
    @Override
    public List<String> getAutonameListByType(String type, String prefix, String affix, int count) throws Exception {
        return getAutonameListByType(type, prefix, affix, "", count);
    }

    /**
     * Generate auto name depend on user parameter and return List of Autoname
     * 
     * @param type Object type
     * @param prefix object name prefix
     * @param affix object name affix
     * @param suffix object name suffix
     * @param count the number of object name will be generated
     * @return generated autoname and return as List of String
     * @throws Exception
     */
    @Override
    public List<String> getAutonameListByType(String type, String prefix, String affix, String suffix, int count) throws Exception {
        List<String> autonameSequenceNumberList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            autonameSequenceNumberList.add(getAutonameSequenceNumberByType(type));
        }
        String catiaSupportedTypes = PropertyReader.getProperty("generate.auto_name.type.support.catia");
        List<String> catiaSupportedTypeList = Arrays.asList(catiaSupportedTypes.split(","));
        if(catiaSupportedTypeList.contains(type)) {
            autonameSequenceNumberList = getSequenceNumberListFromGeneratedNameListForCatiaType(autonameSequenceNumberList);
        } else {
            autonameSequenceNumberList = getSequenceNumberListFromGeneratedNameListForOtherType(autonameSequenceNumberList);
        }
        List<String> autonameList = generateAutonameBySequenceNumber(autonameSequenceNumberList, prefix, affix, suffix);
        LOGGER.info("Process: AutonameGeneraton, type: " + type + ", NameList: " + autonameSequenceNumberList.toString());
        return autonameList;
    }

    private String getAutonameSequenceNumberByType(String type) throws Exception {
        try {
            return businessObjectOperations.getAutoName(context, type);
        } catch (Exception ex) {
            LOGGER.error("Autoname generation error. message: " + ex.getMessage());
            java.util.logging.Logger.getLogger(AutonameGenerateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Sequence Number generation error for auto name. Error Message: " + ex.getMessage());
        }
    }

    private List<String> getSequenceNumberListFromGeneratedNameListForOtherType(List<String> nameList) {
        List<String> sequenceNumberList = new ArrayList<>();
        int sequenceNumberPosition = Integer.parseInt(PropertyReader.getProperty("generate.auto_name.position.other"));
        String separator = PropertyReader.getProperty("generate.auto_name.separator");
        for (String name : nameList) {
            sequenceNumberList.add(name.split(separator)[sequenceNumberPosition]);
        }
        return sequenceNumberList;
    }

    private List<String> getSequenceNumberListFromGeneratedNameListForCatiaType(List<String> nameList) {
        List<String> sequenceNumberList = new ArrayList<>();
        int sequenceNumberPosition = Integer.parseInt(PropertyReader.getProperty("generate.auto_name.position.catia"));
        String separator = PropertyReader.getProperty("generate.auto_name.separator");
        for (String name : nameList) {
            sequenceNumberList.add(name.split(separator)[sequenceNumberPosition]);
        }
        return sequenceNumberList;
    }

    private List<String> generateAutonameBySequenceNumber(List<String> sequenceNumberList, String prefix, String affix, String suffix) {
        List<String> autonameList = new ArrayList<>();
        String separator = PropertyReader.getProperty("generate.auto_name.separator");
        for (String sequenceNumber : sequenceNumberList) {
            String autoname = "";
            if (isNotNullAndNotEmpty(prefix)) {
                autoname = prefix + separator;
            }
            if (isNotNullAndNotEmpty(affix)) {
                autoname += affix + separator;
            }
            autoname += sequenceNumber;
            if (isNotNullAndNotEmpty(suffix)) {
                autoname += separator + suffix;
            }
            autonameList.add(autoname);
        }
        return autonameList;
    }

    private boolean isNotNullAndNotEmpty(String string) {
        return string != null && !string.isEmpty();
    }
}
