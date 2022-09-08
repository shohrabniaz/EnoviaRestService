package com.bjit.common.rest.app.service.controller.comparison;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.dataGatherers.EnoviaStructureFetch;
import com.bjit.common.rest.app.service.dataGatherers.LNStructureFetch;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.BOMCompareResponse;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.EnoviaItem;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.MultilevelEnoviaStructure;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.MultilevelLNStructure;
//import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.compareBOM.MultiLevelBomDataModel.BomLineBean;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.ex.integration.model.webservice.TNR;
import com.bjit.ex.integration.transfer.actions.utilities.BusinessObjectUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Tahmid
 */
@RestController
@RequestMapping(path = "/compareBOM")
public class EnoviaLNBOMComparison {

    //  EnoviaStructureFetch enoviaStructureFetchService;
    private EnoviaItem enoviaRootItem;
    private BomLineBean lnRootItem;
    HashMap<String, Integer> bomIndexTracker;
    private static final Logger BOM_COMPARISON_LOGGER = Logger.getLogger(EnoviaLNBOMComparison.class);

    @ResponseBody
    @PostMapping(value = "/EnoviaLNBOMComparison", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getEnoviaLNBOMComparison(HttpServletRequest httpRequest,
            HttpServletResponse response,
            @RequestParam(value = "expandLevel", required = false) String expandLevel,
            @RequestParam(value = "mode", required = false) String compareMode,
            @RequestParam(value = "attrs", required = false) String attributeAsString,
            @RequestParam(value = "drawingType", required = false) String drawingType,
            @RequestBody final TNR tnr
    ) throws InterruptedException, ExecutionException {
        if (NullOrEmptyChecker.isNullOrEmpty(expandLevel)) {
            expandLevel = PropertyReader.getProperty("bom.compare.default.expand.level");
        }
        String level = expandLevel;
        if (NullOrEmptyChecker.isNullOrEmpty(drawingType)) {
            drawingType = PropertyReader.getProperty("bom.compare.enovia.bom.exprt.drawing.type.parameter");
        }
        String drwType = drawingType;
        if (NullOrEmptyChecker.isNullOrEmpty(attributeAsString)) {
            attributeAsString = PropertyReader.getProperty("bom.compare.enovia.bom.exprt.attribute.parameter");
        }
        String attributes = attributeAsString;
        IResponse responseBuilder = new CustomResponseBuilder();
        CreateContext createContext = new CreateContext();
        Context context = null;
        try {
            context = createContext.getAdminContext();
            BusinessObject businessObject = new BusinessObject(tnr.getType(), tnr.getName(), tnr.getRevision(), "vplm");
            BusinessObject boByTNR = BusinessObjectUtils.getBOByTNR(context, tnr);

            if (!NullOrEmptyChecker.isNull(boByTNR)) {
                Attribute attributeValues = businessObject.getAttributeValues(context, "MBOM_MBOMERP.MBOM_TransferredtoERP");
                String transferredAttr = attributeValues.getValue();
                context.close();
                if (!transferredAttr.equalsIgnoreCase("TRUE")) {
                    String errorMessage = "LN service returned null.";
                    String buildResponse = responseBuilder
                            .addErrorMessage(errorMessage)
                            .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                            .buildResponse();
                    return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                String errorMessage = "Object Not Found in Enovia.";
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

        CompletableFuture<Object> enoviaServiceFuture = CompletableFuture.supplyAsync(() -> EnoviaStructureFetch.getEnoviaStructure(tnr.getType(), tnr.getName(), tnr.getRevision(), level, attributes, drwType));
        CompletableFuture<Object> lnServiceFuture = CompletableFuture.supplyAsync(() -> LNStructureFetch.getLNStructure(tnr.getType(), tnr.getName(), tnr.getRevision(), level));

        CompletableFuture<Object> combinedComparisonFuture = enoviaServiceFuture.thenCombine(lnServiceFuture, (envRootNode, lnRootNode) -> {
            if ((lnRootNode instanceof BomLineBean) && (envRootNode instanceof EnoviaItem)) {
                BomLineBean lnRootNodeBean = (BomLineBean) lnRootNode;
                EnoviaItem enoviaRootItemBean = (EnoviaItem) envRootNode;
                MultilevelEnoviaStructure enoviaStructure = new MultilevelEnoviaStructure();
                MultilevelLNStructure lnStructure = new MultilevelLNStructure();
                BOMCompareResponse bomCompareResponse = new BOMCompareResponse();

                enoviaRootItem = enoviaRootItemBean;

                if (!NullOrEmptyChecker.isNullOrEmpty(compareMode)) {
                    if (compareMode.equals("diffonly")) {
                        List<Integer> removalableChildrenIndice = prepareComparisonTreeForDifference(enoviaRootItem, lnRootNodeBean, true);
                        int childSize = enoviaRootItem.getBomLines().size() > lnRootNodeBean.getBomLines().size() ? enoviaRootItem.getBomLines().size() : lnRootNodeBean.getBomLines().size();
                        if (childSize != 0) {
                            // checking difference in child bom
                            if (removalableChildrenIndice.size() < childSize) {  // some child bom has difference
                                removeChildrenFromBOMItem(enoviaRootItem, lnRootNodeBean, removalableChildrenIndice);
                            } else {    // no difference exist in child bom
                                enoviaRootItem.getBomLines().clear();
                                lnRootNodeBean.getBomLines().clear();
                            }
                        } else {
                            prepareComparisonTree(enoviaRootItem, lnRootNodeBean, true);
                        }
                    }
                } else {
                    prepareComparisonTree(enoviaRootItem, lnRootNodeBean, true);
                }

                //prepareComparisonTree(enoviaRootItemBean, lnRootNodeBean, true);
                enoviaStructure.setStructure(enoviaRootItem);
                lnStructure.setStructure(lnRootItem);

                bomCompareResponse.getBOM().add(enoviaStructure);
                bomCompareResponse.getBOM().add(lnStructure);

                return bomCompareResponse;
            } else {
                String error = "";
                if (!(lnRootNode instanceof BomLineBean)) {
                    error = !NullOrEmptyChecker.isNull(lnRootNode) ? lnRootNode.toString() : "LN service returned null.";
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
                String buildResponse = responseBuilder
                        .addErrorMessage(comparisonResponseEntity.getBody())
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return new ResponseEntity<>(returnedResponse, HttpStatus.OK);
    }

    private void prepareComparisonTree(EnoviaItem enoviaItem, BomLineBean lnItem, boolean isRoot) {
        try {
            List<EnoviaItem> enoviaChildLines = enoviaItem.getBomLines();
            List<BomLineBean> lnChildLines = lnItem.getBomLines();
            int childIndex = 0;

            while (childIndex < enoviaChildLines.size() || childIndex < lnChildLines.size()) {
                int envPosition = -1;
                int lnPosition = -1;
                EnoviaItem envChild = null;
                BomLineBean lnChild = null;
                if (childIndex < enoviaChildLines.size()) {
                    envChild = enoviaItem.getBomLines().get(childIndex);
                    String envPos = NullOrEmptyChecker.isNullOrEmpty(envChild.getPosition()) ? "0" : envChild.getPosition();
                    envPosition = Integer.parseInt(envPos);
                }

                if (childIndex < lnChildLines.size()) {
                    lnChild = lnItem.getBomLines().get(childIndex);
                    String lnPos = NullOrEmptyChecker.isNullOrEmpty(lnChild.getPosition()) ? "0" : lnChild.getPosition();
                    lnPosition = Integer.parseInt(lnPos);
                }

                //------------------------------------------------------------------------------------
                // LN Null ENV not Null
                if (lnPosition == -1 && envPosition != -1) {
                    lnChild = new BomLineBean("" + envPosition);
                    lnItem.getBomLines().add(childIndex, lnChild);
                } // ENV Null LN not Null
                else if (envPosition == -1 && lnPosition != -1) {
                    envChild = new EnoviaItem("" + lnPosition);
                    enoviaItem.getBomLines().add(childIndex, envChild);
                } // LN and ENV both not Null
                else {
                    // LN and ENV different position
                    if (lnPosition != envPosition) {
                        BOM_COMPARISON_LOGGER.info("LN and Enovia not null. ENV: " + envPosition + " LN: " + lnPosition);
                        if (lnPosition < envPosition) {
                            envChild = new EnoviaItem("" + lnPosition);
                            enoviaItem.getBomLines().add(childIndex, envChild);
                        } else if (lnPosition > envPosition) {
                            lnChild = new BomLineBean("" + envPosition);
                            lnItem.getBomLines().add(childIndex, lnChild);
                        }
                    } // LN and ENV same position; Do nothing
                }
                prepareComparisonTree(envChild, lnChild, false);
                childIndex++;
            }
            if (isRoot) {
                enoviaRootItem = enoviaItem;
                lnRootItem = lnItem;
            }
        } catch (Exception e) {
            BOM_COMPARISON_LOGGER.error("Error::---> ", e);
        }
    }

    private List<Integer> prepareComparisonTreeForDifference(EnoviaItem enoviaItem, BomLineBean lnItem, boolean isRoot) {
        List<Integer> itemRemoveList = new ArrayList<>();
        try {
            List<EnoviaItem> enoviaChildLines = enoviaItem.getBomLines();
            List<BomLineBean> lnChildLines = lnItem.getBomLines();
            int index = 0;
            boolean removableNode = false;
            boolean hasNonRemovableChild = false;
            while (index < enoviaChildLines.size() || index < lnChildLines.size()) {
                int envPosition = -1;
                int lnPosition = -1;
                EnoviaItem envChild = null;
                BomLineBean lnChild = null;
                if (index < enoviaChildLines.size()) {
                    envChild = enoviaItem.getBomLines().get(index);
                    String envPos = NullOrEmptyChecker.isNullOrEmpty(envChild.getPosition()) ? "0" : envChild.getPosition();
                    envPosition = Integer.parseInt(envPos);
                }

                if (index < lnChildLines.size()) {
                    lnChild = lnItem.getBomLines().get(index);
                    String lnPos = NullOrEmptyChecker.isNullOrEmpty(lnChild.getPosition()) ? "0" : lnChild.getPosition();
                    lnPosition = Integer.parseInt(lnPos);
                }
                //------------------------------------------------------------------------------------

                if (lnPosition == -1 && envPosition != -1) {    // LN Null ENV not Null
                    lnChild = new BomLineBean("" + envPosition);
                    lnItem.getBomLines().add(index, lnChild);
                    removableNode = false;
                } else if (envPosition == -1 && lnPosition != -1) {   // ENV Null LN not Null
                    envChild = new EnoviaItem("" + lnPosition);
                    enoviaItem.getBomLines().add(index, envChild);
                    removableNode = false;
                } else {  // LN and ENV both not Null
                    if (lnPosition != envPosition) {    // LN and ENV different position
                        BOM_COMPARISON_LOGGER.info("LN and Enovia not null. ENV: " + envPosition + " LN: " + lnPosition);
                        if (lnPosition < envPosition) {
                            envChild = new EnoviaItem("" + lnPosition);
                            enoviaItem.getBomLines().add(index, envChild);
                        } else if (lnPosition > envPosition) {
                            lnChild = new BomLineBean("" + envPosition);
                            lnItem.getBomLines().add(index, lnChild);
                        }
                        removableNode = false;
                    } else {  // LN and ENV same position; Do nothing
                        if ((!envChild.getType().equals("dummy_type") && !envChild.getName().equals("dummy_obj"))
                                || (!lnChild.getType().equals("dummy_type") && !lnChild.getName().equals("dummy_obj"))) {
                            String envName = "";
                            String lnName = "";
                            float envQty = -1f;
                            float lnQty = -1f;
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
                            if (!NullOrEmptyChecker.isNullOrEmpty(lnChild.getName())) {
                                lnName = lnChild.getName();
                            }
                            if (!NullOrEmptyChecker.isNullOrEmpty(lnChild.getQty())) {
                                try {
                                    lnQty = Float.parseFloat(lnChild.getQty());
                                } catch (NumberFormatException e) {
                                    BOM_COMPARISON_LOGGER.error("Error parsing LN " + lnName + " Qty " + lnQty + " : " + e.getMessage());
                                }
                            }
                            if ((envName.equalsIgnoreCase(lnName)) && (envQty == lnQty)) {
                                removableNode = true;
                            }
                        }
                    }
                }
                List<Integer> removalableChildrenIndice = prepareComparisonTreeForDifference(envChild, lnChild, false);
                int childSize = envChild.getBomLines().size() > lnChild.getBomLines().size() ? envChild.getBomLines().size() : lnChild.getBomLines().size();
                if (childSize != 0) {
                    // checking difference in child bom
                    if (removalableChildrenIndice.size() < childSize) {  // some child bom has difference
                        hasNonRemovableChild = true;
                        removableNode = false;
                        removeChildrenFromBOMItem(envChild, lnChild, removalableChildrenIndice);
                    } else {    // no difference exist in child bom
                        envChild.getBomLines().clear();
                        lnChild.getBomLines().clear();
                    }
                }
                if (removableNode && !hasNonRemovableChild) { // Item has no difference and child bom has no difference
                    itemRemoveList.add(index);
                }
                index++;
            }
            if (isRoot) {
                enoviaRootItem = enoviaItem;
                lnRootItem = lnItem;
            }
            return itemRemoveList;
        } catch (Exception e) {
            BOM_COMPARISON_LOGGER.error("Error:::--> ", e);
            return itemRemoveList;
        }
    }

    private void removeChildrenFromBOMItem(EnoviaItem enoviaItem, BomLineBean lnItem, List<Integer> removableChildrenIndice) {
        BOM_COMPARISON_LOGGER.info("Removing removable children");
        List<EnoviaItem> removableEnoviaChildren = new ArrayList<>();
        List<BomLineBean> removableLNChildren = new ArrayList<>();
        for (Integer childIndex : removableChildrenIndice) {
            removableEnoviaChildren.add(enoviaItem.getBomLines().get(childIndex));
            removableLNChildren.add(lnItem.getBomLines().get(childIndex));
        }
        for (int i = 0; i < removableChildrenIndice.size(); i++) {
            enoviaItem.getBomLines().remove(removableEnoviaChildren.get(i));
            lnItem.getBomLines().remove(removableLNChildren.get(i));
        }
    }
}
