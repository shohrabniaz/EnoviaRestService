/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.jsonOutput;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Tahmid
 */
public class BomExportResults {
    private String requester;
    private List<Object> results;

    public List<Object> getBomExportResults() {
        return results;
    }

    public void setBomExportResults(List<Object> results) {
        this.results = results;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

}
