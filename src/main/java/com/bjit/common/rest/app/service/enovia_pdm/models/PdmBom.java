package com.bjit.common.rest.app.service.enovia_pdm.models;

import java.util.List;

public class PdmBom {
    private Item parentItem;
    List<ChildItem> childItems;

    public PdmBom(){}

    public PdmBom(Item parentItem, List<ChildItem> childItems) {
        this.parentItem = parentItem;
        this.childItems = childItems;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public void setParentItem(Item parentItem) {
        this.parentItem = parentItem;
    }

    public List<ChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<ChildItem> childItems) {
        this.childItems = childItems;
    }

    @Override
    public String toString() {
        return "PdmBom{" +
                "parentItem=" + parentItem.toString() +
                ", childItems=" + childItems.toString() +
                '}';
    }
}
