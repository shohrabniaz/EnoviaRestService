/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.expand.ExpandObject;
import com.bjit.mapper.mapproject.expand.ObjectTypesAndRelations;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.util.MatrixException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class DrawingUtil {

    private ExpandObject expandObject;
    //   private ObjectTypesAndRelations typesAndRelations;
    private List<String> processCnxTypeListForDrawing = Arrays.asList(PropertyReader.getProperty("item.types.list.for.process.cnx").split(","));
    BusinessObjectWithSelect busWithSel;

    public HashMap fetchDrawingNumber(Context context, BusinessObjectWithSelect busWithSel, String docType, String[] attributeArr, ObjectTypesAndRelations typesAndRelations) throws MatrixException, IOException {

        this.busWithSel = busWithSel;
        expandObject = new ExpandObject();
        //   typesAndRelations = new ObjectTypesAndRelations();
        String physicalId = busWithSel.getSelectData("physicalid");
        String docId = "";
        String drawingId = "";
        String drawingNumber = "";
        String drawingRev = "";
        String distributionList = "";

        HashMap<String, String> drawingNumberList = new HashMap<>();
        String drawingNumberForPDM = busWithSel.getSelectData(PropertyReader.getProperty("PDM.drawing.attribute"));
        if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumberForPDM)) {
            drawingNumber = drawingNumberForPDM;
        }
        drawingNumberList.put("Drawing Number", drawingNumber);
        drawingNumberList.put("Document Links", "");

        if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
            HashMap<String, String> drawingPhysicalIds = new LinkedHashMap<String, String>();
            StringBuilder docIdBuilder = new StringBuilder();
            String physicalID_VPMReference = "";
            String physicalID_Document = "";
            if (busWithSel.getSelectData("type").equals("VPMReference")) {
                physicalID_VPMReference = physicalId;
            } else {
                BusinessObjectWithSelectList processImplementCnx = expandObject.getChildObjectFromExpansion(context, new BusinessObject(busWithSel.getObjectId()), processCnxTypeListForDrawing, PropertyReader.getProperty("rel.item.to.process.cnx"), typesAndRelations);
                if (NullOrEmptyChecker.isNull(processImplementCnx)) {
                    drawingNumber = fetchLegacyDrawingNumber(busWithSel);
                    if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                        drawingNumberList.put("Drawing Number", drawingNumber);
                        drawingNumberList.put("DistributionList", distributionList);
                    }
                    return drawingNumberList;
                }

                for (BusinessObjectWithSelect drawingBusWithSelect : processImplementCnx) {
                    if (drawingBusWithSelect.getSelectData("type").equals("DELFmiProcessImplementCnx")) {
                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
                        MqlQueries mqlQuery = new MqlQueries();
                        physicalID_VPMReference = mqlQuery.getPhysicalIdFromProcessImpl(context, processImplementCnxID);
                    }
                    if (drawingBusWithSelect.getSelectData("type").equals("PLMDocConnection")) {
                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
                        MqlQueries mqlQuery = new MqlQueries();
                        physicalID_Document = mqlQuery.getPhysicalIdFromDocumentrocessImpl(context, processImplementCnxID, docType);
                        if (!NullOrEmptyChecker.isNullOrEmpty(physicalID_Document)) {
                            docIdBuilder = docIdBuilder.append(physicalID_Document).append(",");
                        }
                    }
                }
            }
            if (NullOrEmptyChecker.isNullOrEmpty(physicalID_VPMReference)) {
                drawingNumber = fetchLegacyDrawingNumber(busWithSel);
                if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                    drawingNumberList.put("Drawing Number", drawingNumber);
                    drawingNumberList.put("DistributionList", distributionList);
                }
                return drawingNumberList;
            }
            BusinessObject VPMReferenceBO = new BusinessObject(physicalID_VPMReference);
            if (NullOrEmptyChecker.isNull(VPMReferenceBO)) {
                drawingNumber = fetchLegacyDrawingNumber(busWithSel);
                if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                    drawingNumberList.put("Drawing Number", drawingNumber);
                    drawingNumberList.put("DistributionList", distributionList);
                }
                return drawingNumberList;
            }
            BusinessObjectWithSelectList drawingList = expandObject.getDrawingObjectsFromExpansion(context, VPMReferenceBO, PropertyReader.getProperty("item.type.drawing"), PropertyReader.getProperty("rel.vpmreference.to.drawing"), typesAndRelations);
            if (drawingList.isEmpty() || NullOrEmptyChecker.isNullOrEmpty(docType)) {
                drawingNumber = fetchLegacyDrawingNumber(busWithSel);
                if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                    drawingNumberList.put("Drawing Number", drawingNumber);
                    drawingNumberList.put("DistributionList", distributionList);
                }
                return drawingNumberList;
            }
            int drawingCount = 1;
            if (!NullOrEmptyChecker.isNullOrEmpty(docType)) {
//                String[] attributeArr = docType.split(",");
                if (attributeArr.length == 1) {
                    for (BusinessObjectWithSelect drawingBusWithSelect : drawingList) {
                        if (drawingBusWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals(attributeArr[0])) {
                            if (NullOrEmptyChecker.isNullOrEmpty(docId)) {
                                docId = drawingBusWithSelect.getSelectData("attribute[XP_VPMRepReference_Ext.E_XParamStr16A]");
                                if (NullOrEmptyChecker.isNullOrEmpty(docId)) {

                                    drawingId = drawingBusWithSelect.getSelectData("name");
                                    drawingRev = drawingBusWithSelect.getSelectData("revision");
                                    docId = drawingId + "_" + drawingRev;
                                }

                                docIdBuilder = docIdBuilder.append(drawingBusWithSelect.getSelectData("physicalid"));
                            } else {
                                String drawingKey = "PhysicalId" + drawingCount;
                                drawingPhysicalIds.put(drawingKey, drawingBusWithSelect.getSelectData("physicalid"));
                                drawingCount++;
                                docIdBuilder = docIdBuilder.append(",").append(drawingBusWithSelect.getSelectData("physicalid"));
                            }
                            String drawingNo = docId;
                            if (!NullOrEmptyChecker.isNullOrEmpty(drawingNo)) {
                                drawingNumber = drawingNo;
                            }
                            distributionList = drawingBusWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]");
                        }
                    }
                } else {
                    //VSIX-5394 Production type drawing is missing while selecting multiple drawing in Himeli report
                    //prioritylist: production, production and customer, customer
//                    JsonOutput jsonObject = new JsonOutput();
//                    attributeArr = jsonObject.getPriorityDrawingTypeList(attributeArr);
                    for (String attributeArrStr : attributeArr) {
                        if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                            for (int i = 0; i < drawingList.size(); i++) {
                                if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                                    BusinessObjectWithSelect busWithSelect = drawingList.get(i);
                                    if (busWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals(attributeArrStr)) {
                                        if (NullOrEmptyChecker.isNullOrEmpty(docId)) {
                                            docId = busWithSelect.getSelectData("attribute[XP_VPMRepReference_Ext.E_XParamStr16A]");
                                            if (NullOrEmptyChecker.isNullOrEmpty(docId)) {
                                                drawingId = busWithSelect.getSelectData("name");
                                                drawingRev = busWithSelect.getSelectData("revision");
                                                docId = drawingId + "_" + drawingRev;
                                            }

                                            docIdBuilder = docIdBuilder.append(busWithSelect.getSelectData("physicalid"));
                                        } else {
                                            String drawingKey = "PhysicalId" + drawingCount;
                                            drawingPhysicalIds.put(drawingKey, busWithSelect.getSelectData("physicalid"));
                                            drawingCount++;
                                            docIdBuilder = docIdBuilder.append(",").append(busWithSelect.getSelectData("physicalid"));
                                        }

                                        String drawingNo = docId;
                                        if (!NullOrEmptyChecker.isNullOrEmpty(drawingNo)) {
                                            drawingNumber = drawingNo;
                                        }
                                        distributionList = busWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                drawingNumber = fetchLegacyDrawingNumber(busWithSel);
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
                drawingNumberList.put("Drawing Number", drawingNumber);
            }
            if (!NullOrEmptyChecker.isNullOrEmpty(docIdBuilder.toString())) {
                drawingNumberList.put("Document Links", docIdBuilder.toString());
            }

            if (!NullOrEmptyChecker.isNullOrEmpty(docIdBuilder.toString())) {
                drawingNumberList.put("DistributionList", distributionList);
            }
            return drawingNumberList;
        }

        return drawingNumberList;

    }

//    public String fetchDistributionList(Context context, BusinessObjectWithSelect busWithSel, ObjectTypesAndRelations typesAndRelations) throws MatrixException {
//
//        this.busWithSel = busWithSel;
//        expandObject = new ExpandObject();
//        String distributionList = "";
//        //   typesAndRelations = new ObjectTypesAndRelations();
//        String physicalId = busWithSel.getSelectData("physicalid");
//        String drawingNumber = "";
//
//        HashMap<String, String> drawingNumberList = new HashMap<>();
//        String drawingNumberForPDM = busWithSel.getSelectData(PropertyReader.getProperty("PDM.drawing.attribute"));
//        if (!NullOrEmptyChecker.isNullOrEmpty(drawingNumberForPDM)) {
//            drawingNumber = drawingNumberForPDM;
//        }
//        drawingNumberList.put("DistributionList", "");
//
//        if (NullOrEmptyChecker.isNullOrEmpty(drawingNumber)) {
//            String physicalID_VPMReference = "";
//            if (busWithSel.getSelectData("type").equals("VPMReference")) {
//                physicalID_VPMReference = physicalId;
//            } else {
//                BusinessObjectWithSelectList processImplementCnx = expandObject.getChildObjectFromExpansion(context, new BusinessObject(busWithSel.getObjectId()), processCnxTypeListForDrawing, PropertyReader.getProperty("rel.item.to.process.cnx"), typesAndRelations);
//                if (NullOrEmptyChecker.isNull(processImplementCnx)) {
//                    drawingNumberList.put("DistributionList", "");
//                    return distributionList;
//                }
//
//                for (BusinessObjectWithSelect drawingBusWithSelect : processImplementCnx) {
//                    if (drawingBusWithSelect.getSelectData("type").equals("DELFmiProcessImplementCnx")) {
//                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
//                        MqlQueries mqlQuery = new MqlQueries();
//                        physicalID_VPMReference = mqlQuery.getPhysicalIdFromProcessImpl(context, processImplementCnxID);
//                    }
//                }
//            }
//            if (NullOrEmptyChecker.isNullOrEmpty(physicalID_VPMReference)) {
//                drawingNumberList.put("DistributionList", "");
//                return distributionList;
//            }
//            BusinessObject VPMReferenceBO = new BusinessObject(physicalID_VPMReference);
//            if (NullOrEmptyChecker.isNull(VPMReferenceBO)) {
//                drawingNumberList.put("DistributionList", "");
//                return distributionList;
//            }
//            BusinessObjectWithSelectList drawingList = expandObject.getDrawingObjectsFromExpansion(context, VPMReferenceBO, PropertyReader.getProperty("item.type.drawing"), PropertyReader.getProperty("rel.vpmreference.to.drawing"), typesAndRelations);
//            if (drawingList.isEmpty()) {
//                drawingNumberList.put("DistributionList", "");
//                return distributionList;
//            }
//
//            // for (BusinessObjectWithSelect drawingBusWithSelect : drawingList) {
//            //  if()
//            // String distributionList = "";
//            if (drawingList.size() > 0) {
//                if (NullOrEmptyChecker.isNullOrEmpty(distributionList)) {
//                    if (drawingList.get(0).getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals("Production") || drawingList.get(0).getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals("ProductionAndCustomer")) {
//                        distributionList = drawingList.get(0).getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]");
//                        // drawingNumberList.put("DistributionList", distributionList);
//                    }
//                }
//            }
//
//            //  }
//            return distributionList;
//        }
//
//        return distributionList;
//
//    }
    public ArrayList fetchDrawingInfo(Context context, BusinessObjectWithSelect busWithSel, String docType, ObjectTypesAndRelations typesAndRelations) throws MatrixException {

        String physicalId = busWithSel.getSelectData("physicalid");
        String drawingId = "";
        ArrayList<HashMap<String, String>> drawingInfo = new ArrayList<>();
        String drawingNumberForPDM = busWithSel.getSelectData(PropertyReader.getProperty("PDM.drawing.attribute"));
        if (NullOrEmptyChecker.isNullOrEmpty(drawingNumberForPDM)) {
            HashMap<String, String> drawingPhysicalIds = new LinkedHashMap<String, String>();
            StringBuilder docIdBuilder = new StringBuilder();
            String physicalID_VPMReference = "";
            String physicalID_Document = "";
            if (busWithSel.getSelectData("type").equals("VPMReference")) {
                physicalID_VPMReference = physicalId;
            } else {
                BusinessObjectWithSelectList processImplementCnx = expandObject.getChildObjectFromExpansion(context, new BusinessObject(busWithSel.getObjectId()), processCnxTypeListForDrawing, PropertyReader.getProperty("rel.item.to.process.cnx"), typesAndRelations);
                if (NullOrEmptyChecker.isNull(processImplementCnx)) {
                    return drawingInfo;
                }

                for (BusinessObjectWithSelect drawingBusWithSelect : processImplementCnx) {
                    if (drawingBusWithSelect.getSelectData("type").equals("DELFmiProcessImplementCnx")) {
                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
                        MqlQueries mqlQuery = new MqlQueries();
                        physicalID_VPMReference = mqlQuery.getPhysicalIdFromProcessImpl(context, processImplementCnxID);
                    }
                    if (drawingBusWithSelect.getSelectData("type").equals("PLMDocConnection")) {
                        String processImplementCnxID = drawingBusWithSelect.getObjectId();
                        MqlQueries mqlQuery = new MqlQueries();
                        physicalID_Document = mqlQuery.getPhysicalIdFromDocumentrocessImpl(context, processImplementCnxID, docType);
                        if (!NullOrEmptyChecker.isNullOrEmpty(physicalID_Document)) {
                            docIdBuilder = docIdBuilder.append(physicalID_Document).append(",");
                        }
                    }
                }

            }
            if (NullOrEmptyChecker.isNullOrEmpty(physicalID_VPMReference)) {
                return drawingInfo;
            }
            BusinessObject VPMReferenceBO = new BusinessObject(physicalID_VPMReference);
            if (NullOrEmptyChecker.isNull(VPMReferenceBO)) {
                return drawingInfo;
            }
            BusinessObjectWithSelectList drawingList = expandObject.getDrawingObjectsFromExpansion(context, VPMReferenceBO, PropertyReader.getProperty("item.type.drawing"), PropertyReader.getProperty("rel.vpmreference.to.drawing"), typesAndRelations);
            if (drawingList.isEmpty() || NullOrEmptyChecker.isNullOrEmpty(docType)) {
                return drawingInfo;
            }
            int drawingCount = 1;
            if (!NullOrEmptyChecker.isNullOrEmpty(docType)) {
                String[] attributeArr = docType.split(",");
                if (attributeArr.length == 1) {

                    for (BusinessObjectWithSelect drawingBusWithSelect : drawingList) {
                        if (drawingBusWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals(attributeArr[0])) {
                            HashMap<String, String> drawingData = new HashMap<>();
                            if (NullOrEmptyChecker.isNullOrEmpty(drawingId)) {
                                drawingId = drawingBusWithSelect.getSelectData("name");
                                drawingData.put("name", drawingId);
                                drawingData.put("docId", drawingBusWithSelect.getSelectData(PropertyReader.getProperty("doc.id.from.drawing")));
                                drawingData.put("physicalId", drawingBusWithSelect.getSelectData("physicalid"));
                                drawingInfo.add(drawingData);
                            } else {
                                String drawingKey = "PhysicalId" + drawingCount;
                                drawingPhysicalIds.put(drawingKey, drawingBusWithSelect.getSelectData("physicalid"));
                                drawingCount++;
                                drawingData.put("name", drawingBusWithSelect.getSelectData("name"));
                                drawingData.put("docId", drawingBusWithSelect.getSelectData(PropertyReader.getProperty("doc.id.from.drawing")));
                                drawingData.put("physicalId", drawingBusWithSelect.getSelectData("physicalid"));
                                drawingInfo.add(drawingData);
                            }

                        }
                    }
                } else {

                    for (BusinessObjectWithSelect drawingBusWithSelect : drawingList) {
                        for (int i = 0; i < attributeArr.length; i++) {
                            if (drawingBusWithSelect.getSelectData("attribute[DOC_DocDistributionList.DOC_DocDistributionList]").equals(attributeArr[i])) {
                                HashMap<String, String> drawingData = new HashMap<>();
                                if (NullOrEmptyChecker.isNullOrEmpty(drawingId)) {
                                    drawingId = drawingBusWithSelect.getSelectData("name");
                                    drawingData.put("name", drawingId);
                                    drawingData.put("docId", drawingBusWithSelect.getSelectData(PropertyReader.getProperty("doc.id.from.drawing")));
                                    drawingData.put("physicalId", drawingBusWithSelect.getSelectData("physicalid"));
                                    drawingInfo.add(drawingData);
                                } else {
                                    String drawingKey = "PhysicalId" + drawingCount;
                                    drawingPhysicalIds.put(drawingKey, drawingBusWithSelect.getSelectData("physicalid"));
                                    drawingCount++;
                                    drawingData.put("name", drawingBusWithSelect.getSelectData("name"));
                                    drawingData.put("docId", drawingBusWithSelect.getSelectData(PropertyReader.getProperty("doc.id.from.drawing")));
                                    drawingData.put("physicalId", drawingBusWithSelect.getSelectData("physicalid"));
                                    drawingInfo.add(drawingData);
                                }

                            }
                        }
                    }
                }

            }

            return drawingInfo;
        }

        return drawingInfo;

    }

    public String fetchLegacyDrawingNumber(BusinessObjectWithSelect busWithSel) {
        String drawingNumber = "";
        if (!NullOrEmptyChecker.isNullOrEmpty(busWithSel.getSelectData(PropertyReader.getProperty("legacy.drawing.attribute")))) {
            drawingNumber = busWithSel.getSelectData(PropertyReader.getProperty("legacy.drawing.attribute"));
        }
        return drawingNumber;
    }

}