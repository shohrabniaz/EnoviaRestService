package com.bjit.common.rest.app.service.utilities;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FileUtilities {
    public static String getFileNameFromPath(String str) {
        str = FilenameUtils.getName(str);
        str = str.substring(0, str.lastIndexOf('.'));
        return str;
    }

    public static String getResourceFileData(String fileLocation) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileLocation);
        InputStream inputStream = classPathResource.getInputStream();
        byte[] byteArray = FileCopyUtils.copyToByteArray(inputStream);
        String data = new String(byteArray, StandardCharsets.UTF_8);
        return data;
    }

    public static File getFile(String fileLocation){
        File file = new File(fileLocation);
        return file;
    }
}