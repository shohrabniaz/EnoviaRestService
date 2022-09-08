/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.createBOM;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Sajjad
 */
public class CreateBOMBean {
    private TNR item;
    private List<HashMap<String, String>> lines;

    public TNR getItem() {
        return item;
    }

    public void setItem(TNR item) {
        this.item = item;
    }

    public List<HashMap<String, String>> getLines() {
        return lines;
    }

    public void setLines(List<HashMap<String, String>> lines) {
        this.lines = lines;
    }
}
