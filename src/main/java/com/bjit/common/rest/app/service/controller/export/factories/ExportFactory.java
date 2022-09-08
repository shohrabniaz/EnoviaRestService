/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.factories;

import com.bjit.common.rest.app.service.controller.export.contracts.IExport;
/**
 *
 * @author BJIT
 */
public class ExportFactory {
    public static synchronized IExport getExportProcessor(String exportType) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
        try {
            String className = "com.bjit.common.rest.app.service.controller.export.export_processors." + exportType + "Export";
            Class<?> forName = Class.forName(className);
            IExport iExport = (IExport) forName.newInstance();
            return iExport;
        } catch (ClassNotFoundException exp) {
            throw exp;
        }
    }
}
