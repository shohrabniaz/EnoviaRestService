/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.createobject;

import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class CreateObjectBean implements Serializable, Cloneable, Comparable<CreateObjectBean> {

    private Boolean isAutoName;
    private TNR tnr;
    private HashMap<String, String> attributes;
    private String templateBusinessObjectId;
    private String folderId;
    private Boolean attributeGlobalRead;
    private String cs; //Collaboration Space
    private String source;
    private String nextVersion;
    private Boolean allowNewRevision;

    public CreateObjectBean() {
    }

    public CreateObjectBean(TNR tnr, HashMap<String, String> attributes) {
        this.tnr = tnr;
        this.attributes = attributes;

        this.isAutoName = false;
        this.nextVersion = "";
        this.allowNewRevision = false;
    }

    public CreateObjectBean(TNR tnr, HashMap<String, String> attributes, String source) {
        this(tnr, attributes);
        this.source = source;
    }

    public CreateObjectBean(Boolean isAutoName, TNR tnr, HashMap<String, String> attributes, String templateBusinessObjectId, String folderId, Boolean attributeGlobalRead, String cs, String source, String nextVersion, Boolean allowNewRevision) {
        this.isAutoName = isAutoName;
        this.tnr = tnr;
        this.attributes = attributes;
        this.templateBusinessObjectId = templateBusinessObjectId;
        this.folderId = folderId;
        this.attributeGlobalRead = attributeGlobalRead;
        this.cs = cs;
        this.source = source;
        this.nextVersion = nextVersion;
        this.allowNewRevision = allowNewRevision;
    }

    public Boolean getIsAutoName() {
        return isAutoName;
    }

    public void setIsAutoName(Boolean isAutoName) {
        this.isAutoName = isAutoName;
    }

    public TNR getTnr() {
        return tnr;
    }

    public void setTnr(TNR tnr) {
        this.tnr = tnr;
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getTemplateBusinessObjectId() {
        return templateBusinessObjectId;
    }

    public void setTemplateBusinessObjectId(String templateBusinessObjectId) {
        this.templateBusinessObjectId = templateBusinessObjectId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Boolean getAttributeGlobalRead() {
        return attributeGlobalRead;
    }

    public void setAttributeGlobalRead(Boolean attributeGlobalRead) {
        this.attributeGlobalRead = attributeGlobalRead;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNextVersion() {
        return nextVersion;
    }

    public void setNextVersion(String nextVersion) {
        this.nextVersion = nextVersion;
    }

    public Boolean getAllowNewRevision() {
        return allowNewRevision;
    }

    public void setAllowNewRevision(Boolean allowNewRevision) {
        this.allowNewRevision = allowNewRevision;
    }

    @Override
    public int compareTo(CreateObjectBean o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        JSON json = new JSON();
        return json.deserialize(json.serialize(this), CreateObjectBean.class);
        //return super.clone();
    }
}
