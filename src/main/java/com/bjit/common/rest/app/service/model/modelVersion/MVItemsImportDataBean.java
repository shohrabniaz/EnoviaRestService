/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.modelVersion;

import java.util.List;

/**
 *
 * @author BJIT
 */
public class MVItemsImportDataBean {
    private String source;
    private String securityContext;
    private String owner;
    private List<MVDataTree> dataTree;
    
    public MVItemsImportDataBean(){
    
    }
    
    public MVItemsImportDataBean(String owner, String source,
                                 String securityContext,
                                 List<MVDataTree> dataTree)
    {
        this.owner = owner;
        this.source = source;
        this.securityContext = securityContext;
        this.dataTree = dataTree;
    }

    public String getSecurityContext() {
        return securityContext;
    }

    public void setSecurityContext(String securityContext) {
        this.securityContext = securityContext;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<MVDataTree> geDataTree() {
        return dataTree;
    }

    public void setDataTree(List<MVDataTree> dataTree) {
        this.dataTree = dataTree;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }
}

