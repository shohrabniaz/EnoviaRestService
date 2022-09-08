package com.bjit.plmkey.ws.controller.expandobject;

import com.bjit.ewc18x.model.ExpandObjectForm;
import com.bjit.ewc18x.model.Response;
import com.bjit.ewc18x.model.Status;
import com.bjit.ewc18x.utils.CustomException;
import com.bjit.ewc18x.utils.JasperReportGenerator;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.XMLParser;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import matrix.db.BusinessInterface;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.State;
import matrix.db.StateList;
import matrix.db.Vault;
import matrix.util.MatrixException;
import org.xml.sax.SAXException;

/**
 *
 * @author Sajjad
 */
public class ExpandObjectUtil {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExpandObjectUtil.class);
    private final static String GRAPHICS_PROPERTY = "java.awt.headless";
    private final static String GRAPHICS_SUPPORT_ENABLED = "true";
    public static Map<String, String> replaceJSONMapString = new LinkedHashMap<>();
    public static List<String> finalSelectedAttributeList = new ArrayList<>();
    public static List<String> finalSelectedRelAttributeList = new ArrayList<>();
    public static List<String> finalSelectedObjParamList = new ArrayList<>();
    public static String getPhycialId(Context context, ExpandObjectForm expandObjectForm) throws CustomException {
        MqlQueries mqlQueries = new MqlQueries();
        String query = "print bus '" + expandObjectForm.getType() + "' '" + expandObjectForm.getName() + "' '" + expandObjectForm.getRevision() + "' " + " select physicalid";
        logger.info("Query : " + query);
        String queryResult = mqlQueries.getQueryResults((Context) context, query);
        logger.info("Returend result is : " + queryResult);
        String physicalId = queryResult.substring(queryResult.lastIndexOf("=") + 1, queryResult.length()).trim();
        return physicalId;
    }

    /**
     * This function accept XML service file as parameter and response the list of type pattern
     * @param serviceFile service XML file as parameter
     * @return List<String> of type pattern
     * @throws CustomException
     */
    public static List<String> getExpandObjectTypeList(File serviceFile) throws CustomException {
        try {
            XMLParser xMLParser = new XMLParser(serviceFile);
            String typePatterns = xMLParser.getTextContentByTagName("typePattern");
            String[] typeNameArray = typePatterns.split(",");
            List<String> typeNameList = Arrays.asList(typeNameArray);
            return typeNameList;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ExpandObjectUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new CustomException(ex.getMessage());
        }
    }
    public static Object getKeyFromValue(Map map, Object value) {
        for (Object o : map.keySet()) {
            if (map.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
    
    public static String getBusinessObjectByTypeName(Context context,String type,String name) throws MatrixException {
        String objectId="";
        Map<String, String> params = new HashMap();
        params.put("name",name.trim());
        params.put("type",type);
        objectId  = (String) JPO.invoke(context, "CloneObjectUtil", null, "searchByTypeName",  JPO.packArgs(params), String.class);
        return objectId;
    }
    
     public static String getLatestRevisonOnState(Context context, String busId,String state) throws MatrixException {
        String rev = "";
        BusinessObject bus = new BusinessObject(busId);
        bus.open(context);
        BusinessObjectList busL = bus.getRevisions(context);
        for (int i=0;i<busL.size();i++) {
            BusinessObject temp = busL.get(i);
            temp.open(context);
            StateList stateList = temp.getStates(context);
            for (int j=0;j<stateList.size();j++) {
                State s = stateList.get(j);
                logger.debug("Current state : " + s.isCurrent() + "; state name : " + s.getName());
                if (s.isCurrent() && state.equalsIgnoreCase(s.getName())) {
                    rev = temp.getRevision();
                }
            }
        }
        return rev;
    }
     
    private static String getExtensionName(String attribute) throws Exception {
        if(attribute.contains("."))
           return attribute.split(".")[0];
        return "";
    } 
     
    private static boolean doesExtensionExist(Context ctx, BusinessObject busObj, String interfaceName) throws MatrixException {
        if (interfaceName != null || !interfaceName.isEmpty()) {
            BusinessInterfaceList busInterfaceList = busObj.getBusinessInterfaces(ctx);
            for (ListIterator<?> busInterfaceListItr = busInterfaceList.listIterator(); busInterfaceListItr.hasNext();) {
                BusinessInterface busInterface = (BusinessInterface) busInterfaceListItr.next();
                logger.debug("Business interface name : " + busInterface.getName());
                if(busInterface.getName().equals(interfaceName)) {
                    return true;
                }
            }
        }
        return false;
    } 
    
    public static void addNewExtension(Context ctx, BusinessObject busObject, String attributeName) throws MatrixException, Exception {
        String interfaceName = getExtensionName(attributeName);
        boolean doesInterfaceExist = doesExtensionExist(ctx, busObject, interfaceName);
        if(!doesInterfaceExist) {
            busObject.addBusinessInterface(ctx, new BusinessInterface(interfaceName,new Vault("")));
            logger.info("Business Interface " + interfaceName + " has been added successfully.");
        } else 
            logger.debug("Business Interface " + interfaceName + " already Exists.");
    }
    
    /**
     * Graphics support is important for Report Generation
     * It depends on the underlying operation system property
     * following method validates and does the support operation
     */
    public static void addGraphicsSupport() throws Exception {
        String isHeadless = System.getProperty(GRAPHICS_PROPERTY);
        if (!isHeadless.equalsIgnoreCase(GRAPHICS_SUPPORT_ENABLED)) {
            System.setProperty(GRAPHICS_PROPERTY, GRAPHICS_SUPPORT_ENABLED);
        }
    }
    
    public static boolean hasPatternMatched(String s, String pattern) throws Exception {
        Pattern patt = Pattern.compile(pattern);
        Matcher matcher = patt.matcher(s);
        return matcher.matches();
    }
    
    public static List<String> getAttributeList(String attributes) throws Exception {
        List<String> attributeList = new ArrayList<>();
        for (String attribute : attributes.split(",")) {
            attributeList.add(attribute.trim());
        }
        if(!attributeList.contains("Type")) {
            attributeList.add("Type");
        }
        return attributeList;
    }
    
    
    public static void getFinalSelectedAttributeList(List<String> selectedAttrList, List<String> propertyList, List<String> notPropertyNotAttributeList, List<String> allRelAttributeList, Map<String, String> allItemMap) throws Exception {
        for (String listItem : selectedAttrList) {
            if (propertyList.contains(listItem) || notPropertyNotAttributeList.contains(listItem)) {
                finalSelectedObjParamList.add("'" + ExpandObjectUtil.getKeyFromValue(allItemMap, listItem) + "'");
            } else if (allRelAttributeList.contains(listItem)) {
                String attrName = (String) ExpandObjectUtil.getKeyFromValue(allItemMap, listItem);
                replaceJSONMapString.put(listItem, attrName);
                finalSelectedRelAttributeList.add("'attribute[" + attrName + "]'");
            } else {
                String attrName = (String) ExpandObjectUtil.getKeyFromValue(allItemMap, listItem);
                replaceJSONMapString.put(listItem, attrName);
                finalSelectedAttributeList.add("'attribute[" + attrName + "]'");
            }
        }  
    }
    
    public static String generateReport(String JSONData, String objectName, String reportName, String requestId, String format) throws Exception {
        if (!format.equalsIgnoreCase("pdf")) {
            throw new CustomException("Only PDF format report can be generated, please try again with pdf format.");
        } else {
            JasperReportGenerator jasperReportGenerator = new JasperReportGenerator();
            jasperReportGenerator.setJsonString(JSONData);
            String reportLink = "";
            try {
                reportLink = jasperReportGenerator.generatePdfReport(reportName, requestId);
                return reportLink;
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }
    
    public static String getRev(Context context, String requestId, String type, String name, String rev) throws Exception {
        if (rev == null || rev.isEmpty()) {
            String objectId = ExpandObjectUtil.getBusinessObjectByTypeName(context, type, name);
            rev = ExpandObjectUtil.getLatestRevisonOnState(context, objectId, "RELEASED");
            if (rev.isEmpty()) {
                return new Response(name, Status.FAILED, requestId, name + " does not have any released state.").toString();
            }
        }
        return rev;
    }
    
    public static String getConfigFileNameFromService(File serviceFile, String tagName) throws ParserConfigurationException, SAXException, IOException {
        return new XMLParser(serviceFile).getTextContentByTagName(tagName);
    }
}
