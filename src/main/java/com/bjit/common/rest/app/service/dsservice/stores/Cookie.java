package com.bjit.common.rest.app.service.dsservice.stores;

import java.util.Date;

public class Cookie {
    private String name;
    private String value;
    private Date expireTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public boolean equals(Object cookie){
        Cookie comparableCookie = (Cookie) cookie;
        boolean nameCompared = this.name.equals(comparableCookie.getName());
        boolean valueCompared = this.value.equals(comparableCookie.getValue());

        return nameCompared && valueCompared;
    }
}
