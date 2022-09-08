package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ComosBatchToolFileProcessor {
    @Autowired
    Environment env;
    private static final Logger FILE_PROCESSOR_LOGGER = Logger.getLogger(ComosBatchToolFileProcessor.class);
    public void moveToFolder(String sourceAbsoluteDirectory, String movingFolderName){
        String fileName = sourceAbsoluteDirectory.substring(sourceAbsoluteDirectory.lastIndexOf("\\")+1);
        String movingFolder = "";
        if( movingFolderName == "old" )
            movingFolder = "old";
        else if( movingFolderName == "error" )
            movingFolder = "error";
        //String relativeDestinationDirectory = sourceAbsoluteDirectory.replace(fileName,"") + movingFolder + "\\" + fileName;
        String relativeDestinationDirectory = env.getProperty("comos.processed.file.directory")+ fileName;
        moveFile(sourceAbsoluteDirectory,relativeDestinationDirectory);
    }

    public void moveFile(String absoluteSourceDirectory, String absoluteDestinationDirectory) {
        try {
            Long currentTime= System.currentTimeMillis();

            Path sourcePath = Paths.get(absoluteSourceDirectory);
            String fileName = absoluteDestinationDirectory.substring(absoluteDestinationDirectory.lastIndexOf("\\"));
            String checkDirectory = absoluteDestinationDirectory.replace(fileName,"");
            Path moveTargetPath = Paths.get(checkDirectory);

            if (!Files.exists(moveTargetPath)) {

                Files.createDirectory(moveTargetPath);
                System.out.println(checkDirectory + " Directory created");
            }

            String fileExtension = absoluteDestinationDirectory.substring(absoluteDestinationDirectory.lastIndexOf("."));
            String targetPathWithFileNam = absoluteDestinationDirectory.replace(fileExtension,"_"+currentTime.toString()) + fileExtension;
            Path absoluteDestinationDirectoryPath = Paths.get(targetPathWithFileNam);
            Files.move(sourcePath, absoluteDestinationDirectoryPath);
        } catch (IOException ex) {
            FILE_PROCESSOR_LOGGER.error(ex);
            throw new RuntimeException();
        }

    }

    public void deleteFile(String fileAbsolutePath) {
        try {
            Path fileWithPath = Paths.get(fileAbsolutePath);
            Files.deleteIfExists(fileWithPath);
        } catch (IOException ex) {
            FILE_PROCESSOR_LOGGER.error(ex);
            throw new RuntimeException();
        }

    }

    public String getFileDirectory() {
        return PropertyReader.getProperty("man.item.triggerred.xml.file.location");
    }

}
