package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import java.io.File;

public interface IFileWriter {
    void writeFile(String directory, String filename, String jsonString);

    default Boolean createDirectory(String directory) {
        File fileDirectory = new File(directory);
        return !fileDirectory.exists() ? fileDirectory.mkdirs() : Boolean.FALSE;
    }
}
