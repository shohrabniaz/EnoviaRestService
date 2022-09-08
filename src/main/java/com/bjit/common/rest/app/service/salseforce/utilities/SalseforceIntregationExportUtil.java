/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.salseforce.utilities;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.salesforce.intregation.SalesforceIntregationController;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.io.IOException;
import com.bjit.ex.integration.model.webservice.Item;
import com.bjit.context.ContextGeneration;
import com.bjit.mapper.mapproject.util.Constants;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Suvonkar Kundu
 */
public class SalseforceIntregationExportUtil {

    private static final org.apache.log4j.Logger Salesforce_Intregation_Controller_Logger = org.apache.log4j.Logger.getLogger(SalesforceIntregationController.class);

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SalseforceIntregationExportUtil.class);

    // File path must be configurable
    private static final String XMLFILEURL = PropertyReader.getProperty("xml.file.url");

    /**
     * Call Product Spotlight API to get MV items Information
     *
     * @param url is the url for calling export API
     * @return response String which contains MV Item information
     */
    public JSONObject getMVItemsInformation(String url) {
        ResponseEntity<String> result = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Username and Password must be configurable
            headers.add("user", "coexusr1");
            headers.add("pass", "WHZmMTYxMTE2U1Ix");
            HttpEntity<String> entity = new HttpEntity<>("contents", headers);
            result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        } catch (RestClientException ex) {
            LOGGER.error("Exception: " + ex.getMessage());
        }
        return setTitleValue(result.getBody());
    }

    public JSONObject getFinalMVItemsInformation(JSONObject result, Map<String, ItemInfo> tnrMV) {
        JSONArray newArray = new JSONArray();
        JSONObject jSONObject = new JSONObject();
        JSONArray array = result.getJSONArray("Results");
        for (int i = 0; i < array.length(); i++) {
            JSONObject target = array.getJSONObject(i);
            for (Map.Entry<String, ItemInfo> entry : tnrMV.entrySet()) {
                if ((entry.getValue().getRevision().equals(target.getString("revision")) && (entry.getValue().getName().equals(target.getString("name"))))) {
                    newArray.put(target);
                }
            }
        }
        jSONObject.put("Results", newArray);
        return jSONObject;
    }

    /**
     * Call SalesForce Token Generation API to get token
     *
     * @return response String which contains token
     */
    public String callingSalesforceTokenGenerationAPI() {
        String tokenGenerationResponse = null;
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.add("SOAPAction", "True");

            HttpEntity<String> entity = new HttpEntity<>(getRequestSoapXML(), headers);
            tokenGenerationResponse = restTemplate.postForObject(getSalesforceTokenGenerationAPIUrl(), entity, String.class);

            LOGGER.info("Response: " + tokenGenerationResponse);

        } catch (RestClientException ex) {
            LOGGER.error("Exception:" + ex.getMessage(), ex);
        }
        return tokenGenerationResponse;
    }

    /**
     * Getting Request SOAP XML body
     *
     * @return SOAP body
     */
    private String getRequestSoapXML() {
        String requestXML = "<soapenv:Envelope"
                + "	xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + "	xmlns:urn=\"urn:enterprise.soap.sforce.com\">"
                + "	<soapenv:Header></soapenv:Header>"
                + "	<soapenv:Body>"
                + "		<urn:login>"
                + "		<urn:username>enovia.api.integration@force.com.valmet.fluidodev</urn:username>"
                + "			<urn:password>jshos21639HhaHHA29</urn:password>"
                + "		</urn:login>"
                + "	</soapenv:Body>"
                + "</soapenv:Envelope>";
        return encodeUTF8(requestXML);
    }

    /**
     * Replace all multiple white spaces with single white space
     *
     * @param xml is the xml String formatted body on which the action will be
     * performed
     * @return newly generated xml body as String
     */
    private String encodeUTF8(String xml) {
        byte[] xmlBytes = xml.replaceAll("	", " ").getBytes();
        return new String(xmlBytes, StandardCharsets.UTF_8);
    }

    /**
     * Call SalesForce API, then put the JSON body and HTTP status code as a Map
     *
     * @param token is the required session token to call the API
     * @param jsonObject is the String formatted JSON which will be transferred
     * @return a map containing API JSON response and HTTP status code
     */
    public Map<JSONArray, HttpStatus> callingSalesforceIntregationAPI(String token, String jsonObject) throws IOException {
        Map<JSONArray, HttpStatus> response = new HashMap<>();
        HttpStatus statusCode = null;
        String tokenGenerationResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", token);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getSalesforceAPIUrl())
                    .queryParam("subscription-key", PropertyReader.getProperty("salesforce.subscription.key"));
            HttpEntity<String> entity = new HttpEntity<>(jsonObject, headers);
            ResponseEntity<String> out = restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, entity, String.class);
            statusCode = out.getStatusCode();
            tokenGenerationResponse = out.getBody();
            LOGGER.debug("Response: " + tokenGenerationResponse);
        } catch (HttpServerErrorException ex) {
            LOGGER.error("Exception:" + ex.getResponseBodyAsString(), ex);
            response.put(new JSONArray(ex.getResponseBodyAsString()), ex.getStatusCode());
            return response;
        } catch (RestClientException ex) {
            LOGGER.error("Exception:" + ex.getMessage(), ex);
            response.put(new JSONArray(ex.getMessage()), HttpStatus.NOT_FOUND);
            return response;
        }
        response.put(new JSONArray(tokenGenerationResponse), statusCode);
        //response.put(new JSONObject(tokenGenerationResponse), HttpStatus.SERVICE_UNAVAILABLE);

        return response;
    }
    
    public Map<String, String> itemsFromApiResponse(Map<JSONArray, HttpStatus> input){
        Map<String, String> failedItemMap = new HashMap();
        for (Map.Entry<JSONArray, HttpStatus> entry : input.entrySet()) {
            JSONArray jsonarray = entry.getKey();
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject explrObject = jsonarray.getJSONObject(i);
                failedItemMap.put(explrObject.opt("productCode").toString(), explrObject.opt("message").toString());
            }
        }
        return failedItemMap;
    }

    public String isSalesforceServiceAvailable(Map<JSONArray, HttpStatus> input) {
        String responseCode = null;
        for (Map.Entry<JSONArray, HttpStatus> salesforceResponse : input.entrySet()) {
            if (salesforceResponse.getValue() == HttpStatus.SERVICE_UNAVAILABLE) {
                responseCode = "503";
            } else if (salesforceResponse.getValue() == HttpStatus.OK) {
                responseCode = "200";
            } else if (salesforceResponse.getValue() == HttpStatus.INTERNAL_SERVER_ERROR) {
                responseCode = "500";
            }
        }
        return responseCode;
    }

    /**
     * Getting SalesForce API URL from property file
     *
     * @return SalesForce API URL String
     */
    public String getSalesforceAPIUrl() {
        String salesforceAPIURL = PropertyReader.getProperty("salesforce.api.url");
        LOGGER.info("Intregate MV to salesforce url:" + salesforceAPIURL);
        return salesforceAPIURL;
    }

    /**
     * Getting SalesForce Token Generation API URL from property file
     *
     * @return Token Generation API URL String
     */
    public String getSalesforceTokenGenerationAPIUrl() {
        String tokenGenerationAPIURL = PropertyReader.getProperty("salesforce.token.generation.url");
        LOGGER.info("Intregate MV to salesforce url:" + tokenGenerationAPIURL);
        return tokenGenerationAPIURL;
    }

    /**
     * Converting String to XML(Marshalling)
     *
     * @param xml is the String formatted XML which needs to be converted
     * @return converted XML
     */
    public Document covertStringToXML(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Setting Title value, then puts it in a map with String "Results", and
     * returns it
     *
     * @param jsonData is the main JSON String to find title
     * @return a map containing API JSON response and HTTP status code
     */
    public JSONObject setTitleValue(String jsonData) {
        JSONObject finalObject = new JSONObject();
        JSONArray mainJsonArray = new JSONArray();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray results = jsonObject.optJSONArray("Results");
            int objectIndex = 0;
            for (int i = 0; i < results.length(); i++) {
                JSONObject explrObject = results.getJSONObject(i);
                if (explrObject.get("type").equals("Products")) {
                    explrObject.put("Title", explrObject.get("Marketing Name"));
                    explrObject.put("type", explrObject.get("Type"));
                    explrObject.remove("Type");
                    explrObject.remove("Marketing Name");
                    explrObject.put("Export restriction", explrObject.get("Export restrictions"));
                    explrObject.remove("Export restrictions");

                    mainJsonArray.put(objectIndex, explrObject);
                    objectIndex++;
                }
            }
        } catch (JSONException ex) {
            LOGGER.info("Exception:" + ex.getMessage());
        }
        return finalObject.put("Results", mainJsonArray);
    }

    /**
     * Starts the loadXMLBOFiles() method
     *
     * @return Map containing XML file name as key and unmarshalled ItemInfo as
     * value
     */
    public Map<String, ItemInfo> xmlFileToModelSalesForce() {
        Map<String, ItemInfo> itemInfoMap = new HashMap<>();
        try {
            itemInfoMap = loadXmlBOFiles(XMLFILEURL);
            LOGGER.debug(itemInfoMap);
        } catch (NotDirectoryException | JAXBException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return itemInfoMap;
    }

    /**
     * Load all XML files in a directory and sends each item for unmarshalling
     *
     * @param directory determines where the action should be performed
     * @return Map containing XML file name as key and unmarshalled ItemInfo as
     * value
     */
    public Map<String, ItemInfo> loadXmlBOFiles(String directory) throws NotDirectoryException, JAXBException {
        Map<String, ItemInfo> itemMap = new HashMap<>();
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                for (Integer fileIndex = 0; fileIndex < files.length; fileIndex++) {
                    ItemInfo itemInfo = jaxbXmlFileToObject(files[fileIndex].getPath());
                    itemMap.put(files[fileIndex].getName(), itemInfo);
                }
            }
        } else {
            throw new NotDirectoryException("Directory not found");
        }
        return itemMap;
    }

    /**
     * JAXB Unmarshalling for the XML file
     *
     * @param fileName denotes the name of the XML file which needs to be
     * unmarshalled
     * @return itemInfo which is our model and converted from XML file
     */
    public ItemInfo jaxbXmlFileToObject(String fileName) throws JAXBException {
        File xmlFile = new File(fileName);
        JAXBContext jaxbContext;

        jaxbContext = JAXBContext.newInstance(ItemInfo.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        ItemInfo itemInfo = (ItemInfo) jaxbUnmarshaller.unmarshal(xmlFile);
        LOGGER.debug("ItemInfo: " + itemInfo);

        return itemInfo;
    }

    /**
     * Return the Export Product API URL
     *
     * @param name of the specific Product
     * @return complete URL to call the API
     */
    public String getExportProductAPIUrl(String name) {
        String exportProductAPIURL = PropertyReader.getProperty("export.product.api.url");
        String url = exportProductAPIURL + name + "&type=&revision=&attributeList=Level AUT,Cost editable,Cardinality Max,Marketing Name,Lock Description,type,Currency Automation,Service Track,Sales Instructions,Subscription Time Months,SalesPrice,Overage Rate,Cost Actual,Sales Configurable,Cardinality Min,Spare Part Coefficient,Business,owner,MainProduct,Reference price,LogisticsHours,License Pricing Type,Cost Adjusted,Aton Version,Title,description,AUT Lifecycle Status,revision,Cost Use Adjusted Cost,License Weight Factor,Export restrictions,Replaced by,AS License Type,Product Group,name,Spare part category VED,Type,License Capacity Provider ID,ReferenceCode,Quantity Unit of Measure,MOD_Mastership,Sales statistical group SSG,Price editable,Language AUT,Delivery valid to specific country,email,classification path";
        LOGGER.info("Export Product API URL:" + url);
        return url;
    }

    /**
     * Call SalesForce API, then put the JSON body and HTTP status code as a Map
     *
     * @param token is the required session token to call the API
     * @param jsonObject is the String formatted JSON which will be transferred
     * @return a map containing API JSON response and HTTP status code
     */
    public ResponseEntity<String> callingCostUpdateAPI(String url) {
        ResponseEntity<String> result = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>("contents", headers);
            result = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        } catch (RestClientException ex) {
            LOGGER.error("Exception: " + ex.getMessage());
        }
        return result;
    }

    /**
     * Getting Cost Update API URL from property file
     *
     * @return Cost Update API URL String
     */
    public String getCostUpdateAPIUrl(String name) {
        String exportProductAPIURL = PropertyReader.getProperty("cost.update.api.url");
        String url = exportProductAPIURL + name;
        LOGGER.info("Cost Update API URL:" + url);
        return url;
    }

    /**
     * Return the SelesForce session Id
     *
     * @param response is XML response of SOAP
     * @return extracted sessionID from the XML
     */
    public String getSessionID(String response) {
        String sessionId = "Bearer ";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "//sessionId";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) node;
                    sessionId = sessionId + eElement.getTextContent();
                }
            }
        } catch (IOException | ParserConfigurationException | XPathExpressionException | SAXException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return sessionId;
    }

    /**
     * Return true/false if any file/s with .xml extension exist in the
     * particular directory
     *
     * @param direcorty is the path of where the file existance should we check
     * @return true if there is any XML file in the directory or vice versa
     */
    public Boolean isAnyFileExist(String directory) {
        File dir = new File(directory);
        Boolean result = false;
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));
            if (files.length != 0) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Move file/s to old or error folder
     *
     * @param status determines where the file will go. "success" for old folder
     * and else is error folder.
     * @param fileName contains the name of the file which needs to be moved
     * @return successfully moved message or empty string for failure
     */
    public String moveXmlFile(String status, String fileName) throws IOException {
        String result = "";
        if (status.equals("success")) {
            File theDir = new File(XMLFILEURL + "//old");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            Files.move(Paths.get(XMLFILEURL + "//" + fileName), Paths.get(XMLFILEURL + "//old//" + fileName), StandardCopyOption.REPLACE_EXISTING);
            result = "Successfully moved to old folder";
        } else {
            File theDir = new File(XMLFILEURL + "//error");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            Files.move(Paths.get(XMLFILEURL + "//" + fileName), Paths.get(XMLFILEURL + "//error//" + fileName), StandardCopyOption.REPLACE_EXISTING);
            result = "Successfully moved to error folder";
        }
        return result;
    }

    /**
     * Prepares itemInfo model and other things to call initializeResultSender()
     * method and return recipient's email address
     *
     * @param itemInfo is the model of our working item
     * @return recipient's email address
     */
    public List<String> emailSending(ItemInfo itemInfo) throws MatrixException {

        Context context = null;
        BusinessObject businessObject = null;
        try {
            context = getContext();
            businessObject = new BusinessObject(itemInfo.getId());
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
        Item item = new Item();
        Map<String, String> itemMap = new HashMap();
        itemMap.put("state-to", itemInfo.getNextState());
        itemMap.put("state-from", itemInfo.getCurrentState());
        item.setAttributes(itemMap);
        item.setId(itemInfo.getId());
        EmailSender emailSender = new EmailSender();
        return emailSender.initializeResultSender(context, businessObject, item);
    }

    public String getSalesforceAPIToken() throws MalformedURLException, ExecutionException, InterruptedException {
        // Get Access Token from Azure AD OAuth
        LOGGER.info("Getting Access Token");
        Instant startTime = Instant.now();
        SalesforceOAuthManager salesforceOAuthManager = new SalesforceOAuthManager();
        String accessToken = salesforceOAuthManager.getAccessToken();
        Instant endTime = Instant.now();
        LOGGER.info("Access Token Acquired. Taken time: " + Duration.between(startTime, endTime));
        return accessToken;
    }

    private Context getContext() {
        Context context = null;
        try {
            CreateContext createContext = new CreateContext();

            context = createContext.getAdminContext();
            if (!context.isConnected()) {
                throw new Exception(Constants.CONTEXT_EXCEPTION);
            }
        } catch (Exception exp) {
            LOGGER.error("Report generation error: " + exp.getMessage());
            throw new RuntimeException(exp);
        }
        return context;
    }
}
