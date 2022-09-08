package com.bjit.common.rest.app.service.comosData.xmlPreparation.utilServices;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public interface IFileReader {
    List<String> getFileListFromDirectoryWithSpecificExtension(String directory, String extension) throws IOException;

    String readFile(String absolutePath) throws IOException;
    String readFile(File file) throws IOException;

    String readFile(String absolutePath, Charset encoding) throws IOException;
}
