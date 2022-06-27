/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.deleteItem;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.model.itemImport.DataTree;
import com.bjit.common.rest.app.service.model.itemImport.ObjectDataBean;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.XmlParse;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ResponseMessageFormaterBean;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.ExpansionWithSelect;
import matrix.db.RelationshipType;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectItr;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

/**
 *
 * @author Sajjad
 */
@Controller
@RequestMapping(path = "/deleteItem")
public class DeleteItemController {

    private static final org.apache.log4j.Logger DELETE_ITEM_CONTROLLER_LOGGER = org.apache.log4j.Logger.getLogger(DeleteItemController.class);

    @RequestMapping(method = RequestMethod.POST, value = "/", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity deletePDMItem(HttpServletRequest httpRequest, @RequestBody final ObjectDataBean objectDataBeanList) {
        IResponse responseBuilder = new CustomResponseBuilder();
        final Context context = (Context) httpRequest.getAttribute("context");
        List<ResponseMessageFormaterBean> successfullItemList = new ArrayList<>();
        List<ResponseMessageFormaterBean> errorItemList = new ArrayList<>();
        String buildResponse;
        CommonPropertyReader commonPropertyReader;
        try {
            commonPropertyReader = new CommonPropertyReader();
        } catch (IOException ex) {
            DELETE_ITEM_CONTROLLER_LOGGER.info(ex.getMessage());
            buildResponse = responseBuilder.addErrorMessage("System File Missing!!").setStatus(Status.FAILED).buildResponse();
            return new ResponseEntity<>(buildResponse, HttpStatus.OK);
        }
        ObjectDataBean objectDataBean = (ObjectDataBean) objectDataBeanList;
        List<DataTree> dataTreeList = objectDataBean.geDataTree();

        for (DataTree dataTree : dataTreeList) {
            boolean isSuccessful = true;
            ResponseMessageFormaterBean responseMessageFormaterBean = new ResponseMessageFormaterBean();
            CreateObjectBean createObjectBean = dataTree.getItem();
            createObjectBean.setSource(objectDataBean.getSource());
            String type = createObjectBean.getTnr().getType();
            String name = createObjectBean.getTnr().getName();
            String PDMrev = createObjectBean.getTnr().getRevision();
            System.out.println(type + name + PDMrev);
            String enoviaItemType = "";
            try {

                XmlParse xmlParse = new XmlParse();
                enoviaItemType = xmlParse.getPredefinedValue(commonPropertyReader.getPropertyValue("tag.type.mappings"),
                        commonPropertyReader.getPropertyValue("attribute.src.pdm"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        commonPropertyReader.getPropertyValue("attribute.discriminator.objectType"),
                        type);
                if (NullOrEmptyChecker.isNullOrEmpty(enoviaItemType)) {
                    throw new Exception(MessageFormat.format(commonPropertyReader.getPropertyValue("item.type.not.found"), type));
                }
            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
                isSuccessful = false;
                responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                responseMessageFormaterBean.setErrorMessage(ex.getMessage());
                DELETE_ITEM_CONTROLLER_LOGGER.info(ex.getMessage());
            } catch (Exception ex) {
                isSuccessful = false;
                responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                responseMessageFormaterBean.setErrorMessage(ex.getMessage());
                DELETE_ITEM_CONTROLLER_LOGGER.info(ex.getMessage());
            }
            if (isSuccessful) {
                try {
                    BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
                    BusinessObject pdmBusObject = businessObjectUtil.findPDMItem(context, enoviaItemType, name, PDMrev);
                    if (!NullOrEmptyChecker.isNull(pdmBusObject) && !businessObjectUtil.itemHasTargetState(context, pdmBusObject, commonPropertyReader.getPropertyValue("item.status.inwork"))) {
                        isSuccessful = false;
                        responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                        responseMessageFormaterBean.setErrorMessage(commonPropertyReader.getPropertyValue("other.state.item.delete.warning.message"));
                        DELETE_ITEM_CONTROLLER_LOGGER.info(commonPropertyReader.getPropertyValue("other.state.item.delete.warning.message"));
                    } else if (!NullOrEmptyChecker.isNull(pdmBusObject)) {
                        Pattern typePattern = new Pattern("*" + "," + "");
                        Pattern relPattern = new Pattern("*" + "," + "");
                        Short expandLevel = Short.valueOf("1");

                        boolean isToRel = true;
                        boolean isFromRel = false;

                        StringList objSelect = new StringList();
                        StringList relSelect = new StringList();

                        String busWhereExpression = "";
                        String relWhereExpression = "";

                        boolean checkHidden = false;

                        ExpansionWithSelect expandToParent = pdmBusObject.expandSelect(context, relPattern.getPattern(),
                                typePattern.getPattern(), objSelect, relSelect, isToRel, isFromRel,
                                expandLevel, busWhereExpression, relWhereExpression, checkHidden);
                        if (expandToParent.getRelationships().size() > 0) {
                            DELETE_ITEM_CONTROLLER_LOGGER.info("Item usage found in enovia: " + expandToParent.getRelationships().size());
                            isSuccessful = false;
                            responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                            responseMessageFormaterBean.setErrorMessage(commonPropertyReader.getPropertyValue("item.is.used.enovia"));
                        } else {
                            isToRel = false;
                            isFromRel = true;
                            ExpansionWithSelect expandToMbom = pdmBusObject.expandSelect(context, relPattern.getPattern(),
                                    typePattern.getPattern(), objSelect, relSelect, isToRel, isFromRel,
                                    expandLevel, busWhereExpression, relWhereExpression, checkHidden);

                            if (expandToMbom.getRelationships().size() > 0) {
                                DELETE_ITEM_CONTROLLER_LOGGER.info("MBOM found in enovia: " + expandToParent.getRelationships().size());
                                disconnectMBOM(context, businessObjectUtil, expandToMbom);
                            }
                            businessObjectUtil.deleteBO(context, pdmBusObject);
                        }
                    } else {
                        isSuccessful = false;
                        responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                        responseMessageFormaterBean.setErrorMessage(commonPropertyReader.getPropertyValue("item.not.found"));
                    }
                } catch (MatrixException ex) {
                    isSuccessful = false;
                    responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                    responseMessageFormaterBean.setErrorMessage(ex.getMessage());
                    DELETE_ITEM_CONTROLLER_LOGGER.info(ex.getMessage());
                } catch (Exception ex) {
                    isSuccessful = false;
                    responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                    responseMessageFormaterBean.setErrorMessage(ex.getMessage());
                    DELETE_ITEM_CONTROLLER_LOGGER.info(ex.getMessage());
                }
            }
            if (isSuccessful) {
                responseMessageFormaterBean.setTnr(createObjectBean.getTnr());
                successfullItemList.add(responseMessageFormaterBean);
            } else {
                errorItemList.add(responseMessageFormaterBean);
            }
        }
        Boolean hasSuccessfulList = !NullOrEmptyChecker.isNullOrEmpty(successfullItemList);
        Boolean hasErrorList = !NullOrEmptyChecker.isNullOrEmpty(errorItemList);

        if (hasSuccessfulList && !hasErrorList) {
            buildResponse = responseBuilder.setData(successfullItemList).setStatus(Status.OK).buildResponse();
        } else if (!hasSuccessfulList && hasErrorList) {
            buildResponse = responseBuilder.addErrorMessage(errorItemList).setStatus(Status.FAILED).buildResponse();
        } else if (hasSuccessfulList && hasErrorList) {
            buildResponse = responseBuilder.setData(successfullItemList).setStatus(Status.FAILED).addErrorMessage(errorItemList).buildResponse();
        } else {
            buildResponse = responseBuilder.addErrorMessage("Unknown Exception Occured !!").setStatus(Status.FAILED).buildResponse();
        }
        return new ResponseEntity<>(buildResponse, HttpStatus.OK);
    }

    /**
     * Disconnect BOM from expand result
     *
     * @param context
     * @param businessObjectUtil
     * @param expandResult
     * @return
     * @throws MatrixException
     */
    private void disconnectMBOM(Context context, BusinessObjectUtil businessObjectUtil, ExpansionWithSelect expandResult) throws MatrixException {
        DELETE_ITEM_CONTROLLER_LOGGER.info("BOM Found!, disconnecting BOM.");
        RelationshipWithSelectItr relItr = new RelationshipWithSelectItr(expandResult.getRelationships());
        try {
            while (relItr.next()) {
                RelationshipWithSelect rel = relItr.obj();
                String relId = rel.getName();
                businessObjectUtil.disconnectRelationship(context, relId);
            }
            DELETE_ITEM_CONTROLLER_LOGGER.info("BOM Disconnection done.");
        } catch (MatrixException e) {
            DELETE_ITEM_CONTROLLER_LOGGER.info("Failed to disconnect BOM, cause: " + e.getMessage());
            throw e;
        }
    }

}
