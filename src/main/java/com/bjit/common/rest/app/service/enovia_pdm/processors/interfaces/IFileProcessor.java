/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * @author BJIT
 */
public interface IFileProcessor {
    HashMap<String, Item> processXMLFiles() throws IOException;

    void moveToFolder(String sourceAbsoluteDirectory, String folderName);

    void moveFile(String absoluteSourceDirectory, String absoluteDestinationDirectory);

    void deleteFile(String fileAbsolutePath);

    String getFileDirectory();
}
