package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.model.comosxml.RFLP;

import java.util.HashMap;

public interface IComosFileWriter extends IFormattedFileWriter{
    void writeFile(String directory, String filename, String jsonString, String extension);

    int writeFile(HashMap<String, RFLP> stringRFLPHashMap);

    int writeFile(HashMap<String, RFLP> stringRFLPHashMap, Boolean prepareSeparateDirectory);

    Boolean deleteFile(String absoluteFilePath);

    String getXMLFileDirectory (String data);
}
