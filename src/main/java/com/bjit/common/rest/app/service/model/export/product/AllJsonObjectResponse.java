/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.export.product;

import com.google.gson.JsonArray;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;

/**
 *
 * @author Suvonkar Kundu
 */
public class AllJsonObjectResponse {
    @ApiModelProperty(dataType="List", value = "Results")
    private List<JsonResponse> Results;

    public List<JsonResponse> getResults() {
        return Results;
    }

    public void setResults(List<JsonResponse> Results) {
        this.Results = Results;
    }

    public AllJsonObjectResponse() {
    }
}
