package com.bjit.common.rest.app.service.dsservice.models.sercuritycontext;

public class PreferredCredentialsModel {
    private CSModel collabspace;
    private CSModel organization;
    private CSModel role;

    public CSModel getCollabspace() {
        return collabspace;
    }

    public void setCollabspace(CSModel collabspace) {
        this.collabspace = collabspace;
    }

    public CSModel getOrganization() {
        return organization;
    }

    public void setOrganization(CSModel organization) {
        this.organization = organization;
    }

    public CSModel getRole() {
        return role;
    }

    public void setRole(CSModel role) {
        this.role = role;
    }
}
