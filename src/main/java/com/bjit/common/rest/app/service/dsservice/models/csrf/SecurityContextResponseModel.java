package com.bjit.common.rest.app.service.dsservice.models.csrf;

import com.bjit.common.rest.app.service.dsservice.models.sercuritycontext.PreferredCredentialsModel;


public class SecurityContextResponseModel {
    private String pid;
    private String name;
    private PreferredCredentialsModel preferredcredentials;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PreferredCredentialsModel getPreferredcredentials() {
        return preferredcredentials;
    }

    public void setPreferredcredentials(PreferredCredentialsModel preferredcredentials) {
        this.preferredcredentials = preferredcredentials;
    }
}
