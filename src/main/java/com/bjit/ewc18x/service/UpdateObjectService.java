/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.service;

import com.bjit.ewc18x.model.AttributesForm;
import com.bjit.ewc18x.model.UpdateObjectForm;
import com.bjit.ewc18x.utils.CustomException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.Context;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author Kayum-603
 */
public interface UpdateObjectService {
    public UpdateObjectForm populateEnvironment(UpdateObjectForm updateObjectForm) throws CustomException;
    public void setAttributeValues(File file, UpdateObjectForm updateObjectForm,  AttributesForm attributesForm, Context context) throws CustomException, FileNotFoundException; 
    public boolean checkId(List <HashMap <String,String>>Listofmap);
    public boolean checkTypeNameRevision (List <HashMap <String,String>>Listofmap);
    public String getInvalidAttributes();
    public List <HashMap <String,String>> addObjectIdInMap (List <HashMap <String,String>>Listofmap, Context context, UpdateObjectForm updateObjectForm) throws CustomException;
    public Map<String, List> sendUpdateRequest(UpdateObjectForm updateObjectForm, List<HashMap<String, String>> attributeListOfMaps,String isClassificationPath, AttributesForm attributesForm, Context context) throws CustomException ;
    public Map checkAvailableAttributes(Context context, Map<String,  String> objectMap, List<String> notUpdatableProperties,  List<String> notUpdatableAttr, List<String> allRelationshipAttrNameList) throws CustomException;
    public String updateClassificationPath(String objectID, String classificationPath, Context context);
    public String generateXLS(File file, Map<String, List> outputStatus, UpdateObjectForm updateObjectForm) throws CustomException;
    public void addTemplateSheet(HSSFWorkbook inputBook, int rowIndex);
     public HSSFCellStyle createStyle(HSSFWorkbook inputBook);
}
