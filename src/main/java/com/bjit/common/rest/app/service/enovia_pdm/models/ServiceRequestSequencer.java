package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;

import java.util.HashMap;
import java.util.List;

public class ServiceRequestSequencer {
    public ServiceRequestSequencer(){}
    public ServiceRequestSequencer(String filename, HashMap<String, List<ParentChildModel>> structure, Item item){
        this.filename = filename;
        this.structure = structure;
        this.exportItem = item;
    }
    String filename;
    HashMap<String, List<ParentChildModel>> structure;
    Item exportItem;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public HashMap<String, List<ParentChildModel>> getStructure() {
        return structure;
    }

    public void setStructure(HashMap<String, List<ParentChildModel>> structure) {
        this.structure = structure;
    }

    public Item getExportItem() {
        return exportItem;
    }

    public void setExportItem(Item exportItem) {
        this.exportItem = exportItem;
    }

}
