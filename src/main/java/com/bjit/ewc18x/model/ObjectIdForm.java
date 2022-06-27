/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Administrator
 */
public class ObjectIdForm extends UiStatusMessageForm {

    @NotEmpty
    private String cbpKey;
    @NotEmpty
    private String type;// = "3DShape";// = "ENOCLG_KeywordReference";
    @NotEmpty
    private String name;// = "3sh-Interfix to be defined 12569198-00000661";// = "testA003611C000015485742A3E900067630";
    private String revision;// = "A.1";
    private String vault;// = "vplm";
    private String objectId;
    private String demoException;

    /**
     * @return the cbpKey
     */
    public String getCbpKey() {
        return cbpKey;
    }

    /**
     * @param cbpKey the cbpKey to set
     */
    public void setCbpKey(String cbpKey) {
        this.cbpKey = cbpKey;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return the vault
     */
    public String getVault() {
        return vault;
    }

    /**
     * @param vault the vault to set
     */
    public void setVault(String vault) {
        this.vault = vault;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the demoException
     */
    public String getDemoException() {
        return demoException;
    }

    /**
     * @param demoException the demoException to set
     */
    public void setDemoException(String demoException) {
        this.demoException = demoException;
    }

    @Override
    public String toString() {
        return "ObjectIdForm{" + "cbpKey=" + cbpKey + ", type=" + type + ", name=" + name + ", revision=" + revision + ", vault=" + vault + ", objectId=" + objectId + ", demoException=" + demoException + '}';
    }

}
