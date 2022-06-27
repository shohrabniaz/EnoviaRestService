/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.mail.rnp;

/**
 *
 * @author BJIT
 */
public class RnPMailParameters {
    String reportDownloadLink;
    String mailSubject;
    String envName;
    String itemName;

    public String getReportDownloadLink() {
        return reportDownloadLink;
    }

    public void setReportDownloadLink(String reportDownloadLink) {
        this.reportDownloadLink = reportDownloadLink;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
}
