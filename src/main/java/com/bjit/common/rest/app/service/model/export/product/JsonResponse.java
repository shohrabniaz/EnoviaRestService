/*
 * Copyright 2018 BJIT Limited All rights reserved.
 */
package com.bjit.common.rest.app.service.model.export.product;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Suvonkar Kundu
 */
public class JsonResponse {

    @ApiModelProperty(
            name = "HTC",
            dataType = "String",
            example = "HTC")
    private String name;
    @ApiModelProperty(
            name = "suvonkar.kundu@gmail.com",
            dataType = "String",
            example = "suvonkar.kundu@gmail.com")
    private String email;
    @ApiModelProperty(
            name = "HTC ulta",
            dataType = "String",
            example = "HTC ulta")
    private String marketingName;
    @ApiModelProperty(
            name = "HTC|ulta",
            dataType = "String",
            example = "HTC|ulta")
    private String classificationPath;
    @ApiModelProperty(
            name = "Suvonkar Kundu",
            dataType = "String",
            example = "Suvonkar Kundu")
    private String owner;

    public JsonResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMarketingName() {
        return marketingName;
    }

    public void setMarketingName(String marketingName) {
        this.marketingName = marketingName;
    }

    public String getClassificationPath() {
        return classificationPath;
    }

    public void setClassificationPath(String classificationPath) {
        this.classificationPath = classificationPath;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public JsonResponse(String name, String email, String marketingName, String classificationPath, String owner) {
        this.name = name;
        this.email = email;
        this.marketingName = marketingName;
        this.classificationPath = classificationPath;
        this.owner = owner;
    }

}
