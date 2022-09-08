/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.utilities;


import com.bjit.common.rest.app.service.enoviaCPQ.model.Item;
import com.bjit.common.rest.app.service.enoviaCPQ.model.ItemInfo;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.transfer.util.ApplicationProperties;
import com.bjit.ex.integration.transfer.util.FileDirProcess;
//import com.bjit.ex.integration.transfer.util.ItemInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author BJIT
 */
public class FileProcessorUtils {

    private static final Logger TRANSFER_ACTION_LOGGER = Logger.getLogger(FileProcessorUtils.class);

//    public HashMap<String, Item> processXMLFiles() throws IOException {
//        HashMap<String, Item> itemMap = new HashMap<>();
//        String triggerredFileDirectory = PropertyReader.getProperty("cpq.env.config.properties.dir");
//        TRANSFER_ACTION_LOGGER.info("CPQ file directory : " + triggerredFileDirectory);
//        try ( DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(triggerredFileDirectory))) {
//            for (Path filePath : stream) {
//                String filePathName = filePath.toString();
//                String pattern = Pattern.quote(System.getProperty("file.separator"));
//                String[] arrOfFilePathName = filePathName.split(pattern);
//                try {
//
//                    if (arrOfFilePathName.length == 4 && filePathName.contains(".xml")) {
//                        Item exportItem = getObject(new File(filePath.toString()), Item.class);
////                              String export = pdmBomExporter.export(context, exportItem.getId());
//                        itemMap.put(filePath.getFileName().toString(), exportItem);
////                                moveAndDeleteXML(filePath, arrOfFilePathName, "old");
//
//                    }
////                            responseList.add(export);
//                } catch (Exception ex) {
////                            moveAndDeleteXML(filePath, arrOfFilePathName, "error");
//                    TRANSFER_ACTION_LOGGER.error(ex);
//                }
//            };
//        }
//
//        return itemMap;
//    }
    public void moveToFolder(String sourceAbsoluteDirectory, String movingFolderName) {
        String fileName = sourceAbsoluteDirectory.substring(sourceAbsoluteDirectory.lastIndexOf("/") + 1);
        String movingFolder = "";
        if (movingFolderName == "old") {
            movingFolder = PropertyReader.getProperty("env.cpq.transfer.xml.successful.storage");
        } else if (movingFolderName == "error") {
            movingFolder = PropertyReader.getProperty("env.cpq.transfer.xml.unsuccessful.storage");
        }
        String relativeDestinationDirectory = sourceAbsoluteDirectory.replace(fileName, "") + movingFolder + "/" + fileName;
        moveFile(sourceAbsoluteDirectory, relativeDestinationDirectory);
    }

    public void moveFile(String absoluteSourceDirectory, String absoluteDestinationDirectory) {
        try {
            Long currentTime = System.currentTimeMillis();

            Path sourcePath = Paths.get(absoluteSourceDirectory);
            String fileName = absoluteDestinationDirectory.substring(absoluteDestinationDirectory.lastIndexOf("/"));
            String checkDirectory = absoluteDestinationDirectory.replace(fileName, "");
            Path moveTargetPath = Paths.get(checkDirectory);

            if (!Files.exists(moveTargetPath)) {

                Files.createDirectory(moveTargetPath);
                System.out.println(checkDirectory + " Directory created");
            }
            String fileExtension = absoluteDestinationDirectory.substring(absoluteDestinationDirectory.lastIndexOf("."));
            String targetPathWithFileNam = absoluteDestinationDirectory.replace(fileExtension, "_" + currentTime.toString()) + fileExtension;
            Path absoluteDestinationDirectoryPath = Paths.get(targetPathWithFileNam);
            Files.move(sourcePath, absoluteDestinationDirectoryPath);

        } catch (IOException ex) {
            TRANSFER_ACTION_LOGGER.error(ex);
            throw new RuntimeException();
        }

    }

    public void deleteFile(String fileAbsolutePath) {
        try {
            Path fileWithPath = Paths.get(fileAbsolutePath);
            Files.deleteIfExists(fileWithPath);
        } catch (IOException ex) {
            TRANSFER_ACTION_LOGGER.error(ex);
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
    
     public ItemInfo jaxbXmlFileToObject(File fileName) throws JAXBException {
       File xmlFile = fileName;
        JAXBContext jaxbContext;

        jaxbContext = JAXBContext.newInstance(ItemInfo.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        ItemInfo itemInfo = (ItemInfo) jaxbUnmarshaller.unmarshal(xmlFile);
        

        return itemInfo;
    }

    public HashMap<String, Item> processXMLFiles() throws Exception {
        String xmlBusinessObjectFileDirectory = PropertyReader.getProperty("cpq.env.config.properties.dir");;
        // String dir = ApplicationProperties.getProprtyValue(xmlBusinessObjectFileDirectory);
        TRANSFER_ACTION_LOGGER.info("Loading XML from location: " + xmlBusinessObjectFileDirectory);
        File directoryName = new File(xmlBusinessObjectFileDirectory);
        HashMap<String, Item> itemMap = new HashMap<>();
        try {
            FileFilter filter = FileDirProcess.getFileFilter(directoryName, "xml");
            File[] listOfFiles = directoryName.listFiles(filter);
            if (listOfFiles.length < 1) {
                TRANSFER_ACTION_LOGGER.info("No XML file found on the directory !!! ");

            } else {

                try {

                    try {

                        Arrays.sort(listOfFiles, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS");
                                Date creationDate1 = new Date();
                                Date creationDate2 = new Date();
                                try {
                                    creationDate1 = format.parse(f1.getName().substring(f1.getName().lastIndexOf('_') + 1).replaceAll(".xml", ""));
                                    creationDate2 = format.parse(f2.getName().substring(f2.getName().lastIndexOf('_') + 1).replaceAll(".xml", ""));
                                } catch (ParseException ex) {
                                    //  Logger.getLogger(FileProcessorUtils.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                return creationDate1.compareTo(creationDate2);
                            }
                        });

                        List<File> fileList = Arrays.asList(listOfFiles);

                        try {
                            for (int i = 0; i < fileList.size(); i++) {
                                File file = fileList.get(i);
                                ItemInfo itemInfo = new ItemInfo();
                                FileDirProcess fileDirProcess = new FileDirProcess();
                                itemInfo = jaxbXmlFileToObject(file);
                                Item item = new Item();
                                item.setName(itemInfo.getName());
                                item.setRevision(itemInfo.getRevision());
                                item.setId(itemInfo.getId());
                                item.setCurrentState(itemInfo.getCurrentState());
                                item.setMessage(itemInfo.getMessage());
                                item.setNextState(itemInfo.getNextState());
                                
                                itemMap.put(file.getName(), item);
                            }
                            //  processXmlBusinessFile(fileList);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            TRANSFER_ACTION_LOGGER.error("\n Transfer Error !!! " + ex);
                            TRANSFER_ACTION_LOGGER.trace(ex);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        TRANSFER_ACTION_LOGGER.error("Error Occurred : " + e);
                        TRANSFER_ACTION_LOGGER.trace(e);
                        throw e;
                    }
                } catch (Exception exp) {
                    exp.printStackTrace();
                    TRANSFER_ACTION_LOGGER.error("Error Occurred : " + exp);
                    TRANSFER_ACTION_LOGGER.error(exp);
                    throw exp;
                }
            }
            return itemMap;
        } catch (Exception exp) {
            TRANSFER_ACTION_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    public String getFileDirectory() {
        return PropertyReader.getProperty("cpq.env.config.properties.dir");
    }

}
