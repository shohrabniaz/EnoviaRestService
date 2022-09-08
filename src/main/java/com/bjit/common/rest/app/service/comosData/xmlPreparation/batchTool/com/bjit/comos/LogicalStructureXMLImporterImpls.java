package com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos;

import com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.comos.consumer.ServiceConsumer;
import com.bjit.common.rest.app.service.comosData.xmlPreparation.batchTool.com.bjit.services.IBatchToolRunner;

//import com.bjit.services.IBatchToolRunner;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Log4j
@Service
@Qualifier("LogicalStructureXMLImporterImpls")
public class LogicalStructureXMLImporterImpls implements IBatchToolRunner {
    public String unique_mil_equipment_id = "";
    @Autowired
    Environment env;

    @Autowired
    ComosBatchToolFileProcessor comosBatchToolFileProcessor;


    @Autowired
    ServiceConsumer serviceConsumer;

    private static void greetings() {
        log.info("######################################");
        log.info("#Starting CATFLEditorImportBatch Tool#");
        log.info("######################################");
    }

//    List<String> directoryName = new ArrayList<>();

    public void updateXmlPath(Document doc, List<String> filenamesList, String file) {
        NodeList configFileData = doc.getElementsByTagName("RFLP");
        filenamesList.stream().forEach(filename -> {
                    Element emp = (Element) configFileData.item(0);
                    Node xmlPath = emp.getElementsByTagName("XMLPath").item(0).getFirstChild();
                    String filesAbsoluteDirectory = env.getProperty("generated.logical.structure.xml.file.directory") + file + "\\" + filename;
//                    System.out.println("Generated XML file Directory '" + filesAbsoluteDirectory + "'");
                    log.info("Processing '" + filesAbsoluteDirectory + "'");

                    xmlPath.setNodeValue(filesAbsoluteDirectory);
                    try {
                        updatedFileSave(doc);
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }

                    String configFile = env.getProperty("logical.structure.xml.import.batch.tool.config");
                    log.info("Config File Directory : " + configFile);
                    File configurationFile = new File(configFile);
                    readXMLFile(configurationFile);
                    runBatchTool();
                    moveFiletoOld(filesAbsoluteDirectory);
                    File current_folder = new File(env.getProperty("generated.logical.structure.xml.file.directory") + file);
                    current_folder.delete();


                }
        );

        try {
//          serviceConsumer.callbyfilename();
            serviceConsumer.callByFilename(file);

        } catch (IOException e) {
            log.error(e);
        }

    }


    private void updateElementValue(Document doc) {
//        HashMap<String, List<String>> unique_mil_equipment_id_wise_list = uniqueFileSeparation();
//
//        unique_mil_equipment_id_wise_list.forEach((unique_mil_equipment_id, filenamesList) -> {
//                    System.out.println(filenamesList);
//                    updateXmlPath(doc, filenamesList);
//                }
//        );
        String xmlFileDirectory = env.getProperty("generated.logical.structure.xml.file.directory");
        log.info("Generated XML log file directory : '" + xmlFileDirectory + "'");
        File directoryPath = new File(xmlFileDirectory);
        //List of all file directories
        File[] mil_equipment_id_wise_filesList = directoryPath.listFiles();
        for (File file : mil_equipment_id_wise_filesList) {
            List<String> filenamesList = getConfigFileNames(file.toString());
            log.info(filenamesList);
//            directoryName.add(file.getName());
            updateXmlPath(doc, filenamesList, file.getName());
        }

//        List<String> filenamesList = getConfigFileNames(env.getProperty("generated.logical.structure.xml.file.directory"));
//        log.info(filenamesList);
//        NodeList configFileData = doc.getElementsByTagName("RFLP");
//
//        //loop for file xml path write
//        filenamesList.stream().forEach(filename -> {
//                    Element emp = (Element) configFileData.item(0);
//                    Node xmlPath = emp.getElementsByTagName("XMLPath").item(0).getFirstChild();
//                    String filesAbsoluteDirectory = env.getProperty("generated.logical.structure.xml.file.directory") + "\\" + filename;
//
//                    log.info("Processing '" + filesAbsoluteDirectory + "'");
//
//                    xmlPath.setNodeValue(filesAbsoluteDirectory);
//                    try {
//                        updatedFileSave(doc);
//                    } catch (TransformerException e) {
//                        e.printStackTrace();
//                    }
//
//                    File configurationFile = new File(env.getProperty("logical.structure.xml.import.batch.tool.config"));
//                    readXMLFile(configurationFile);
//                    runBatchTool();
//
//                }
//        );
    }


    private void runBatchTool() {
        try {
            greetings();
            String importCommand = "cmd /c start CATSTART.exe -run \"CATFLEditorImportBatch " + env.getProperty("logical.structure.xml.import.batch.tool.config") + "\"";
            log.info(importCommand);
            Process process = Runtime.getRuntime().exec(
                    importCommand,
                    null,
                    new File(env.getProperty("xml.file.import.batch.tool.directory")));


            InputStream inputStream = process.getInputStream();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(inputStream));
            log.info("Here is the standard output of the command:\n");
            String s;
            while ((s = stdInput.readLine()) != null) {
                log.info(s);
            }

        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }
    }

    public List<String> getConfigFileNames(String config_file_dir) {
        File[] files = new File(config_file_dir).listFiles();

        return Optional
                .ofNullable(files)
                .map(Arrays::asList)
                .orElse(new ArrayList<>())
                .stream()
                .filter(File::isFile)
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    public void updatedFileSave(Document doc) throws TransformerException {
        //write the updated document to file or console
        doc.getDocumentElement().normalize();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(new File(env.getProperty("logical.structure.xml.import.batch.tool.config")));
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(source, result);
        log.info("XML file updated successfully");
    }

    public void readXMLFile(File configurationFile) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configurationFile);

            doc.getDocumentElement().normalize();

            log.info("Root element : " + doc.getDocumentElement().getNodeName());
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            log.info("-----------Modified File-----------");
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTool() {
        return null;
    }

    @Override
    public void run() throws RuntimeException {
        String configFile = env.getProperty("logical.structure.xml.import.batch.tool.config");
        log.info("Batch Tool Configuration: " + configFile);
        File xmlFile = new File(configFile);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            //update Element value
            updateElementValue(doc);

        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
    }


    public HashMap<String, List<String>> uniqueFileSeparation() {
        List<String> filenamesList = getConfigFileNames(env.getProperty("generated.logical.structure.xml.file.directory"));
        HashMap<String, List<String>> unique_mil_equipment_id_wise_list = new HashMap<>();
        Set<String> CheckingUniqueMilEquipmentId = new LinkedHashSet<String>();
        filenamesList.stream().forEach(filename -> {
            System.out.println(filename);
            Pattern pattern = Pattern.compile("mill-id_([0-9]+)_eq-id_([0-9.]+).*.xml");
            Matcher matcher = pattern.matcher(filename);
            if ((matcher.matches())) {
                System.out.printf("Name entered: %s\n", matcher.group(1));
                System.out.printf("Name entered: %s\n", matcher.group(2));
                unique_mil_equipment_id = "mill-id_" + matcher.group(1) + "_eq-id_" + matcher.group(2);

            }
            if (CheckingUniqueMilEquipmentId.contains(unique_mil_equipment_id)) {
                unique_mil_equipment_id_wise_list.get(unique_mil_equipment_id).add(filename);

            } else {
                CheckingUniqueMilEquipmentId.add(unique_mil_equipment_id);
                unique_mil_equipment_id_wise_list.put(unique_mil_equipment_id, new ArrayList<String>());
                unique_mil_equipment_id_wise_list.get(unique_mil_equipment_id).add(filename);
            }
        });

        return unique_mil_equipment_id_wise_list;
    }

    public void takingFileList() {
        File directoryPath = new File(env.getProperty("generated.logical.structure.xml.file.directory"));
        //List of all file directories
        File[] mil_equipment_id_wise_filesList = directoryPath.listFiles();
        for (File file : mil_equipment_id_wise_filesList) {
            List<String> filenamesList = getConfigFileNames(file.toString());
            log.info(filenamesList);
        }

    }


    public void moveFiletoOld(String filename) {
//        comosBatchToolFileProcessor.moveToFolder("D:/COMOS new/GeneratedXML/mill-id_119160_eq-id_119160.135_C_3_0001.xml", "old");
        comosBatchToolFileProcessor.moveToFolder(filename, "old");
//        String  oldFileDirectory=env.getProperty("generated.logical.structure.xml.file.directory");
//        Files.move(filename, oldFileDirectory);


    }

    public void moveFiletoError() {
        comosBatchToolFileProcessor.moveToFolder("D:/COMOS new/GeneratedXML/mill-id_119160_eq-id_119160.135_C_3_0001.xml", "old");

    }
}
