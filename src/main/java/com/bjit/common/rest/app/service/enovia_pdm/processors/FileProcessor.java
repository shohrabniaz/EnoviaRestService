/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enovia_pdm.processors;

import com.bjit.common.rest.app.service.enovia_pdm.models.xml.Item;
import com.bjit.common.rest.app.service.enovia_pdm.processors.interfaces.IFileProcessor;
import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author BJIT
 */
@Component
public class FileProcessor implements IFileProcessor {

    private static final Logger FILE_PROCESSOR_LOGGER = Logger.getLogger(FileProcessor.class);

    @Override
    public HashMap<String, Item> processXMLFiles() throws IOException {
        HashMap<String, Item> itemMap = new HashMap<>();
        String triggerredFileDirectory = PropertyReader.getProperty("man.item.triggerred.xml.file.location");
        FILE_PROCESSOR_LOGGER.info("Mastership file directory : " + triggerredFileDirectory);
        try (Stream<Path> paths = Files.walk(Paths.get(triggerredFileDirectory))) {
            paths
                    .filter(Files::isRegularFile)
//                    .parallel()
                    .forEach((Path filePath) -> {
                        String filePathName = filePath.toString();
                        String pattern = Pattern.quote(System.getProperty("file.separator"));
                        String[] arrOfFilePathName = filePathName.split(pattern);
                        int nthFolderLocation = 4;
                        try {

                            if (arrOfFilePathName.length == nthFolderLocation && filePathName.contains(".xml")) {
                                Item exportItem = getObject(new File(filePath.toString()), Item.class);
//                              String export = pdmBomExporter.export(context, exportItem.getId());
                                itemMap.put(filePath.getFileName().toString(), exportItem);
//                                moveAndDeleteXML(filePath, arrOfFilePathName, "old");

                            }
//                            responseList.add(export);
                        } catch (Exception ex) {
//                            moveAndDeleteXML(filePath, arrOfFilePathName, "error");
                            FILE_PROCESSOR_LOGGER.error(ex);
                        }
                    });
        }

        return itemMap;
    }

    @Override
    public void moveToFolder(String sourceAbsoluteDirectory, String movingFolderName){
        String fileName = sourceAbsoluteDirectory.substring(sourceAbsoluteDirectory.lastIndexOf("/")+1);
        String movingFolder = "";
        if( movingFolderName == "old" )
            movingFolder = PropertyReader.getProperty("env.pdm.mastership.xml.successful.storage");
        else if( movingFolderName == "error" )
            movingFolder = PropertyReader.getProperty("env.pdm.mastership.xml.unsuccessful.storage");
        String relativeDestinationDirectory = sourceAbsoluteDirectory.replace(fileName,"") + movingFolder + "/" + fileName;
        moveFile(sourceAbsoluteDirectory,relativeDestinationDirectory);
    }


    @Override
    public void moveFile(String absoluteSourceDirectory, String absoluteDestinationDirectory) {
        try {
            Long currentTime= System.currentTimeMillis();

            Path sourcePath = Paths.get(absoluteSourceDirectory);
            String fileName = absoluteDestinationDirectory.substring(absoluteDestinationDirectory.lastIndexOf("/"));
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

    @Override
    public void deleteFile(String fileAbsolutePath) {
        try {
            Path fileWithPath = Paths.get(fileAbsolutePath);
            Files.deleteIfExists(fileWithPath);
        } catch (IOException ex) {
            FILE_PROCESSOR_LOGGER.error(ex);
            throw new RuntimeException();
        }

    }

    protected <T> T getObject(File file, Class<T> classReference) throws JAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classReference);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T object = (T) jaxbUnmarshaller.unmarshal(file);
            return object;
        } catch (JAXBException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    @Override
    public String getFileDirectory() {
        return PropertyReader.getProperty("man.item.triggerred.xml.file.location");
    }

}
