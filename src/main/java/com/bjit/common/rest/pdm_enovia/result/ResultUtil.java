package com.bjit.common.rest.pdm_enovia.result;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.bjit.common.rest.pdm_enovia.utility.CommonUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public class ResultUtil {
    
    private static final Logger RESULT_UTIL_LOGGER = Logger.getLogger(ResultUtil.class);
    
    public Map<String, ResponseMessageFormaterBean> successResultMap = new HashMap<>();
    public Map<String, ResponseMessageFormaterBean> errorResultMap = new HashMap<>();
    public Map<String, TNR> itemTNRMap = new HashMap<>();
    public List<String> successfulCreateList = new ArrayList<>();
    public List<String> successfulUpdateList = new ArrayList<>();
    public List<String> itemListWith3dModels = new ArrayList<>();
    public List<String> fileNameList = new ArrayList<>();
    
    public void addSuccessResult(TNR successItemTNR, String successItemId) {
        ResponseMessageFormaterBean successResult = new ResponseMessageFormaterBean();
        RESULT_UTIL_LOGGER.debug("Item ID : " + successItemId);
        RESULT_UTIL_LOGGER.debug("TNR Type :" + successItemTNR.getType() + " Name : " + successItemTNR.getName() + " Revision : " + successItemTNR.getRevision());
        successResult.setTnr(successItemTNR);
        successResult.setObjectId(successItemId);
        successResultMap.put(successItemTNR.getName(), successResult);
        RESULT_UTIL_LOGGER.debug("SuccessMap : " + successResult);
    }
    
    public void addErrorResult(String errorKey, TNR errorItemTNR, String errorMessage) {
        if(errorResultMap.containsKey(errorKey)) {
            ResponseMessageFormaterBean errorBean = errorResultMap.get(errorKey);
            String previousErrorMessage = errorBean.getErrorMessage();
            String newErrorMessage = previousErrorMessage + "\n" + errorMessage;
            errorBean.setErrorMessage(newErrorMessage);
            errorResultMap.put(errorKey, errorBean);
        }
        else {
            ResponseMessageFormaterBean errorResult = new ResponseMessageFormaterBean();
            errorResult.setTnr(errorItemTNR);
            errorResult.setErrorMessage(errorMessage);
            errorResultMap.put(errorKey, errorResult);
        }
    }
    
    public void addErrorResult(String errorKey, TNR errorItemTNR, String itemId, String errorMessage) {
        if (errorResultMap.containsKey(errorKey)) {
            ResponseMessageFormaterBean errorBean = errorResultMap.get(errorKey);
            String previousErrorMessage = errorBean.getErrorMessage();
            String newErrorMessage = previousErrorMessage + "\n" + errorMessage;
            errorBean.setErrorMessage(newErrorMessage);
            errorBean.setObjectId(itemId);
            errorResultMap.put(errorKey, errorBean);
        } else {
            ResponseMessageFormaterBean errorResult = new ResponseMessageFormaterBean();
            errorResult.setTnr(errorItemTNR);
            errorResult.setObjectId(itemId);
            errorResult.setErrorMessage(errorMessage);
            errorResultMap.put(errorKey, errorResult);
        }
    }

    public void clearResultMaps() {
        this.successResultMap = new HashMap<>();
        this.errorResultMap = new HashMap<>();
        this.itemTNRMap = new HashMap<>();
        this.successfulCreateList = new ArrayList<>();
        this.successfulUpdateList = new ArrayList<>();
        this.itemListWith3dModels = new ArrayList<>();
        this.fileNameList = new ArrayList<>();
    }
    
    public void addItemTNR(String itemName, TNR itemTNR) {
        if(!NullOrEmptyChecker.isNullOrEmpty(itemName) && !NullOrEmptyChecker.isNull(itemTNR)) {
            itemTNRMap.put(itemName, itemTNR);
        }
    }
    
    public TNR getItemTNR(String itemName) {
        if(!NullOrEmptyChecker.isNullOrEmpty(itemName)) {
            if(itemTNRMap.containsKey(itemName)) {
                return itemTNRMap.get(itemName);
            }
        }
        return null;
    }
    
    public void moveAllFiles() {
        RESULT_UTIL_LOGGER.debug(">>>> Document file moving started.");
        String uplaodDirectory = PropertyReader.getProperty("stmt.checkin.file.upload.directory");
        String oldDirectory = PropertyReader.getProperty("stmt.checkin.file.history.directory");
        for(String fileName : fileNameList) {
            File file = new File(uplaodDirectory+fileName);
            if(file.exists()) {
                try {
                CommonUtil.moveFile(file, oldDirectory);
                } catch (Exception e) {
                    RESULT_UTIL_LOGGER.error(">>>> Error occured during transfering file "+fileName+": "+e.getMessage());
                }
            }
        }
        RESULT_UTIL_LOGGER.debug(">>>> Document file moving completed.");
    }
}