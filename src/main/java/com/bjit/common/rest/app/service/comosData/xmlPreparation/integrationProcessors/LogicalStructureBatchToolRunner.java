package com.bjit.common.rest.app.service.comosData.xmlPreparation.integrationProcessors;

import com.bjit.ewc18x.utils.PropertyReader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Qualifier("LogicalStructureBatchToolRunner")
public class LogicalStructureBatchToolRunner implements IBatchToolRunner {
    private static final Logger LOGICAL_STRUCTURE_BATCH_TOOL_RUNNER_LOGGER = Logger.getLogger(LogicalStructureBatchToolRunner.class);

    private void updateElementValue(Document doc) {
        List<String> filenamesList = getConfigFileNames(PropertyReader.getProperty("generated.logical.structure.xml.file.directory"));
        System.out.println(filenamesList);
        NodeList configFileData = doc.getElementsByTagName("RFLP");

        //loop for file xml path write
        filenamesList.stream().forEach(elem -> {
                    Element emp = (Element) configFileData.item(0);
                    Node xmlPath = emp.getElementsByTagName("XMLPath").item(0).getFirstChild();
                    xmlPath.setNodeValue(PropertyReader.getProperty("generated.logical.structure.xml.file.directory") + "\\" + elem);
                    try {
                        updatedFileSave(doc);
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    }

                    File configurationFile = new File(PropertyReader.getProperty("logical.structure.xml.import.batch.tool.config"));
                    readXMLFile(configurationFile);
                    runBatchTool();
                }
        );
    }

    private void runBatchTool() {
        try {
            String importCommand = "cmd /c start CATSTART.exe -run \"CATFLEditorImportBatch " + PropertyReader.getProperty("logical.structure.xml.import.batch.tool.config") + "\"";
            System.out.println(importCommand);
            Process process = Runtime.getRuntime().exec(
                    importCommand,
                    null,
                    new File(PropertyReader.getProperty("xml.file.import.batch.tool.directory")));


            InputStream inputStream = process.getInputStream();

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(inputStream));
            System.out.println("Here is the standard output of the command:\n");
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            LOGICAL_STRUCTURE_BATCH_TOOL_RUNNER_LOGGER.error(e);
        } catch (Exception e) {
            LOGICAL_STRUCTURE_BATCH_TOOL_RUNNER_LOGGER.error(e);
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

        StreamResult result = new StreamResult(new File(PropertyReader.getProperty("logical.structure.xml.import.batch.tool.config")));
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(source, result);
        System.out.println("XML file updated successfully");
    }


    public void readXMLFile(File configurationFile) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configurationFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element : " + doc.getDocumentElement().getNodeName());
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            System.out.println("-----------Modified File-----------");
            StreamResult consoleResult = new StreamResult(System.out);
            transformer.transform(source, consoleResult);
        } catch (Exception e) {
            LOGICAL_STRUCTURE_BATCH_TOOL_RUNNER_LOGGER.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() throws RuntimeException {
        File xmlFile = new File(PropertyReader.getProperty("logical.structure.xml.import.batch.tool.config"));
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
}
