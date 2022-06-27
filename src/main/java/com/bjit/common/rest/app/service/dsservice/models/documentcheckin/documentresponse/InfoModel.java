package com.bjit.common.rest.app.service.dsservice.models.documentcheckin.documentresponse;

import com.bjit.common.rest.app.service.dsservice.models.fcsjob.DataElementsModel;
import java.util.List;

public class InfoModel {
    private String id;
    private String type;
    private String cestamp;
    private DataElementsModel dataelements;
    private List<Object> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCestamp() {
        return cestamp;
    }

    public void setCestamp(String cestamp) {
        this.cestamp = cestamp;
    }

    public DataElementsModel getDataelements() {
        return dataelements;
    }

    public void setDataelements(DataElementsModel dataelements) {
        this.dataelements = dataelements;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }
}
