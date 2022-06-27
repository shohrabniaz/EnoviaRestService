/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.model.bom;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tomal
 */
public class BomImportBean {
    List <BomItem> BillOfMaterial_ODI;

    public List<BomItem> getBillOfMaterial_ODI() {
        return BillOfMaterial_ODI;
    }

    public void setBillOfMaterial_ODI(List<BomItem> BillOfMaterial_ODI) {
        this.BillOfMaterial_ODI = BillOfMaterial_ODI;
    }
}
