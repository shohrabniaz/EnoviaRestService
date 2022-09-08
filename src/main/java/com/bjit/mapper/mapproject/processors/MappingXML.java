/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.processors;

import com.bjit.mapper.mapproject.util.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.IOUtils;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.st;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 *
 * @author BJIT / Md.Omour Faruq
 * @param <T>
 */
public class MappingXML<T> implements IMapper<T> {

    private Class<T> classReference;
    private String fileLocation;
    private File file;
    private T mappingObject;
    private String xmlMapFileAsString;
    final static org.apache.log4j.Logger MAPPING_XML_LOGGER = org.apache.log4j.Logger.getLogger(MappingXML.class.getName());

    /**
     * Initializes the mapper processor from a given class which is a XML map
     * POJO or model class
     *
     * @param classReference
     * @throws Exception
     */
    @Override
    public void __init__(Class<T> classReference) throws Exception {
        this.classReference = classReference;
        this.fileLocation = Configuration.MAPPING_XML_FILE_PATH;
        try {
            loadXmlMapperStringFromResources();
        } catch (IOException ioExp) {
            MAPPING_XML_LOGGER.error("Xml Mapper couldn''t be read as string from resource location. " + this.fileLocation);
            //ioExp.printStackTrace(System.out);
            try {
                readXmlMapperFromFile();
            } catch (Exception exp) {
                MAPPING_XML_LOGGER.error("Xml Mapper couldn''t be initialized from " + this.fileLocation);
                MAPPING_XML_LOGGER.error(exp.getMessage());
                exp.printStackTrace(System.out);
                throw exp;
            }
        } catch (Exception exp) {
            MAPPING_XML_LOGGER.error("Xml Mapper couldn''t be read as string from resource location. " + this.fileLocation);
            exp.printStackTrace(System.out);
            try {
                readXmlMapperFromFile();
            } catch (Exception except) {
                MAPPING_XML_LOGGER.error("Xml Mapper couldn''t be initialized from " + this.fileLocation);
                MAPPING_XML_LOGGER.error(except.getMessage());
                except.printStackTrace(System.out);
                throw except;
            }
        }
    }

    @Override
    public void __init__(Class<T> classReference, String absoluteMapperFileDirectory) throws Exception {
        Configuration.MAPPING_XML_FILE_PATH = absoluteMapperFileDirectory;
        Configuration.MAPPING_XML_FILE_DIRECTORY = absoluteMapperFileDirectory;
        __init__(classReference);
    }

    /**
     * Will be deleted Soon
     */
    /*@Deprecated
    public void __init__(Class<T> classReference) throws Exception {
        this.classReference = classReference;
        this.fileLocation = Configuration.MAPPING_XML_FILE_PATH;
        try {
            readXmlMapperFromFile();
        } catch (Exception exp) {
            MAPPING_XML_LOGGER.error("Xml Mapper couldn''t be initialized from " + this.fileLocation);
            MAPPING_XML_LOGGER.error(exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
    }*/

    public void setMappingObject(T mappingObject) {
        this.mappingObject = mappingObject;
    }

    private void getXMLModelInstance() throws InstantiationException, IllegalAccessException {
        try {
            mappingObject = this.classReference.newInstance();
        } catch (InstantiationException | IllegalAccessException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    /**
     * Creates Java object from the XML map file
     *
     * @return a model object T
     * @throws JAXBException
     */
    @Override
    public T getObject() throws JAXBException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(this.classReference);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T object = (T) jaxbUnmarshaller.unmarshal(this.file);
            return object;
        } catch (JAXBException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    /**
     * Creates Java object from the XML map files string value which has gotten
     * from the resource folder as input stream
     *
     * @return a model object T
     * @throws JAXBException
     */
    @Override
    public T getObjectFromString() throws JAXBException {
        try {
            MAPPING_XML_LOGGER.debug("Creating xml map object from the xml map string");
            StringReader mappingXmlMapStringReader = new StringReader(xmlMapFileAsString);
            JAXBContext jaxbContext = JAXBContext.newInstance(this.classReference);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T object = (T) jaxbUnmarshaller.unmarshal(mappingXmlMapStringReader);
            MAPPING_XML_LOGGER.debug("Read data from XML map has been completed");
            return object;
        } catch (JAXBException | NullPointerException exp) {
            exp.printStackTrace(System.out);
            MAPPING_XML_LOGGER.error("Error occured when unmarshalling from Map String " + xmlMapFileAsString + ". Error : " + exp.getMessage());
            throw exp;
        }
    }

    /**
     * Sets the XML map to a file in the given directory
     *
     * @throws JAXBException
     */
    @Override
    public void setObject() throws JAXBException {
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(this.classReference);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(mappingObject, this.file);
            jaxbMarshaller.marshal(mappingObject, System.out);

        } catch (JAXBException exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    /**
     * Sets the object which comes as parameters into a file to a given location
     * in the properties file
     *
     * @param object
     * @throws Exception
     */
    @Override
    public void setObject(T object) throws Exception {
        setMappingObject(object);
        setObject();
    }

    /**
     * Reads mapper from a hard file from the given location in the properties
     * file
     *
     * @throws Exception
     */
    private void readXmlMapperFromFile() throws Exception {
        try {
            this.fileLocation = Configuration.MAPPING_XML_FILE_DIRECTORY;
            System.out.println("\n\n\n");
            MAPPING_XML_LOGGER.info("XML mapper files relative path : " + this.fileLocation);
            Resource resource = new ClassPathResource(this.fileLocation);
            this.file = resource.getFile();
            System.out.println("\n\n\n");
            MAPPING_XML_LOGGER.info("XML mapper files absolute path : " + this.file.getAbsolutePath());

            if (!this.file.exists()) {
                System.out.println("\n\n\n");
                MAPPING_XML_LOGGER.error("Mapping.xml file is not exists");
                throw new Exception("Mapping.xml file is not exists");
            }

            getXMLModelInstance();
            BufferedReader br = new BufferedReader(new FileReader(this.file));
            String mapString;
            StringBuilder mapStringBuilder = new StringBuilder();
            while ((mapString = br.readLine()) != null) {
                //System.out.println(mapString);
                mapStringBuilder.append(mapString);
            }

            xmlMapFileAsString = mapStringBuilder.toString();
            MAPPING_XML_LOGGER.debug("XML Map \n" + xmlMapFileAsString);
        } catch (FileNotFoundException | NullPointerException exp) {
            System.out.println("\n\n\n");
            MAPPING_XML_LOGGER.error("Mapping.xml file is not found in " + this.file.getAbsolutePath());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {

        }
    }

    /**
     * Reads data from an XML map file as a resource. Which resides at the
     * resource folder of the jar or project
     *
     * @return the XML mapper as string
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private String loadXmlMapperStringFromResources() throws IOException, InstantiationException, IllegalAccessException {
        System.out.println("\n\n\n");
        MAPPING_XML_LOGGER.debug("Reading XML Map from resource " + this.fileLocation + " has been started");
        try {
            try (InputStream inputStream = this.getClass().getResourceAsStream(this.fileLocation)) {
                xmlMapFileAsString = IOUtils.toString(inputStream);
                System.out.println("\n\n\n");
                MAPPING_XML_LOGGER.debug("Reading XML Map from resource " + this.fileLocation + " has been completed");
                getXMLModelInstance();
                IOUtils.closeQuietly(inputStream);
            }
            MAPPING_XML_LOGGER.debug("XML Map \n" + xmlMapFileAsString);
            return xmlMapFileAsString;
        } catch (IOException | InstantiationException | IllegalAccessException exp) {
            //exp.printStackTrace(System.out);
            System.out.println("\n\n\n");
            MAPPING_XML_LOGGER.error("Xml Map couldn''t be parsed as string from " + this.fileLocation + ". Error : " + exp.getMessage());
            throw exp;
        }
    }
}
