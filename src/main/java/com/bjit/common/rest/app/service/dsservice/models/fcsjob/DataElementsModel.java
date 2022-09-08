package com.bjit.common.rest.app.service.dsservice.models.fcsjob;

public class DataElementsModel {
    private String ticketURL;
    private String ticketparamname;
    private String ticket;
    private String title;
    private String policty;
    private String description;
    private String receipt;
    private String name;
    private String policy;
    private String state;
    private String stateNLS;
    private String typeNLS;
    private String revision;
    private String isLatestRevision;
    private String collabspace;
    private String originated;
    private String modified;
    private String comments;
    private String hasDownloadAccess;
    private String hasReviseAccess;
    private String hasModifyAccess;
    private String hasDeleteAccess;
    private String reservedby;
    private String secondaryTitle;
    private String typeicon;
    private String image;
    private String firstname;
    private String lastname;
    private String locker;
    private String fileType;
    private String dimension;
    private String length;

    public String getTicketURL() {
        return ticketURL;
    }

    public void setTicketURL(String ticketURL) {
        this.ticketURL = ticketURL;
    }

    public String getTicketparamname() {
        return ticketparamname;
    }

    public void setTicketparamname(String ticketparamname) {
        this.ticketparamname = ticketparamname;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPolicty() {
        return policty;
    }

    public void setPolicty(String policty) {
        this.policty = policty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateNLS() {
        return stateNLS;
    }

    public void setStateNLS(String stateNLS) {
        this.stateNLS = stateNLS;
    }

    public String getTypeNLS() {
        return typeNLS;
    }

    public void setTypeNLS(String typeNLS) {
        this.typeNLS = typeNLS;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getIsLatestRevision() {
        return isLatestRevision;
    }

    public void setIsLatestRevision(String isLatestRevision) {
        this.isLatestRevision = isLatestRevision;
    }

    public String getCollabspace() {
        return collabspace;
    }

    public void setCollabspace(String collabspace) {
        this.collabspace = collabspace;
    }

    public String getOriginated() {
        return originated;
    }

    public void setOriginated(String originated) {
        this.originated = originated;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getHasDownloadAccess() {
        return hasDownloadAccess;
    }

    public void setHasDownloadAccess(String hasDownloadAccess) {
        this.hasDownloadAccess = hasDownloadAccess;
    }

    public String getHasReviseAccess() {
        return hasReviseAccess;
    }

    public void setHasReviseAccess(String hasReviseAccess) {
        this.hasReviseAccess = hasReviseAccess;
    }

    public String getHasModifyAccess() {
        return hasModifyAccess;
    }

    public void setHasModifyAccess(String hasModifyAccess) {
        this.hasModifyAccess = hasModifyAccess;
    }

    public String getHasDeleteAccess() {
        return hasDeleteAccess;
    }

    public void setHasDeleteAccess(String hasDeleteAccess) {
        this.hasDeleteAccess = hasDeleteAccess;
    }

    public String getReservedby() {
        return reservedby;
    }

    public void setReservedby(String reservedby) {
        this.reservedby = reservedby;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public String getTypeicon() {
        return typeicon;
    }

    public void setTypeicon(String typeicon) {
        this.typeicon = typeicon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLocker() {
        return locker;
    }

    public void setLocker(String locker) {
        this.locker = locker;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    /**
     * Used by BeanUtils
     * @return the current
     */
    public String getCurrent() {
        return this.state;
    }

    /**
     * Used by BeanUtils
     * @param current the current to set
     */
    public void setCurrent(String current) {
        //this.current = current;
        this.state = current;
    }
}
