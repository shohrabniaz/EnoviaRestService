/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.createobject;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class UpdateObjectBean {

    private String businessObjectId;
    private TNR tnr;
    private HashMap<String, String> attributeListMap;
    private String source;

    public String getBusinessObjectId() {
        return businessObjectId;
    }

    public void setBusinessObjectId(String businessObjectId) {
        this.businessObjectId = businessObjectId;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public HashMap<String, String> getAttributeListMap() {
        return attributeListMap;
    }

    public void setAttributeListMap(HashMap<String, String> attributeListMap) {
        this.attributeListMap = attributeListMap;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
