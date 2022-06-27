package com.bjit.common.rest.app.service.model.drawing.response;

import java.util.List;

public class ItemData {
    private Item item;
    private List<Info> info;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Info> getInfo() {
        return info;
    }

    public void setInfo(List<Info> info) {
        this.info = info;
    }
}

