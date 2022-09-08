package com.bjit.common.rest.app.service.dsservice.models.itemduplication;

public class CreatedItem {

    private String identifier;
    private String relativePath;
    private String derivedFrom;
    private String id;
    private String source;
    private String type;

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the relativePath
     */
    public String getRelativePath() {
        return relativePath;
    }

    /**
     * @param relativePath the relativePath to set
     */
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * @return the derivedFrom
     */
    public String getDerivedFrom() {
        return derivedFrom;
    }

    /**
     * @param derivedFrom the derivedFrom to set
     */
    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
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

}
