/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.refItem;

/**
 *
 * @author Suvonkar Kundu
 */
public interface RefItemExportService {

    public String getJsonArrayData(String code, String codeStatus);

    public String convertJsonToXml(String jsonString);
}
