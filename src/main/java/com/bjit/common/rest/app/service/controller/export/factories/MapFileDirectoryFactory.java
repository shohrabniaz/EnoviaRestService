/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.export.factories;

import com.bjit.ewc18x.utils.PropertyReader;

/**
 *
 * @author BJIT
 */
public class MapFileDirectoryFactory {
    public static synchronized String getMapFileDirectory(String exportType){
        return PropertyReader.getProperty("export.mapper.xml.file.location") + exportType + ".xml";
    }
}
