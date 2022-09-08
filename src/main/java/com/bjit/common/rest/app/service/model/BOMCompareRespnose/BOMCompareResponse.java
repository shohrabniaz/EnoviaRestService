/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.BOMCompareRespnose;

import com.bjit.project_structure.utilities.NullOrEmptyChecker;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tahmid
 */
public class BOMCompareResponse {

    private List<BOMData> BOM;

    public List<BOMData> getBOM() {
        if (NullOrEmptyChecker.isNullOrEmpty(BOM)) {
            BOM = new ArrayList<>();
        }
        return BOM;
    }

    public void setBOM(List<BOMData> BOM) {
        this.BOM = BOM;
    }

}
