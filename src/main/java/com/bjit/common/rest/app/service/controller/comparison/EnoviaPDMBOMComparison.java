/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.comparison;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.dataGatherers.EnoviaPDMStructureFetch;
import com.bjit.common.rest.app.service.dataGatherers.PDMStructureFetch;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.BOMCompareResponse;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.MultilevelEnoviaStructure;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.MultilevelPDMStructure;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.EnoviaItem;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.PDMItem;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.pdm_enovia.bom.comparison.constant.Constant;
import com.bjit.common.rest.pdm_enovia.bom.comparison.conversion.PDMEnoviaV6Conversion;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.ArrayList;
//import com.bjit.compareBOM.MultiLevelBomDataModel.PDMItem;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Tanzil
 */
@RestController
@RequestMapping(path = "/compareBOM")
public class EnoviaPDMBOMComparison {

    private EnoviaItem enoviaRootItem;
    private PDMItem pdmRootItem;
    HashMap<String, Integer> bomIndexTracker;
    private static final Logger BOM_COMPARISON_LOGGER = Logger.getLogger(EnoviaPDMBOMComparison.class);
    BOMCompareResponse bomCompareResponse = new BOMCompareResponse();
    String enoviaRevision = "";
    String pdmRevision = "";

    @ResponseBody
    @PostMapping(value = "/EnoviaPDMBOMComparison", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getEnoviaPDMBOMComparison(HttpServletRequest httpRequest,
            HttpServletResponse response,
            @RequestParam(value = "expandLevel", required = false) String expandLevel,
            @RequestParam(value = "mode", required = false) String compareMode,
            @RequestParam(value = "attrs", required = false) String attributeAsString,
            @RequestParam(value = "drawingType", required = false) String drawingType,
            @RequestBody final TNR tnr
    ) throws InterruptedException, ExecutionException, Exception {
        if (NullOrEmptyChecker.isNullOrEmpty(expandLevel)) {
            expandLevel = "99";
        }
        String level = expandLevel;
        if (NullOrEmptyChecker.isNullOrEmpty(attributeAsString)) {
            attributeAsString = PropertyReader.getProperty("bom.compare.enovia.bom.exprt.attribute.parameter");
        }
        String attributes = attributeAsString;
        if (NullOrEmptyChecker.isNullOrEmpty(drawingType)) {
            drawingType = PropertyReader.getProperty("bom.compare.enovia.bom.exprt.drawing.type.parameter");
        }
        String drwType = drawingType;
        bomCompareResponse.getBOM().clear();
        if (Float.parseFloat(tnr.getRevision()) % 1.0 == 0) {
            enoviaRevision = getRevision(Float.parseFloat(tnr.getRevision()), true);
            pdmRevision = tnr.getRevision();
        } else {
            pdmRevision = getRevision(Float.parseFloat(tnr.getRevision()), false);
            enoviaRevision = tnr.getRevision();
        }

        CreateContext createContext = new CreateContext();
        Context context = null;
        try {
            context = createContext.getAdminContext();
            BusinessObject businessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), "vplm");
            Attribute attributeValues = businessObject.getAttributeValues(context, "MBOM_MBOMPDM.MBOM_Mastership");
            String mastershipAttr = attributeValues.getValue();
            context.close();
            if (!mastershipAttr.equalsIgnoreCase("PDM")) {
                String errorMessage = "PDM service returned null.";
                IResponse responseBuilder = new CustomResponseBuilder();
                String buildResponse = responseBuilder
                        .addErrorMessage(errorMessage)
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE); 
                               
            }
        } catch (MatrixException ex) {
            BOM_COMPARISON_LOGGER.error(ex.getMessage());
        } catch (Exception ex) {
            BOM_COMPARISON_LOGGER.error(ex.getMessage());
        }

        String relation = Constant.DEFAULT_RELATIONSHIP;
        PDMEnoviaV6Conversion pdmEnoviaV6Conversion = new PDMEnoviaV6Conversion();
        CompletableFuture<Object> enoviaServiceFuture = CompletableFuture.supplyAsync(() -> EnoviaPDMStructureFetch.getEnoviaStructure(tnr.getType(), tnr.getName(), enoviaRevision, level, attributes, drwType, true));
        String pdmString = pdmEnoviaV6Conversion.conversion(tnr.getType(), tnr.getName(), pdmRevision, relation, level);
        CompletableFuture<Object> pdmServiceFuture = CompletableFuture.supplyAsync(() -> PDMStructureFetch.getPDMStructure(pdmString));

        CompletableFuture<Object> combinedComparisonFuture = enoviaServiceFuture.thenCombine(pdmServiceFuture, (envRootNode, pdmRootNode) -> {
            if ((pdmRootNode instanceof PDMItem) && (envRootNode instanceof EnoviaItem)) {
                PDMItem pdmRootNodeBean = (PDMItem) pdmRootNode;
                EnoviaItem enoviaRootItemBean = (EnoviaItem) envRootNode;
                MultilevelEnoviaStructure enoviaStructure = new MultilevelEnoviaStructure();
                MultilevelPDMStructure pdmStructure = new MultilevelPDMStructure();
                BOMCompareResponse bomCompareResponse = new BOMCompareResponse();
                enoviaRootItem = enoviaRootItemBean;
                pdmRootItem = pdmRootNodeBean;
                if (!NullOrEmptyChecker.isNullOrEmpty(compareMode)) {
                    if (compareMode.equals("diffonly")) {
                        List<Integer> removalableChildrenIndice = prepareComparisonTreeForDifference(enoviaRootItem, pdmRootNodeBean, true);
                        int childSize = enoviaRootItem.getBomLines().size() > pdmRootNodeBean.getBomLines().size() ? enoviaRootItem.getBomLines().size() : pdmRootNodeBean.getBomLines().size();
                        if (childSize != 0) {
                            // checking difference in child bom
                            if (removalableChildrenIndice.size() < childSize) {  // some child bom has difference
                                removeChildrenFromBOMItem(enoviaRootItem, pdmRootNodeBean, removalableChildrenIndice);
                            } else {    // no difference exist in child bom
                                enoviaRootItem.getBomLines().clear();
                                pdmRootNodeBean.getBomLines().clear();
                            }
                        } else {
                            prepareComparisonTree(enoviaRootItem, pdmRootNodeBean, true);
                        }
                    }
                } else {
                    prepareComparisonTree(enoviaRootItem, pdmRootNodeBean, true);
                }
                enoviaStructure.setStructure(enoviaRootItem);
                pdmStructure.setStructure(pdmRootItem);

                bomCompareResponse.getBOM().add(enoviaStructure);
                bomCompareResponse.getBOM().add(pdmStructure);

                return bomCompareResponse;
            } else {
                String error = "";
                if (!(pdmRootNode instanceof PDMItem)) {
                    error = !NullOrEmptyChecker.isNull(pdmRootNode) ? pdmRootNode.toString() : "PDM service returned null.";
                } else {
                    error = !NullOrEmptyChecker.isNull(envRootNode) ? envRootNode.toString() : "Enovia service returned null.";
                }
                return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
        Object returnedResponse = combinedComparisonFuture.get();
        if (returnedResponse instanceof ResponseEntity) {
            ResponseEntity comparisonResponseEntity = (ResponseEntity) returnedResponse;
            if (comparisonResponseEntity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                IResponse responseBuilder = new CustomResponseBuilder();
                String buildResponse = responseBuilder
                        .addErrorMessage(comparisonResponseEntity.getBody())
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        }

        return new ResponseEntity<>(returnedResponse, HttpStatus.OK);

    }

    private List<Integer> prepareComparisonTreeForDifference(EnoviaItem enoviaItem, PDMItem pdmServiceFuture, boolean isRoot) {
        List<Integer> itemRemoveList = new ArrayList<>();
        try {
            List<EnoviaItem> enoviaChildLines = enoviaItem.getBomLines();
            List<PDMItem> pdmChildLines = pdmServiceFuture.getBomLines();
            int index = 0;
            boolean removableNode = false;
            boolean hasNonRemovableChild = false;
            while (index < enoviaChildLines.size() || index < pdmChildLines.size()) {
                int envPosition = -1;
                int pdmPosition = -1;
                EnoviaItem envChild = null;
                PDMItem pdmChild = null;
                if (index < enoviaChildLines.size()) {
                    envChild = enoviaItem.getBomLines().get(index);
                    String envPos = NullOrEmptyChecker.isNullOrEmpty(envChild.getPosition()) ? "0" : envChild.getPosition();
                    envPosition = Integer.parseInt(envPos);
                }
                if (index < pdmChildLines.size()) {
                    pdmChild = pdmServiceFuture.getBomLines().get(index);
                    String pdmPos = NullOrEmptyChecker.isNullOrEmpty(pdmChild.getPosition()) ? "0" : pdmChild.getPosition();
                    pdmPosition = Integer.parseInt(pdmPos);
                }
                //------------------------------------------------------------------------------------

                if (pdmPosition == -1 && envPosition != -1) {    // PDM Null ENV not Null
                    pdmChild = new PDMItem("" + envPosition);
                    pdmServiceFuture.getBomLines().add(index, pdmChild);
                    removableNode = false;
                } else if (envPosition == -1 && pdmPosition != -1) {   // ENV Null PDM not Null
                    envChild = new EnoviaItem("" + pdmPosition);
                    enoviaItem.getBomLines().add(index, envChild);
                    removableNode = false;
                } else {  // PDM and ENV both not Null
                    if (pdmPosition != envPosition) {    // PDM and ENV different position
                        BOM_COMPARISON_LOGGER.info("PDM and Enovia not null. ENV: " + envPosition + " PDM: " + pdmPosition);
                        if (pdmPosition < envPosition) {
                            envChild = new EnoviaItem("" + pdmPosition);
                            enoviaItem.getBomLines().add(index, envChild);
                        } else if (pdmPosition > envPosition) {
                            pdmChild = new PDMItem("" + envPosition);
                            pdmServiceFuture.getBomLines().add(index, pdmChild);
                        }
                        removableNode = false;
                    } else {  // PDM and ENV same position; Do nothing
                        if ((!envChild.getType().equals("dummy_type") && !envChild.getName().equals("dummy_obj"))
                                || (!pdmChild.getType().equals("dummy_type") && !pdmChild.getName().equals("dummy_obj"))) {
                            String envName = "";
                            String pdmName = "";
                            float envQty = -1f;
                            float pdmQty = -1f;
                            if (!NullOrEmptyChecker.isNullOrEmpty(envChild.getName())) {
                                envName = envChild.getName();
                            }
                            if (!NullOrEmptyChecker.isNullOrEmpty(envChild.getQty())) {
                                try {
                                    envQty = Float.parseFloat(envChild.getQty());
                                } catch (NumberFormatException e) {
                                    BOM_COMPARISON_LOGGER.error("Error parsing Enovia " + envName + " Qty " + envQty + " : " + e.getMessage());
                                }
                            }
                            if (!NullOrEmptyChecker.isNullOrEmpty(pdmChild.getName())) {
                                pdmName = pdmChild.getName();
                            }
                            if (!NullOrEmptyChecker.isNullOrEmpty(pdmChild.getQty())) {
                                try {
                                    pdmQty = Float.parseFloat(pdmChild.getQty());
                                } catch (NumberFormatException e) {
                                    BOM_COMPARISON_LOGGER.error("Error parsing PDM " + pdmName + " Qty " + pdmQty + " : " + e.getMessage());
                                }
                            }
                            if ((envName.equalsIgnoreCase(pdmName)) && (envQty == pdmQty)) {
                                removableNode = true;
                            }
                        }
                    }
                }
                List<Integer> removalableChildrenIndice = prepareComparisonTreeForDifference(envChild, pdmChild, false);
                int childSize = envChild.getBomLines().size() > pdmChild.getBomLines().size() ? envChild.getBomLines().size() : pdmChild.getBomLines().size();
                if (childSize != 0) {
                    // checking difference in child bom
                    if (removalableChildrenIndice.size() < childSize) {  // some child bom has difference
                        hasNonRemovableChild = true;
                        removableNode = false;
                        removeChildrenFromBOMItem(envChild, pdmChild, removalableChildrenIndice);
                    } else {    // no difference exist in child bom
                        envChild.getBomLines().clear();
                        pdmChild.getBomLines().clear();
                    }
                }
                if (removableNode && !hasNonRemovableChild) { // Item has no difference and child bom has no difference
                    itemRemoveList.add(index);
                }
                index++;
            }
            if (isRoot) {
                enoviaRootItem = enoviaItem;
                pdmRootItem = pdmServiceFuture;
            }
            return itemRemoveList;
        } catch (Exception e) {
            BOM_COMPARISON_LOGGER.error("Error:::--> ", e);
            return itemRemoveList;
        }
    }

    private void removeChildrenFromBOMItem(EnoviaItem enoviaItem, PDMItem pdmItem, List<Integer> removableChildrenIndice) {
        BOM_COMPARISON_LOGGER.info("Removing removable children");
        List<EnoviaItem> removableEnoviaChildren = new ArrayList<>();
        List<PDMItem> removableLNChildren = new ArrayList<>();
        for (Integer childIndex : removableChildrenIndice) {
            removableEnoviaChildren.add(enoviaItem.getBomLines().get(childIndex));
            removableLNChildren.add(pdmItem.getBomLines().get(childIndex));
        }
        for (int i = 0; i < removableChildrenIndice.size(); i++) {
            enoviaItem.getBomLines().remove(removableEnoviaChildren.get(i));
            pdmItem.getBomLines().remove(removableLNChildren.get(i));
        }
    }

    private void prepareComparisonTree(EnoviaItem enoviaItem, PDMItem pdmItem, boolean isRoot) {
        try {
            List<EnoviaItem> enoviaChildLines = enoviaItem.getBomLines();
            List<PDMItem> pdmChildLines = pdmItem.getBomLines();
            int childIndex = 0;

            while (childIndex < enoviaChildLines.size() || childIndex < pdmChildLines.size()) {
                int envPosition = -1;
                int pdmPosition = -1;
                EnoviaItem envChild = null;
                PDMItem pdmChild = null;
                if (childIndex < enoviaChildLines.size()) {
                    envChild = enoviaItem.getBomLines().get(childIndex);
                    String envPos = NullOrEmptyChecker.isNullOrEmpty(envChild.getPosition()) ? "0" : envChild.getPosition();
                    envPosition = Integer.parseInt(envPos);
                }

                if (childIndex < pdmChildLines.size()) {
                    pdmChild = pdmItem.getBomLines().get(childIndex);
                    String pdmPos = NullOrEmptyChecker.isNullOrEmpty(pdmChild.getPosition()) ? "0" : pdmChild.getPosition();
                    pdmPosition = Integer.parseInt(pdmPos);
                }

                //------------------------------------------------------------------------------------
                // PDM Null ENV not Null
                if (pdmPosition == -1 && envPosition != -1) {
                    pdmChild = new PDMItem("" + envPosition);
                    pdmItem.getBomLines().add(childIndex, pdmChild);
                } // ENV Null PDM not Null
                else if (envPosition == -1 && pdmPosition != -1) {
                    envChild = new EnoviaItem("" + pdmPosition);
                    enoviaItem.getBomLines().add(childIndex, envChild);
                } // PDM and ENV both not Null
                else {
                    // PDM and ENV different position
                    if (pdmPosition != envPosition) {
                        BOM_COMPARISON_LOGGER.info("PDM and Enovia not null. ENV: " + envPosition + " LN: " + pdmPosition);
                        if (pdmPosition < envPosition) {
                            envChild = new EnoviaItem("" + pdmPosition);
                            enoviaItem.getBomLines().add(childIndex, envChild);
                        } else if (pdmPosition > envPosition) {
                            pdmChild = new PDMItem("" + envPosition);
                            pdmItem.getBomLines().add(childIndex, pdmChild);
                        }
                    } // PDM and ENV same position; Do nothing
                }
                prepareComparisonTree(envChild, pdmChild, false);
                childIndex++;
            }
            if (isRoot) {
                enoviaRootItem = enoviaItem;
                pdmRootItem = pdmItem;
            }
        } catch (Exception e) {
            BOM_COMPARISON_LOGGER.error(e.getCause());
        }
    }

    private String getRevision(Float rev, boolean pdm) {
        if (pdm) {
            Float revision = rev + (float) 1.1;
            return String.valueOf(revision);
        } else {
            int revision = (int) Math.round(rev) / 1 - 1;
            return "0" + String.valueOf(revision);
        }
    }

}
