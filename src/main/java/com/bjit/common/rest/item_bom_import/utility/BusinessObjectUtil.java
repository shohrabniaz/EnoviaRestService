/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.utility;

import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.CommonSearch;
import com.bjit.common.rest.app.service.utilities.Conditionals;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.MQLCommand;
import matrix.db.Query;
import matrix.db.RelationshipList;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.State;
import matrix.db.StateList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;
import org.xml.sax.SAXException;

/**
 *
 * @author Sajjad
 */
public class BusinessObjectUtil {

    private static final org.apache.log4j.Logger BUSINESS_OBJECT_UTIL_LOGGER = org.apache.log4j.Logger.getLogger(BusinessObjectUtil.class);

    /**
     *
     * @param context
     * @param type
     * @param name
     * @return
     * @throws MatrixException
     */
    public BusinessObject getLatestRevBO(Context context, String type, String name) throws MatrixException {
        StringList busSelect = new StringList(4);
        busSelect.addElement("id");
        busSelect.addElement("type");
        busSelect.addElement("name");
        busSelect.addElement("revision");
        busSelect.addElement("originated");

        Query query = new Query();
        query.setBusinessObjectType(type);
        query.setBusinessObjectName(name);
        query.setBusinessObjectRevision("*");

        BusinessObjectWithSelectList bwsList = new BusinessObjectWithSelectList();

        bwsList = query.selectTmp(context, busSelect);
        if (bwsList.isEmpty()) {
            return null;
        }

        HashMap<String, Date> itemOriginatedDateMap = new HashMap<>();
        for (int i = 0; i < bwsList.size(); i++) {
            BusinessObjectWithSelect bws = (BusinessObjectWithSelect) bwsList.elementAt(i);
            String id = (String) bws.getSelectData("id");
            String sRevision = (String) bws.getSelectData("revision");
            String created = (String) bws.getSelectData("originated");
            itemOriginatedDateMap.put(id, new Date(created));
        }

        LinkedHashMap<String, Date> itemOriginatedDateMapSorted = itemOriginatedDateMap
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));

        BusinessObject latestBO = new BusinessObject(itemOriginatedDateMapSorted.entrySet().iterator().next().getKey());
        return latestBO;
    }
//    public static synchronized BusinessObject getLatestRevBO(Context context, String type, String name) throws MatrixException, IOException {
//        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//        String isLastRevision = commonPropertyReader.getPropertyValue("item.is.last.version");
//
//        StringList busSelect = new StringList(4);
//        busSelect.addElement("id");
//        busSelect.addElement("type");
//        busSelect.addElement("name");
//        busSelect.addElement("revision");
//        busSelect.addElement("attribute[" + isLastRevision + "]");
//
//        Query query = new Query();
//        query.setBusinessObjectType(type);
//        query.setBusinessObjectName(name);
//        query.setBusinessObjectRevision("*");
//
//        BusinessObjectWithSelectList bwsList = new BusinessObjectWithSelectList();
//
//        bwsList = query.selectTmp(context, busSelect);
//        if (bwsList.isEmpty()) {
//            return null;
//        }
//
//        HashMap<String, Date> itemOriginatedDateMap = new HashMap<>();
//        BusinessObject latestBO = null;
//        for (int i = 0; i < bwsList.size(); i++) {
//            BusinessObjectWithSelect bws = (BusinessObjectWithSelect) bwsList.elementAt(i);
//            String id = (String) bws.getSelectData("id");
//            //String sRevision = (String) bws.getSelectData("revision");
//            //String created = (String) bws.getSelectData("originated");
//            String isLastVersion = (String) bws.getSelectData("attribute[" + isLastRevision + "]");
//            if (isLastVersion.equalsIgnoreCase("TRUE")) {
//                latestBO = new BusinessObject(id);
//                break;
//            }
//        }
//        return latestBO;
//    }

//    public static synchronized BusinessObject reviseBO(Context context, BusinessObject reversibleBO) throws MatrixException {
//        String command = "revise businessobject " + reversibleBO.getObjectId() + " major select id dump |;";
//    BUSINESS_OBJECT_UTIL_LOGGER.info (command);
//        String queryResult = MqlUtil.mqlCommand(context, command);
//        String id = queryResult;
//        return new BusinessObject(id);
//    }
    /**
     *
     * @param context
     * @param reversibleBO
     * @return
     * @throws IOException
     */
    public BusinessObject reviseBO(Context context, BusinessObject reversibleBO) throws IOException, InterruptedException {
        String id = "";
        BusinessObject revisedBO = null;
        boolean isError = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        String isLastRevision = commonPropertyReader.getPropertyValue("item.is.last.version");
        try {
            String command = "revise businessobject " + reversibleBO.getObjectId() + " major select id dump |;";
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Revise query : " + command);
            //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
            String queryResult = MqlUtil.mqlCommand(context, command);
            id = queryResult;
            revisedBO = new BusinessObject(id);
        } catch (MatrixException ex) {
            isError = true;
            BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + ex.getMessage());
        }
        if (!isError) {
            try {
                reversibleBO.setAttributeValue(context, isLastRevision, "FALSE");
            } catch (MatrixException ex) {
                BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + ex.getMessage());
            }
        }

        return revisedBO;
    }

    /**
     *
     * @param context
     * @param reversibleBO
     * @param isLastVersionAvailableInPDM
     * @return
     * @throws IOException
     */
    public BusinessObject reviseBO(Context context, BusinessObject reversibleBO, Boolean isLastVersionAvailableInPDM) throws IOException, InterruptedException {
        String id = "";
        BusinessObject revisedBO = null;
        boolean isError = false;
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        String isLastRevision = commonPropertyReader.getPropertyValue("item.is.last.version");
        try {
            String command = "revise businessobject " + reversibleBO.getObjectId() + " major select id dump |;";
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Revise query : " + command);
            //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
            String queryResult = MqlUtil.mqlCommand(context, command);
            id = queryResult;
            revisedBO = new BusinessObject(id);
        } catch (MatrixException ex) {
            isError = true;
            BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + ex.getMessage());
        }
        if (!isError) {
            try {
                reversibleBO.setAttributeValue(context, isLastRevision, "FALSE");

                revisedBO.setAttributeValue(context, isLastRevision, isLastVersionAvailableInPDM ? "FALSE" : "TRUE");
//                if (isLastVersionAvailableInPDM) {
//                    revisedBO.setAttributeValue(context, isLastRevision, "FALSE");
//                }
            } catch (MatrixException ex) {
                BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + ex.getMessage());
            }
        }

        return revisedBO;
    }

    /**
     *
     * @param context
     * @param type
     * @param name
     * @param PDMRev
     * @return
     * @throws MatrixException
     */
    public BusinessObject findPDMItem(Context context, String type, String name, String PDMRev) throws MatrixException, IOException {

        CommonSearch itemSearch = new CommonSearch();

        if (!type.equalsIgnoreCase("val*")) {
            itemSearch.nested();
            String[] itemTypes = type.split("\\|");
            for (int iterator = 0; iterator < itemTypes.length; iterator++) {
                itemSearch.prepareWhere("type", Conditionals.EQUALS, itemTypes[iterator]);
                if (iterator < (itemTypes.length - 1)) {
                    itemSearch.or();
                }
            }
            itemSearch
                    .done()
                    .and()
                    .prepareWhere("attribute[" + new CommonPropertyReader().getPropertyValue("item.import.enovia.pdm.attribute.revision") + "].value", Conditionals.EQUALS, PDMRev);
        }
        try {
            List<HashMap<String, String>> searchPreparedItem = itemSearch.searchPreparedItem(context, new TNR(!type.equalsIgnoreCase("val*") ? null : type, name, null), null, null);
            if (searchPreparedItem.size() > 1) {
                throw new RuntimeException("Multiple item has same name '" + name + "'");
            }
            return new BusinessObject(searchPreparedItem.get(0).get("id"));

        } catch (Exception ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + ex.getMessage());
            return null;
        }
    }
    //        BUSINESS_OBJECT_UTIL_LOGGER.debug("Start finding item :: Type :  " + type + " Name : " + name + " PDM revision : " + PDMRev);
    //
    //        StringList busSelect = new StringList(4);
    //        busSelect.addElement("id");
    //        busSelect.addElement("type");
    //        busSelect.addElement("name");
    //        busSelect.addElement("revision");
    //
    //        Query query = new Query();
    //        query.setBusinessObjectType(type);
    //        query.setBusinessObjectName(name);
    //        query.setBusinessObjectRevision("*");
    //
    //        BusinessObjectWithSelectList bwsList = new BusinessObjectWithSelectList();
    //        bwsList = query.selectTmp(context, busSelect);
    //
    //        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
    //        for (int itr = 0; itr < bwsList.size(); itr++) {
    //            BusinessObjectWithSelect bws = (BusinessObjectWithSelect) bwsList.elementAt(itr);
    //            String id = (String) bws.getSelectData("id");
    //            BusinessObject bo = new BusinessObject(id);
    //            try {
    //                String PDMRevAtt = bo.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision")).getValue();
    //                if (PDMRev != null && PDMRev.equals(PDMRevAtt)) {
    //                    BUSINESS_OBJECT_UTIL_LOGGER.debug("Item found :: Type :  " + type + " Name : " + name + " PDM revision : " + PDMRevAtt);
    //                    return bo;
    //                }
    //            } catch (MatrixException e) {
    //                BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + e.getMessage());
    //                BUSINESS_OBJECT_UTIL_LOGGER.error("Result returned from server : " + e);
    //            }
    //        }
    //        return null;
    //    }

    /**
     *
     * @param context
     * @param type
     * @param name
     * @param rev
     * @param attributeMap
     * @return
     * @throws MatrixException
     */
    public ArrayList<BusinessObject> findBO(Context context, String type, String name, String rev, HashMap<String, String> attributeMap) throws MatrixException {
        StringList busSelect = new StringList(4);
        busSelect.addElement("id");
        busSelect.addElement("type");
        busSelect.addElement("name");
        busSelect.addElement("revision");

        Query query = new Query();
        query.setBusinessObjectType(type);
        query.setBusinessObjectName(name);
        if (rev == null || rev.equals("")) {
            query.setBusinessObjectRevision("*");
        } else {
            query.setBusinessObjectRevision(rev);
        }
        StringBuilder whereExpBuilder = new StringBuilder();
        int i = 0;
        for (String attributeName : attributeMap.keySet()) {
            if (i > 0) {
                whereExpBuilder.append("&&");
            }
            //whereExpBuilder.append("attribute[").append(attributeName).append("]").append("==").append("'").append(StringEscapeUtils.escapeJava(attributeMap.get(attributeName))).append("'");
            whereExpBuilder.append("attribute[").append(attributeName).append("]").append("==").append("'").append(attributeMap.get(attributeName)).append("'");
        }

        String whereExpression = whereExpBuilder.toString();

        query.setWhereExpression(whereExpression);
        BusinessObjectWithSelectList bwsList = new BusinessObjectWithSelectList();
        bwsList = query.selectTmp(context, busSelect);
        ArrayList<BusinessObject> boList = new ArrayList<>();

        for (int itr = 0; itr < bwsList.size(); itr++) {
            BusinessObjectWithSelect bws = (BusinessObjectWithSelect) bwsList.elementAt(itr);
            String id = (String) bws.getSelectData("id");
            boList.add(new BusinessObject(id));
        }
        return boList;
    }

    /**
     *
     * @param context
     * @param tnr
     * @return
     * @throws MatrixException
     */
    public ArrayList<BusinessObject> findBO(Context context, TNR tnr) throws MatrixException {
        String tempQuery = "temp query bus '" + tnr.getType() + "' '" + tnr.getName() + "' '" + tnr.getRevision() + "' select id dump";
        BUSINESS_OBJECT_UTIL_LOGGER.debug(tempQuery);
        MQLCommand mqlCommand = new MQLCommand();
        String mqlResult = MqlUtil.mqlCommand(context, mqlCommand, tempQuery);
        BUSINESS_OBJECT_UTIL_LOGGER.debug(mqlResult);

        ArrayList<BusinessObject> boList = new ArrayList<>();

        if (!NullOrEmptyChecker.isNullOrEmpty(mqlResult)) {
            List<String> dataList = Arrays.asList(mqlResult.split("\n"));

            dataList.stream().forEach((String objectData) -> {
                try {
                    String[] objectProperties = objectData.split(",");

                    boList.add(new BusinessObject(objectProperties[objectProperties.length - 1]));
                } catch (MatrixException ex) {
                    BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
                }

            });
        }

        return boList;
    }

    public BusinessObject getBOByTNR(Context context, TNR tnr) {
        BusinessObject bo = null;
        try {
            if (tnr.getRevision() == null || tnr.getRevision().isEmpty()) {
                bo = getLatestRevBO(context, tnr.getType(), tnr.getRevision());
            } else {
                bo = findBO(context, tnr).get(0);
            }
        } catch (Exception ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
        }
        return bo;
    }

    /**
     *
     * @param context
     * @param fromID
     * @param toID
     * @param relationshipName
     * @return
     * @throws MatrixException
     */
    //print connection bus 23040.48585.28004.17758 to Provide  prvd-28952824-00001137  1.1 relationship DELFmiFunctionIdentifiedInstance select id dump |
    public String checkToRelationship(Context context, String fromID, String toID, String relationshipName) throws MatrixException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("print connection bus ")
                .append(fromID).append(" to ").append(toID)
                .append(" relationship ").append(relationshipName)
                .append(" select id dump |;");
        String connectionCheckQuery = commandBuilder.toString();
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Connection check query : " + connectionCheckQuery);
        String relId = "";
        String queryResult = MqlUtil.mqlCommand(context, connectionCheckQuery);
        relId = queryResult;
        return relId;
    }

    /**
     *
     * @param context
     * @param fromID
     * @param toID
     * @param relationshipName
     * @param positionAttribute
     * @param positionAttributeValue
     * @return
     * @throws MatrixException
     */
    //print bus 40184.9451.51818.29006 select from[DELFmiFunctionIdentifiedInstance|attribute[MBOM_MBOMInstance.MBOM_Position]=="2" && to.id=="40184.9451.42149.59219"].id dump
    public ArrayList<String> checkToRelationshipWithPosition(Context context, String fromID, String toID, String relationshipName, String positionAttribute, String positionAttributeValue) throws MatrixException {
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Checking connections between Parent and child ");
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("print bus ")
                .append(fromID).append(" select from[").append(relationshipName)
                .append("|attribute[").append(positionAttribute)
                .append("]=='").append(positionAttributeValue).append("' && to.id=='")
                .append(toID).append("'].id dump;");
        String connectionCheckQuery = commandBuilder.toString();
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Connection check query : " + connectionCheckQuery);
        String queryResult = MqlUtil.mqlCommand(context, connectionCheckQuery);
        if (queryResult.trim().length() == 0) {
            return new ArrayList<>();
        }
        ArrayList<String> connectedChildRelIDlist = new ArrayList<>(Arrays.asList(queryResult.split(",")));
        return connectedChildRelIDlist;
    }

    /**
     *
     * @param context
     * @param relID
     * @throws MatrixException
     */
    //disconnect connection 23040.48585.28004.8821:BSF
    public void disconnectRelationship(Context context, String relID) throws MatrixException {
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Desconnection relationship ::  " + relID);
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("disconnect connection ").append(relID).append(";");
        String disconnectQuery = commandBuilder.toString();
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Connection check query : " + disconnectQuery);
        MqlUtil.mqlCommand(context, disconnectQuery);
    }

    /**
     *
     * @param context
     * @param connectionId
     * @param relAttributeMap
     * @param relPropertyMap
     * @throws MatrixException
     */
    //modify connection 23040.48585.60712.17799:BSF "MBOM_MBOMInstance.MBOM_Set_Quantity" "2" "MBOM_MBOMInstance.MBOM_Position" "2" select id dump |
    //public static synchronized void modifyConnection(Context context, String connectionId, HashMap<String, String> relAttributeMap, HashMap<String, String> relPropertyMap) throws MatrixException {
    public void modifyConnection(Context context, String connectionId, HashMap<String, String> relAttributeMap, HashMap<String, String> relPropertyMap) throws MatrixException, InterruptedException {
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Start modifying connection : " + connectionId);
        StringBuilder relAttrPropNameValueQuery = new StringBuilder();
        for (String relAttributeName : relAttributeMap.keySet()) {
            relAttrPropNameValueQuery
                    .append("\"")
                    .append(relAttributeName)
                    .append("\"")
                    .append(" ")
                    .append("\"")
                    .append(relAttributeMap.get(relAttributeName))
                    .append("\"")
                    .append(" ");
        }

        for (String relPropertyName : relPropertyMap.keySet()) {
            relAttrPropNameValueQuery
                    .append("\"")
                    .append(relPropertyName)
                    .append("\"")
                    .append(" ")
                    .append("\"")
                    .append(relPropertyMap.get(relPropertyName))
                    .append("\"")
                    .append(" ");
        }

        StringBuilder cmndBuilder = new StringBuilder();
        String relationAttributeNameValueQuery = relAttrPropNameValueQuery.toString();
        cmndBuilder
                .append("modify connection")
                .append(" ")
                .append(connectionId)
                .append(" ")
                .append(relationAttributeNameValueQuery)
                .append("select id dump |;");

        String connectionUpdateQuery = cmndBuilder.toString();
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Update query : " + connectionUpdateQuery);

        //Thread.sleep(Long.parseLong(Optional.ofNullable(PropertyReader.getProperty("bus.modification.thread.sleep.time.in.millis")).orElse("0")));
        String queryResult = MqlUtil.mqlCommand(context, connectionUpdateQuery);
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Updated connection : " + queryResult);
    }

//    public synchronized static String getPersonOrganization(Context context, String collaborationSpace) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
//        String organization = "";
//        String personName = context.getUser();
//        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//
//        //CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//        String defaultOrganizationElementPath = commonPropertyReader.getPropertyValue("item.import.organization.default");
//        String defaultOrganization = XmlParse.getAttributeValue(defaultOrganizationElementPath, "defaultOrganization");
//        BUSINESS_OBJECT_UTIL_LOGGER.info("Default Organization is: " + defaultOrganization);
//
//        try {
//            String command = "print person " + personName + " select ASSIGNMENT dump @;";
//    BUSINESS_OBJECT_UTIL_LOGGER.info (command);
//            String queryResult = MqlUtil.mqlCommand(context, command);
//            
//            BUSINESS_OBJECT_UTIL_LOGGER.debug("Roles found for '" + personName + "' are '" + queryResult + "'");
//            
//            if (queryResult.length() > 0) {
//                String[] queryResultArray = queryResult.split("@");
//                try {
//                    for (int i = 0; i < queryResultArray.length; i++) {
//                        String splitedString = queryResultArray[i];
//                        BUSINESS_OBJECT_UTIL_LOGGER.debug("Person Role : " + splitedString);
//                        BUSINESS_OBJECT_UTIL_LOGGER.debug("Collaboration Space : " + collaborationSpace);
//                        String cs = splitedString.split("\\.")[2];
//                        BUSINESS_OBJECT_UTIL_LOGGER.debug("Collaboration space found from the users role is : '" + cs + "'");
//                        if (cs.equalsIgnoreCase(collaborationSpace)) {
//                            String org = splitedString.split("\\.")[1];
//                            organization = org;
//                            break;
//                        }
//                    }
//                } catch (Exception exp) {
//                    BUSINESS_OBJECT_UTIL_LOGGER.error(exp);
//                    //return commonPropertyReader.getPropertyValue("item.organization.default.createAssembly");
//                    return defaultOrganization;
//                }
//
//            }
//        } catch (FrameworkException ex) {
//            BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
//        }
//
//        //return NullOrEmptyChecker.isNullOrEmpty(organization) ? commonPropertyReader.getPropertyValue("item.organization.default.createAssembly") : organization;
//        return NullOrEmptyChecker.isNullOrEmpty(organization) ? defaultOrganization : organization;
//    }
    /**
     * Created / updated item object is assigned to company
     *
     * @param context User security context
     * @param owner Item Owner
     * @param type Item Type
     * @param name Item Name
     * @param revision Item revision
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     * @description update objects company from owner information
     */
    public void updateObjectCompany(Context context, String owner, String type, String name, String revision) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException, RuntimeException {
        BUSINESS_OBJECT_UTIL_LOGGER.info("Going to update company.");
        try {
            String result = MqlUtil.mqlCommand(context, "expand bus '" + type + "' " + name + " " + revision + " to rel \"Company Product\";");
            if (result.length() > 0) {
                disconnectCurrentCompany(context, type, name, revision);
            }
            connectObjectWithCompany(context, owner, type, name, revision);
        } catch (FrameworkException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (RuntimeException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        }
        BUSINESS_OBJECT_UTIL_LOGGER.info("Company Update Success.");
    }

    /**
     * To disconnect current company with item object
     *
     * @param context
     * @param type
     * @param name
     * @param revision
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     * @description disconnect current company
     */
    private static void disconnectCurrentCompany(Context context, String type, String name, String revision) {
        try {
            String mqlQuery = "expand bus '" + type + "' " + name + " " + revision + " to relationship \"Company Product\" recurse to 1 select relationship id dump :;";
            String queryResult = MqlUtil.mqlCommand(context, mqlQuery);
            // 1:Company Product:from:Company:Company Name:-:42848.60610.40457.36289
            mqlQuery = "disconnect connection " + queryResult.split(":")[6];
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Disconnecting Company");
            queryResult = MqlUtil.mqlCommand(context, mqlQuery);
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Disconnected!");
        } catch (FrameworkException ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(ex);
            throw new RuntimeException("Unable to update company.");
        }
    }

    /**
     * Created / updated item object is assigned to company
     *
     * @param context
     * @param owner
     * @param type
     * @param name
     * @param revision
     * @throws RuntimeException
     * @description disconnect current company
     */
    public static void connectObjectWithCompany(Context context, String owner, String type, String name, String revision) throws RuntimeException {
        try {
            String command = "expand bus Person '" + owner + "' -  to rel Employee dump :";
            String queryResult = MqlUtil.mqlCommand(context, command);
            if (queryResult.length() > 0) {
                // 1|Employee|from|Company|Company Name
                String companyName = queryResult.split(":")[4];
                command = "temp query bus * * * where \"name matchlist '" + companyName + "' ','\" select id dump :";
                queryResult = MqlUtil.mqlCommand(context, command);
                // Company,Company Name,-,42848.60610.2976.44525
                BUSINESS_OBJECT_UTIL_LOGGER.debug(queryResult);
                String companyObjectId = queryResult.split(":")[3];

                command = "temp query bus '" + type + "' " + name + " " + revision + " select id dump :";
                queryResult = MqlUtil.mqlCommand(context, command);
                // Hardware Product:HP-0004330:A:42848.60610.61407.38766
                BUSINESS_OBJECT_UTIL_LOGGER.debug(queryResult);
                String objectId = queryResult.split(":")[3];

                command = "connect bus " + objectId + " rel \"Company Product\" from " + companyObjectId;
                BUSINESS_OBJECT_UTIL_LOGGER.debug("Input: " + command);
                queryResult = MqlUtil.mqlCommand(context, command);
                BUSINESS_OBJECT_UTIL_LOGGER.debug("Query Result: " + queryResult);
            } else {
                throw new RuntimeException("Error! '" + owner + "' doesn't have company assigned.");
            }
        } catch (FrameworkException ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(ex);
            throw new RuntimeException("Unable to update company.");
        }
    }

    /**
     * Get Person Organization
     *
     * @param context
     * @param collaborationSpace
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     * @throws FrameworkException
     */
    public String getPersonOrganization(Context context, String collaborationSpace) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException, FrameworkException {
        try {
            String organization = "";
            String personName = context.getUser();
            CommonPropertyReader commonPropertyReader = new CommonPropertyReader();

            //CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
            String defaultOrganizationElementPath = commonPropertyReader.getPropertyValue("item.import.organization.default");

            XmlParse xmlParse = new XmlParse();
            String defaultOrganization = xmlParse.getAttributeValue(defaultOrganizationElementPath, "defaultOrganization");
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Default Organization is: " + defaultOrganization);

            String command = "print person " + personName + " select ASSIGNMENT dump @;";
            BUSINESS_OBJECT_UTIL_LOGGER.debug("MQL role fetching query : " + command);
            String queryResult = MqlUtil.mqlCommand(context, command);

            BUSINESS_OBJECT_UTIL_LOGGER.debug("Roles found for '" + personName + "' are '" + queryResult + "'");

            if (queryResult.length() > 0) {
                HashMap<String, List<String>> checkRole = filterRole(Arrays.asList(queryResult.split("@")));
                List<String> ctxRoleList = checkRole.get("ctxRole");
                List<String> generalRoleList = checkRole.get("generalRole");

                for (int iterator = 0; iterator < ctxRoleList.size(); iterator++) {
                    String eachRole = ctxRoleList.get(iterator);
//                    BUSINESS_OBJECT_UTIL_LOGGER.debug("Each Role : '" + eachRole + "'");
                    String[] roleParts = eachRole.split("\\.");

                    String eachCollaborationSpace = roleParts[2];
//                    BUSINESS_OBJECT_UTIL_LOGGER.debug("Each Collaboration Space : '" + eachCollaborationSpace + "'");

                    if (eachCollaborationSpace.equalsIgnoreCase(collaborationSpace)) {
                        String eachOrganization = roleParts[1];
                        return eachOrganization;
                    }
                }
            }

        } catch (FrameworkException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (RuntimeException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (IOException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        }

        String noOrganization = "No organization has found for this '" + collaborationSpace + "' collaboration space";
        BUSINESS_OBJECT_UTIL_LOGGER.fatal(noOrganization);
        throw new NullPointerException(noOrganization);
    }

    /**
     * Get Organization by Collaboration space
     *
     * @param context
     * @param collaborationSpace
     * @return
     * @throws IOException
     * @throws FrameworkException
     */
    public String getOrganization(Context context, String collaborationSpace) throws IOException, FrameworkException {
        try {
            String personName = context.getUser();

            String command = "print person " + personName + " select ASSIGNMENT dump @;";
            BUSINESS_OBJECT_UTIL_LOGGER.debug("MQL role fetching query : " + command);
            String queryResult = MqlUtil.mqlCommand(context, command);

            BUSINESS_OBJECT_UTIL_LOGGER.debug("Roles found for '" + personName + "' are '" + queryResult + "'");

            if (queryResult.length() > 0) {
                HashMap<String, List<String>> checkRole = filterRole(Arrays.asList(queryResult.split("@")));
                List<String> ctxRoleList = checkRole.get("ctxRole");

                for (int iterator = 0; iterator < ctxRoleList.size(); iterator++) {
                    String eachRole = ctxRoleList.get(iterator);
                    String[] roleParts = eachRole.split("\\.");
                    String eachCollaborationSpace = roleParts[2];
                    if (eachCollaborationSpace.equalsIgnoreCase(collaborationSpace)) {
                        String eachOrganization = roleParts[1];
                        return eachOrganization;
                    }
                }
            }

        } catch (FrameworkException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (NullPointerException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (RuntimeException exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        } catch (Exception exp) {
            BUSINESS_OBJECT_UTIL_LOGGER.fatal(exp);
            throw exp;
        }

        String noOrganization = "No organization has found for this '" + collaborationSpace + "' collaboration space";
        BUSINESS_OBJECT_UTIL_LOGGER.fatal(noOrganization);
        throw new NullPointerException(noOrganization);
    }

    /**
     *
     * @param roleList
     * @return
     */
    private HashMap<String, List<String>> filterRole(List<String> roleList) {
        try {
            List<String> ctxList = new ArrayList<>();
            List<String> generalList = new ArrayList<>();

            roleList.forEach(role -> {
                if (role.contains("ctx::")) {
                    ctxList.add(role);
                } else {
                    generalList.add(role);
                }
            });

            HashMap<String, List<String>> listHashMap = new HashMap<>();
            listHashMap.put("ctxRole", ctxList);
            listHashMap.put("generalRole", generalList);

            BUSINESS_OBJECT_UTIL_LOGGER.debug("CTX Role : " + ctxList);
            BUSINESS_OBJECT_UTIL_LOGGER.debug("General Role : " + generalList);

            return listHashMap;
        } catch (Exception exp) {
            throw exp;
        }
    }

    /**
     *
     * @param context
     * @param bo
     * @return
     * @throws FrameworkException
     */
    public boolean deleteBO(Context context, BusinessObject bo) throws FrameworkException {
        StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append("delete bus ").append(bo.getObjectId());
        String deleteQuery = commandBuilder.toString();
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Delete query : " + deleteQuery);
        MqlUtil.mqlCommand(context, deleteQuery);
        return true;
    }

    /**
     *
     * @param context
     * @param parentBO
     * @param busPatternList
     * @param relPatternList
     * @param busWhereExpression
     * @param relWhereExpression
     * @param expandLevel
     * @return
     * @throws IOException
     */
    public HashMap<String, HashMap> getExistingChildInfoByExpandingParent(Context context, BusinessObject parentBO, ArrayList<String> busPatternList, ArrayList<String> relPatternList, String busWhereExpression, String relWhereExpression, Short expandLevel) throws IOException {
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Expand Parent 1 level ");
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
        HashMap<String, ArrayList<String>> childInfoRelMap = new HashMap<>();
        HashMap<String, String> childInfoIdMap = new HashMap<>();
        try {
            SelectList selectBusStmts = new SelectList();
            selectBusStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision") + "]");

            SelectList selectRelStmts = new SelectList();
            selectRelStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("BOM.Instance.position") + "]");

            String patternDelim = ",";
            Pattern typePattern = (busPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, busPatternList));
            Pattern relPattern = (relPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, relPatternList));

            String busWhereExp = (busWhereExpression == null) ? "" : busWhereExpression;
            String relWhereExp = (relWhereExpression == null) ? "" : relWhereExpression;

            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relItr = null;

            expandResult = parentBO.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false, true,
                    expandLevel, busWhereExp, relWhereExp, false);
            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> relList = new ArrayList<>();
            int childCounter = 0;
            while (relItr.next()) {
                childCounter++;
                RelationshipWithSelect relSelect = relItr.obj();
                String relID = relSelect.getName();
                String relType = relSelect.getTypeName();
                BusinessObjectWithSelect busSelect = relSelect.getTarget();
                String childRev = "";
                try {
                    childRev = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision")).getValue();
                } catch (MatrixException e) {
                }
                String childPosition = "";
                try {
                    childPosition = relSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("BOM.Instance.position")).getValue();
                } catch (MatrixException e) {
                    e.printStackTrace();
                    BUSINESS_OBJECT_UTIL_LOGGER.error(e.getMessage());
                }

                BusinessObject childBO = relSelect.getTo();
                String childName = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.att.external.id")).getValue();
                //childBO.open(context);
                //String childName = childBO.getName();
                String childId = childBO.getObjectId();
                String childInfo = childName + "-" + childRev + "-" + childPosition;
                if (childInfoRelMap.containsKey(childInfo)) {
                    relList = childInfoRelMap.get(childInfo);
                } else {
                    relList = new ArrayList<>();
                }
                if (!childInfoIdMap.containsKey(childInfo)) {
                    childInfoIdMap.put(childInfo, childId);
                }
                relList.add(relID);
                childInfoRelMap.put(childInfo, relList);
                //childBO.close(context);
            }
            existingChildDataCollectorMap.put("child-rel-info", childInfoRelMap);
            existingChildDataCollectorMap.put("child-id-info", childInfoIdMap);
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Total child in first level " + childCounter);

        } catch (MatrixException ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.error(ex.getMessage());
            BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
        }

        return existingChildDataCollectorMap;
    }

//    public HashMap<String, HashMap> getExpandedDataFromParent(Context context, BusinessObject parentBO, ArrayList<String> busPatternList, ArrayList<String> relPatternList, String busWhereExpression, String relWhereExpression, Short expandLevel) throws IOException {
//        BUSINESS_OBJECT_UTIL_LOGGER.debug("Expand Parent 1 level ");
//        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
//        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
//        HashMap<String, ArrayList<String>> childInfoRelMap = new HashMap<>();
//        HashMap<String, String> childInfoIdMap = new HashMap<>();
//        try {
//            SelectList selectBusStmts = new SelectList();
//            //selectBusStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision") + "]");
//
//            SelectList selectRelStmts = new SelectList();
//            selectRelStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("BOM.Instance.position") + "]");
//
//            String patternDelim = ",";
//            Pattern typePattern = (busPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, busPatternList));
//            Pattern relPattern = (relPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, relPatternList));
//
//            String busWhereExp = (busWhereExpression == null) ? "" : busWhereExpression;
//            String relWhereExp = (relWhereExpression == null) ? "" : relWhereExpression;
//
//            ExpansionWithSelect expandResult;
//            RelationshipWithSelectItr relItr = null;
//
//            expandResult = parentBO.expandSelect(
//                    context, relPattern.getPattern(),
//                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
//                    false, true,
//                    expandLevel, busWhereExp, relWhereExp, false);
//            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
//            ArrayList<String> relList = new ArrayList<>();
//            int childCounter = 0;
//            while (relItr.next()) {
//                childCounter++;
//                RelationshipWithSelect relSelect = relItr.obj();
//                String relID = relSelect.getName();
//                String relType = relSelect.getTypeName();
//                BusinessObjectWithSelect busSelect = relSelect.getTarget();
//                String childRev = "";
//                try {
//                    childRev = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision")).getValue();
//                } catch (MatrixException e) {
//                }
//                String childPosition = "";
//                try {
//                    childPosition = relSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("BOM.Instance.position")).getValue();
//                } catch (MatrixException e) {
//                    e.printStackTrace();
//                    BUSINESS_OBJECT_UTIL_LOGGER.error(e.getMessage());
//                }
//
//                BusinessObject childBO = relSelect.getTo();
//                String childName = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.att.external.id")).getValue();
//                //childBO.open(context);
//                //String childName = childBO.getName();
//                String childId = childBO.getObjectId();
//                String childInfo = childName + "-" + childRev + "-" + childPosition;
//                if (childInfoRelMap.containsKey(childInfo)) {
//                    relList = childInfoRelMap.get(childInfo);
//                } else {
//                    relList = new ArrayList<>();
//                }
//                if (!childInfoIdMap.containsKey(childInfo)) {
//                    childInfoIdMap.put(childInfo, childId);
//                }
//                relList.add(relID);
//                childInfoRelMap.put(childInfo, relList);
//                //childBO.close(context);
//            }
//            existingChildDataCollectorMap.put("child-rel-info", childInfoRelMap);
//            existingChildDataCollectorMap.put("child-id-info", childInfoIdMap);
//            BUSINESS_OBJECT_UTIL_LOGGER.debug("Total child in first level " + childCounter);
//
//        } catch (MatrixException ex) {
//            BUSINESS_OBJECT_UTIL_LOGGER.error(ex.getMessage());
//            BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
//        }
//
//        return existingChildDataCollectorMap;
//    }
    public HashMap<String, HashMap> getExpandedDataFromParent(Context context, BusinessObject parentBO, List<String> busPatternList, List<String> relPatternList, String busWhereExpression, String relWhereExpression, Short expandLevel) throws IOException {
        BUSINESS_OBJECT_UTIL_LOGGER.debug("Expand Parent 1 level ");
        CommonPropertyReader commonPropertyReader = new CommonPropertyReader();
        HashMap<String, HashMap> existingChildDataCollectorMap = new HashMap<>();
        HashMap<String, ArrayList<String>> childInfoRelMap = new HashMap<>();
        HashMap<String, String> childInfoIdMap = new HashMap<>();
        try {
            SelectList selectBusStmts = new SelectList();
            //selectBusStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision") + "]");

            SelectList selectRelStmts = new SelectList();
            selectRelStmts.addElement("attribute[" + commonPropertyReader.getPropertyValue("BOM.Instance.position") + "]");

            String patternDelim = ",";
            Pattern typePattern = (busPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, busPatternList));
            Pattern relPattern = (relPatternList == null) ? new Pattern("*") : new Pattern(String.join(patternDelim, relPatternList));

            String busWhereExp = (busWhereExpression == null) ? "" : busWhereExpression;
            String relWhereExp = (relWhereExpression == null) ? "" : relWhereExpression;

            ExpansionWithSelect expandResult;
            RelationshipWithSelectItr relItr = null;

            expandResult = parentBO.expandSelect(
                    context, relPattern.getPattern(),
                    typePattern.getPattern(), selectBusStmts, selectRelStmts,
                    false, true,
                    expandLevel, busWhereExp, relWhereExp, false);
            relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
            ArrayList<String> relList = new ArrayList<>();
            int childCounter = 0;
            while (relItr.next()) {
                childCounter++;
                RelationshipWithSelect relSelect = relItr.obj();
                String relID = relSelect.getName();
                String relType = relSelect.getTypeName();
                BusinessObjectWithSelect busSelect = relSelect.getTarget();
                String childRev = "";
                try {
                    childRev = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.import.enovia.pdm.attribute.revision")).getValue();
                } catch (MatrixException e) {
                }
                String childPosition = "";
                try {
                    childPosition = relSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("BOM.Instance.position")).getValue();
                } catch (MatrixException e) {
                    e.printStackTrace();
                    BUSINESS_OBJECT_UTIL_LOGGER.error(e.getMessage());
                }

                BusinessObject childBO = relSelect.getTo();
                String childName = busSelect.getAttributeValues(context, commonPropertyReader.getPropertyValue("item.att.external.id")).getValue();
                //childBO.open(context);
                //String childName = childBO.getName();
                String childId = childBO.getObjectId();
                String childInfo = childName + "-" + childRev + "-" + childPosition;
                if (childInfoRelMap.containsKey(childInfo)) {
                    relList = childInfoRelMap.get(childInfo);
                } else {
                    relList = new ArrayList<>();
                }
                if (!childInfoIdMap.containsKey(childInfo)) {
                    childInfoIdMap.put(childInfo, childId);
                }
                relList.add(relID);
                childInfoRelMap.put(childInfo, relList);
                //childBO.close(context);
            }
            existingChildDataCollectorMap.put("child-rel-info", childInfoRelMap);
            existingChildDataCollectorMap.put("child-id-info", childInfoIdMap);
            BUSINESS_OBJECT_UTIL_LOGGER.debug("Total child in first level " + childCounter);

        } catch (MatrixException ex) {
            BUSINESS_OBJECT_UTIL_LOGGER.error(ex.getMessage());
            BUSINESS_OBJECT_UTIL_LOGGER.error(ex);
        }

        return existingChildDataCollectorMap;
    }

    /**
     *
     * @param context
     * @param bo
     * @return
     * @throws MatrixException
     */
    public ArrayList<String> getItemParents(Context context, BusinessObject bo) throws MatrixException {
        ArrayList<String> parentList = new ArrayList<>();
        RelationshipList relList = bo.getToRelationship(context);
        for (int i = 0; i < relList.size(); i++) {
            BusinessObject parentBO = relList.get(i).getFrom();
            parentBO.open(context);
            parentList.add(parentBO.getName() + " " + parentBO.getRevision());
            parentBO.close(context);
        }
        return parentList;
    }

    /**
     *
     * @param context
     * @param bo
     * @return
     * @throws MatrixException
     */
    public ArrayList<String> getItemChilds(Context context, BusinessObject bo) throws MatrixException {
        ArrayList<String> childList = new ArrayList<>();
        RelationshipList relList = bo.getFromRelationship(context);
        for (int i = 0; i < relList.size(); i++) {
            BusinessObject childBO = relList.get(i).getTo();
            childBO.open(context);
            childList.add(childBO.getName() + " " + childBO.getRevision());
            childBO.close(context);
        }
        return childList;
    }

    /**
     * This method checks item state with target state
     *
     * @param context
     * @param businessObject
     * @param targetState
     * @return
     * @throws MatrixException
     * @throws Exception
     */
    public boolean itemHasTargetState(Context context, BusinessObject businessObject, String targetState)
            throws MatrixException, Exception {
        if (!context.checkContext()) {
            throw new Exception("Connection is closed!");
        } else if (businessObject == null) {
            throw new Exception("Invalid Item!");
        } else if (targetState == null || targetState.isEmpty()) {
            throw new Exception("State cannot be empty!");
        }
        boolean isStateFound = false;
        boolean hasTargetState = false;
        for (State state : businessObject.getStates(context)) {
            if (state.getName().equals(targetState)) {
                isStateFound = true;
                if (state.isEnabled() && state.isCurrent()) {
                    hasTargetState = true;
                }
                break;
            }
        }
        if (!isStateFound) {
            throw new Exception("Invalid State '" + targetState + "'");
        }
        return hasTargetState;
    }

}
