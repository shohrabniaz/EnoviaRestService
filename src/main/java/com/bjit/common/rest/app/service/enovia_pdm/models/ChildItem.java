package com.bjit.common.rest.app.service.enovia_pdm.models;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;

public class ChildItem extends Item {
    private RelationData rel;

    public ChildItem(){}

    public ChildItem(TNR childTNR){
        super(childTNR);
    }

    public ChildItem(TNR childTNR, String owner){
        super(childTNR, owner);
    }

    public ChildItem(TNR childTNR, String owner, HashMap<String, String> attributes){
        super(childTNR, owner, attributes);
    }

    public ChildItem(TNR childTNR, String owner, HashMap<String, String> attributes, RelationData rel){
        this(childTNR.getType(), childTNR.getName(), childTNR.getRevision(), owner, attributes, rel);
    }

    public ChildItem(RelationData rel) {
        this.rel = rel;
    }

    public ChildItem(String type, String name, String revision, String owner, HashMap<String, String> attributes, RelationData rel) {
        super(type, name, revision, owner, attributes);
        this.rel = rel;
    }

    public RelationData getRel() {
        return rel;
    }

    public void setRel(RelationData rel) {
        this.rel = rel;
    }

    @Override
    public String toString() {
        return "ChildItem{" +
                "child=" + super.toString() +
                "rel=" + rel.toString() +
                '}';
    }
}
