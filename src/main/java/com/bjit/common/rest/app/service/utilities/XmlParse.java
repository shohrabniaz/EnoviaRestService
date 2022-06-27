/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.ewc18x.utils.PropertyReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sajjad
 */
public class XmlParse {

    private static final org.apache.log4j.Logger XML_PARSE_LOGGER = org.apache.log4j.Logger.getLogger(XmlParse.class);
    public static final String FILE_PATH = PropertyReader.getProperty("map.xml.xpath.predefined.value.file");
    public static final String XML_ROOT_PATH = PropertyReader.getProperty("map.xml.xpath.predefined.value.file.xmlRootPath");

    /*public static void main(String[] args) {
        try {
            String value = getPredefinedValue("OwnerGroup_CollaborationSpace", "PDM", "OwnerGroup", "CollaborationSpace", "Air systems");
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
            XML_PARSE_LOGGER.error(ex);
        }
    }*/
    public String getPredefinedValue(String mappingValueType, String masterSource, String masterDiscriminator, String V6_Discriminator, String sourceValue) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String filePath = getClass().getResource(FILE_PATH).getFile();

        XML_PARSE_LOGGER.debug("Map file path : " + FILE_PATH);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        
        Document doc = dBuilder.parse(filePath);
        doc.getDocumentElement().normalize();
        XPath xPath = XPathFactory.newInstance().newXPath();
        String startBracket = "[";
        String endBracket = "]";
        String singleQuote = "'";
        String pathSeparator = "/";
        String space = " ";
        String andOperator = "and";

        StringBuilder expressionBuilder = new StringBuilder();
        expressionBuilder.append(XML_ROOT_PATH)
                .append(mappingValueType).append(pathSeparator).append("Master[@src=").append(singleQuote)
                .append(masterSource).append(singleQuote).append(andOperator).append("@discriminator=")
                .append(singleQuote).append(masterDiscriminator).append(singleQuote).append(endBracket)
                .append(pathSeparator).append("EnoviaV6[@discriminator=").append(singleQuote).append(V6_Discriminator)
                .append(singleQuote).append(endBracket).append(pathSeparator).append("value[@src=").append(singleQuote)
                .append(sourceValue).append(singleQuote).append(endBracket);

        String expression = expressionBuilder.toString();
        XML_PARSE_LOGGER.debug("XPath expression is : " + expression);
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        String predefinedValue = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            predefinedValue = node.getTextContent();
        }
        XML_PARSE_LOGGER.debug("Value is : " + predefinedValue);
        return predefinedValue;
    }

    public String getAttributeValue(String elementPath, String attributeName) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        try {
            Resource resource = new ClassPathResource(FILE_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(resource.getURI().getPath());
            XML_PARSE_LOGGER.debug("Path:: " + resource.getURI().getPath());
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            Element element = (Element) xpath.evaluate(elementPath, document, XPathConstants.NODE);

            if (NullOrEmptyChecker.isNull(element)) {
                throw new NullPointerException("'" + elementPath + "' not found at '" + FILE_PATH + "' file");
            }

            String attribute = element.getAttribute(attributeName);

            if (NullOrEmptyChecker.isNullOrEmpty(attribute)) {
                XML_PARSE_LOGGER.debug("Value of '" + attributeName + "' in '" + elementPath + "' at '" + FILE_PATH + "' has not set perfectly");
                //throw new NullPointerException("Value of '" + attributeName + "' has not set perfectly");
            }

            return attribute;
        } catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
            XML_PARSE_LOGGER.error(ex);
            throw ex;
        } catch (IOException ex) {
            XML_PARSE_LOGGER.debug(ex);
            throw new IOException("'" + FILE_PATH + "' file not found");
        } catch (NullPointerException ex) {
            XML_PARSE_LOGGER.debug(ex);
            throw ex;
        } catch (Exception ex) {
            XML_PARSE_LOGGER.debug(ex);
            throw ex;
        }
    }
}
