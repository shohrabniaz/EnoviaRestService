/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.ewc18x.utils;

import com.bjit.plmkey.ws.controller.expandobject.ExpandObjectRestService;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Sudeepta
 */
public class XMLParser {

    private Document doc;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(XMLParser.class);
    
    /**
     * To create the instance you have to provide the file of the xml
     * @param fXmlFile XML file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public XMLParser(File fXmlFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
    }

    /**
     * This function accept the tag name of xml and return the first element value with the tag name
     * If the tag name not found then return null
     * @param tagName provide the tag name of the xml
     * @return String Text Content
     */
    public String getTextContentByTagName(String tagName) {
        System.out.println("Root element :" + doc.getElementsByTagName(tagName).item(0).getTextContent());
        return doc.getElementsByTagName(tagName).item(0).getTextContent();
        //System.out.println("Root element :" + doc.getElementsByTagName(tagName).item(0).getTextContent());
        /*if (doc.getElementsByTagName(tagName).getLength() > 0) {
            return doc.getElementsByTagName(tagName).item(0).getTextContent();
        } else {
            return null;
        }*/
    }
    
    public static void main(String[] args) {
        try {
            File file = new File("src\\main\\resources\\common\\services\\BOMExport.xml");
            System.out.println(file.getAbsolutePath());
            String s = new XMLParser(file).getTextContentByTagName("typePatterns");
            System.out.println(s);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
