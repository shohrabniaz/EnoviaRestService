/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.service;

import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.utils.CustomException;
import java.util.List;
import javax.servlet.http.HttpSession;
import matrix.db.Context;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Kayum-603
 */
public interface ExpandObjectService {
   public String getJsonOutput(HttpSession httpSession, Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException;
   public String getXmlOutput(HttpSession httpSession,Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException;
   public String getXlsOutput(HttpSession httpSession,Context context, String physicalId, List<String> objectParamList, List<String> objectAttrList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm, List<String> unchangableAttr) throws CustomException;
   public String getSelectedTypePatternListExpression(ExpandObjectForm expandObjectForm);
   public ExpandObjectForm populateServiceInfo(ExpandObjectForm expandObjectForm, String fileName) throws CustomException ;
   public ResponseEntity<String> getRootObjectProperties(String physicalId, String host, HttpHeaders headers, List<String> objectParamList, List<String> objectRelAttrList, ExpandObjectForm expandObjectForm) throws CustomException;
}
