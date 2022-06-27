/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.bom.model;

import java.util.LinkedHashMap;
import java.util.Map;
import matrix.db.BusinessObject;

/**
 *
 * @author Ashikur Rahman
 */
public class AttributeMapping {
    public static Map<String, String> getAttributeMap() {
        Map<String, String> attrMap = new LinkedHashMap<>();
        attrMap.put("MBOM_Drawing_Number", "Drawing Number");
        attrMap.put("MBOM_Drawing_Revision", "Drawing Revision");
        attrMap.put("MBOM_Standard", "Standard");
        attrMap.put("MBOM_short_Title_In_English", "Short Title");
        attrMap.put("Title_Abbreviation", "Title");
        attrMap.put("EstimatedWeight", "Weight");
        return attrMap;
    }
    
    /*public static Map<String, String> getRelationshipMap(BusinessObject busObj, String relName) {
        Map<String, String> attrMap = new LinkedHashMap<>();
        busObj.getAl
        attrMap.put("MBOM_Drawing_Number", "Drawing Number");
        attrMap.put("MBOM_Drawing_Revision", "Drawing Revision");
        attrMap.put("MBOM_Standard", "Standard");
        attrMap.put("MBOM_short_Title_In_English", "Short Title");
        attrMap.put("Title_Abbreviation", "Title");
        attrMap.put("EstimatedWeight", "Weight");
        return attrMap;
    }*/
}
