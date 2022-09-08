/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.controller.modelVersion.processor;

import com.bjit.common.code.utility.aton.automation.AtributeMapping;
import com.bjit.common.code.utility.aton.automation.BasicClassInfo;
import com.bjit.common.rest.app.service.controller.item.factories.ItemValidatorFactory;
import com.bjit.common.rest.app.service.controller.item.factories.XMLAttributeMapperFactory;
import com.bjit.common.rest.app.service.controller.item.interfaces.IItemValidator;
import com.bjit.common.rest.app.service.controller.item.interfaces.IXmlMapperProcessor;
import com.bjit.common.rest.app.service.controller.item.validators.CommonItemParameters;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.AtonRequestModel;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.AtonResponseModel;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.Model;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.SearchConfigService;
import com.bjit.common.rest.app.service.controller.modelVersion.atoncontext.SearchConfigServiceImpl;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateObjectBean;
import com.bjit.common.rest.app.service.model.modelVersion.MVCreateUpdateResponseFormatter;
import com.bjit.common.rest.app.service.model.modelVersion.MVDataTree;
import com.bjit.common.rest.app.service.model.modelVersion.MVUpdateRequestModel;
import com.bjit.common.rest.app.service.model.tnr.TNR;
import com.bjit.common.rest.app.service.service.modelVersionService.MVImportService;
import com.bjit.common.rest.app.service.service.modelVersionService.MVImportServiceImpl;
import com.bjit.common.rest.app.service.utilities.BusinessObjectOperations;
import com.bjit.common.rest.app.service.utilities.DsServiceCall;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.item_bom_import.item_import.AtonAttributeBusinessLogic;
import com.bjit.common.rest.item_bom_import.utility.BusinessObjectUtil;
import com.bjit.common.rest.item_bom_import.xml_mapping_model.ItemImportMapping;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import matrix.db.Context;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**

 @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class MVImportProcessor
{

    private static final Logger MV_CREATION_PROCESSOR = Logger.getLogger(MVImportProcessor.class);

    private static final String SOURCE_ATON = PropertyReader.getProperty("aton.integration.source");
    private static final String MASTERSHIP_ATON = PropertyReader.getProperty("aton.integration.mastership");
    private static final String SOURCE_TOOL = PropertyReader.getProperty("tool.integration.source");
    private static final String MASTERSHIP_TOOL = PropertyReader.getProperty("tool.integration.mastership");

//    private final String itemOwnerGroup = "Aton Items";
//    private final String itemInventoryUnit = "pcs";
//    private final String itemPurchaseStatisticsGroup = "DPCP02s";
//    private final String itemUnitSet = "Metric";
    private final String itemType = PropertyReader.getProperty("aton.childmfgitem.type");
    private final String topItemType = PropertyReader.getProperty("aton.topmfgitem.type");
    private final String modelVersionType = "Products";
    private final String contextItemStatus = "IN_WORK";

    public List<MVCreateUpdateResponseFormatter> mvCreation(
            List<MVDataTree> mvRequest, MVImportService mvService,
            DsServiceCall dsCall, Context context, String source) throws Exception
    {
        try {
            List<MVCreateUpdateResponseFormatter> results = new ArrayList<>();
            try {
                List<MVCreateUpdateResponseFormatter> result = mvService.createMV(mvRequest, dsCall);
                //add user group
                if (!NullOrEmptyChecker.isNullOrEmpty(result)) {
                    List<HashMap<String, String>> params = new ArrayList();
                    HashMap<String, String> map = new HashMap();
                    map.put("mvPhysicalId", result.get(0).getMvPhysicalId());
                    params.add(map);
                    if (mvService.addUserGroupToMV(params, dsCall)) {
                        MV_CREATION_PROCESSOR.info("User group added");
                    }
                    results.addAll(result);
                }
            }
            catch (Exception ex) {
                MV_CREATION_PROCESSOR.error(ex.getMessage());
                throw ex;
            }
            return results;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<MVCreateUpdateResponseFormatter> mvUpdate(
            List<MVDataTree> mvRequest, MVImportService mvService,
            DsServiceCall dsCall, Context context) throws Exception
    {
        try {
            List<MVCreateUpdateResponseFormatter> results = new ArrayList<>();

            mvRequest.forEach(req -> {
                try {
                    MVUpdateRequestModel attr = new MVUpdateRequestModel();
                    attr.setTitle(req.getItem().getAttributes().get("title"));
                    attr.setName(req.getItem().getTnr().getName());
                    attr.setDescription(req.getItem().getAttributes().get("description") == null ? "" : req.getItem().getAttributes().get("description"));
                    attr.setBasePrice(req.getItem().getAttributes().get("basePrice") == null ? "0.0" : req.getItem().getAttributes().get("basePrice"));
                    attr.setCustomerAttributes(new HashMap<String, HashMap<String, String>>());
                    List<MVCreateUpdateResponseFormatter> result = mvService.updateMV(attr, req.getItem().getAttributes().get("mvPhysicalId"), dsCall);
                    //add user group
                    if (!NullOrEmptyChecker.isNullOrEmpty(result)) {
                        List<HashMap<String, String>> params = new ArrayList();
                        HashMap<String, String> map = new HashMap();
                        map.put("mvPhysicalId", result.get(0).getMvPhysicalId());
                        params.add(map);
                        if (mvService.addUserGroupToMV(params, dsCall)) {
                            MV_CREATION_PROCESSOR.info("User group added");
                        }
                        results.addAll(result);
                    }
                }
                catch (Exception ex) {
                    MV_CREATION_PROCESSOR.error("Update Error: " + ex.getMessage());
                }
            });

            return results;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public boolean mvUpdateForRevise(
            List<MVDataTree> mvRequest, MVImportService mvService,
            Context context) throws Exception
    {
        boolean isOkay = false;
        List<HashMap<String, String>> intList = new ArrayList();
        for (MVDataTree req : mvRequest) {
            HashMap<String, String> intMap = new HashMap();
            intMap.put("mvPhysicalId", req.getItem().getAttributes().get("mvPhysicalId"));
            intMap.put("title", req.getItem().getAttributes().get("title"));
            intMap.put("status", req.getItem().getAttributes().get("status"));
            intList.add(intMap);
        }
        try {
            isOkay = mvService.updateMaturityState(context, intList);
            return isOkay;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

//    public List<HashMap<String, String>> itemsCreation(Context context,
//                                                       MVImportService mvService,
//                                                       List<MVDataTree> request) throws Exception
//    {
//        ObjectDataBean odb = new ObjectDataBean();
//        List<DataTree> dtList = new ArrayList();
//        for (MVDataTree req : request) {
//            DataTree dt = new DataTree();
//            CreateObjectBean cob = new CreateObjectBean();
//            HashMap<String, String> map = new HashMap();
//            map.put("Title in English", req.getItem().getAttributes().get("title"));
//            map.put("Short name in English", req.getItem().getAttributes().get("title"));
//            map.put("Mastership", MASTERSHIP);
//            //map.put("Owner Group", req.getItem().getAttributes().get("ownerGroup") == null ? itemOwnerGroup : req.getItem().getAttributes().get("ownerGroup"));
//            map.put("Owner Group", itemOwnerGroup);
//            map.put("Inventory unit", itemInventoryUnit);
//            map.put("Purchase Statistics Group", itemPurchaseStatisticsGroup);
//            map.put("Unit set", itemUnitSet);
//            map.put("Status", req.getItem().getAttributes().get("status"));
//            map.put("LifeCycleStatus", req.getItem().getAttributes().get("status"));
//            map.put("Version", req.getItem().getTnr().getRevision());
//            map.put("ItemCode", req.getItem().getTnr().getName());
//            cob.setAttributes(map);
//            TNR tnr = new TNR();
//            tnr.setType(itemType);
//            tnr.setName("");
//            tnr.setRevision(itemCreateVersion);
//            cob.setTnr(tnr);
//            cob.setIsAutoName(Boolean.TRUE);
//            dt.setItem(cob);
//            dtList.add(dt);
//
//            dt = new DataTree();
//            cob = new CreateObjectBean();
//            map = new HashMap();
//            map.put("Title in English", req.getItem().getAttributes().get("title"));
//            map.put("Short name in English", req.getItem().getAttributes().get("title"));
//            map.put("Mastership", MASTERSHIP);
//            //map.put("Owner Group", req.getItem().getAttributes().get("ownerGroup") == null ? itemOwnerGroup : req.getItem().getAttributes().get("ownerGroup"));
//            map.put("Owner Group", itemOwnerGroup);
//            map.put("Inventory unit", itemInventoryUnit);
//            map.put("Purchase Statistics Group", itemPurchaseStatisticsGroup);
//            map.put("Unit set", itemUnitSet);
//            map.put("Status", req.getItem().getAttributes().get("status"));
//            map.put("LifeCycleStatus", req.getItem().getAttributes().get("status"));
//            map.put("Version", req.getItem().getTnr().getRevision());
//            cob.setAttributes(map);
//            tnr = new TNR();
//            tnr.setType(itemType);
//            tnr.setName("");
//            tnr.setRevision(itemCreateVersion);
//            cob.setTnr(tnr);
//            cob.setIsAutoName(Boolean.TRUE);
//            dt.setItem(cob);
//            dtList.add(dt);
//        }
//
//        odb.setDataTree(dtList);
//        odb.setSource(SOURCE);
//        try {
//            List<HashMap<String, String>> result = mvService.createItems(context, odb);
//            return result;
//        }
//        catch (Exception e) {
//            MV_CREATION_PROCESSOR.error(e.getMessage());
//            throw e;
//        }
//    }
    public List<HashMap<String, String>> itemsCreation(
            MVImportService mvService,
            List<MVDataTree> request, String source, DsServiceCall dsCall,
            Context context) throws Exception
    {
        List<HashMap<String, String>> itemList = new ArrayList();

        Boolean topItemExists = false;
        Boolean childItemExists = false;
        List<HashMap<String, String>> mfgItemInfo = new ArrayList();

        for (MVDataTree req : request) {

            SearchConfigService searchService = new SearchConfigServiceImpl();
            TNR tnr = new TNR(null, req.getItem().getTnr().getName(), null);
            mfgItemInfo = searchService.getMfgItemInfo(tnr, req.getItem().getTnr().getRevision(), topItemType, itemType, context);
            for (HashMap<String, String> mfgItem : mfgItemInfo) {
                if (mfgItem.get("type").equalsIgnoreCase(topItemType)) {
                    topItemExists = true;
                } else {
                    if (mfgItem.get("type").equalsIgnoreCase(itemType)) {
                        childItemExists = true;
                    }
                }
            }

            HashMap<String, String> map;
            if (!topItemExists && !childItemExists) {
                map = new HashMap();
                map.put("title", "CONTEXT-" + req.getItem().getAttributes().get("title"));
                map.put("description", req.getItem().getAttributes().get("description") == null ? "" : req.getItem().getAttributes().get("description"));
                map.put("type", topItemType);
                if (source.equalsIgnoreCase(SOURCE_ATON)) {
                    map.put("mastership", MASTERSHIP_ATON);
                } else {
                    if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                        map.put("mastership", MASTERSHIP_TOOL);
                    }
                }
                map.put("status", req.getItem().getAttributes().get("status"));
                map.put("version", req.getItem().getTnr().getRevision());
                map.put("itemCode", req.getItem().getTnr().getName());
                itemList.add(map);

                map = new HashMap();
                map.put("title", req.getItem().getAttributes().get("title"));
                map.put("description", req.getItem().getAttributes().get("description") == null ? "" : req.getItem().getAttributes().get("description"));
                map.put("type", itemType);
                if (source.equalsIgnoreCase(SOURCE_ATON)) {
                    map.put("mastership", MASTERSHIP_ATON);
                } else {
                    if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                        map.put("mastership", MASTERSHIP_TOOL);
                    }
                }
                map.put("status", req.getItem().getAttributes().get("status"));
                map.put("version", req.getItem().getTnr().getRevision());
                map.put("itemCode", req.getItem().getTnr().getName());
                itemList.add(map);
            }
        }

        try {
            List<HashMap<String, String>> result = new ArrayList();
            if (NullOrEmptyChecker.isNullOrEmpty(itemList)) {
                result.addAll(mfgItemInfo);
                int i = 0;
                for (HashMap<String, String> mfgItem : mfgItemInfo) {
                    if (mfgItem.get("type").equalsIgnoreCase(itemType)) {
                        HashMap<String, String> updateMap = new HashMap();
                        updateMap.put("physicalId", mfgItem.get("physicalId"));
                        updateMap.put("title", request.get(i).getItem().getAttributes().get("title"));
                        updateMap.put("description", request.get(i).getItem().getAttributes().get("description") == null ? "" : request.get(i).getItem().getAttributes().get("description"));
                        updateMap.put("status", request.get(i).getItem().getAttributes().get("status"));
                        updateMap.put("version", request.get(i).getItem().getTnr().getRevision());
                        if (mvService.updateChildItemInfo(context, updateMap)) {
                            MV_CREATION_PROCESSOR.info("Child item updated");
                        }
                        i++;
                    }
                }
            } else {
                result = mvService.mfgItemCreate(itemList, dsCall);
                int i = 0;
                for (int j = 0; j < result.size(); j += 2) {
                    HashMap<String, String> updateMap = new HashMap();
                    updateMap.put("latestRevision", request.get(i).getItem().getTnr().getRevision());
                    updateMap.put("physicalId", result.get(j).get("physicalId"));
                    updateMap.put("status", contextItemStatus);
                    if (mvService.updateContextItemInfo(context, updateMap)) {
                        MV_CREATION_PROCESSOR.info("Top item updated");
                    }

                    updateMap = new HashMap();
                    updateMap.put("physicalId", result.get(j + 1).get("physicalId"));
                    updateMap.put("title", request.get(i).getItem().getAttributes().get("title"));
                    updateMap.put("description", request.get(i).getItem().getAttributes().get("description") == null ? "" : request.get(i).getItem().getAttributes().get("description"));
                    updateMap.put("status", request.get(i).getItem().getAttributes().get("status"));
                    updateMap.put("version", request.get(i).getItem().getTnr().getRevision());
                    if (mvService.updateChildItemInfo(context, updateMap)) {
                        MV_CREATION_PROCESSOR.info("Child item updated");
                    }
                    i++;
                }
            }
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

//    public List<HashMap<String, String>> bomCreation(Context context,
//                                                     MVImportService mvService,
//                                                     List<TNR> items) throws Exception
//    {
//        List<CreateBOMBeanExtended> createBOMBeanList = new ArrayList();
//
//        List<HashMap<String, TNR>> itemSet = new ArrayList();
//        for (int i = 0; i < items.size(); i += 2) {
//            HashMap<String, TNR> map = new HashMap();
//            map.put("top", items.get(i));
//            map.put("child", items.get(i + 1));
//            itemSet.add(map);
//        }
//
//        for (HashMap<String, TNR> tnrSet : itemSet) {
//            CreateBOMBeanExtended cbb = new CreateBOMBeanExtended();
//            cbb.setItem(tnrSet.get("top"));
//            List<HashMap<String, String>> linesList = new ArrayList();
//            HashMap<String, String> lines = new HashMap();
//            TNR childItem = tnrSet.get("child");
//            lines.put("type", childItem.getType());
//            lines.put("component", childItem.getName());
//            lines.put("revision", childItem.getRevision());
//            lines.put("Position", "1");
//            lines.put("Net quantity", "1");
//            linesList.add(lines);
//            cbb.setLines(linesList);
//            cbb.setSource(SOURCE_ATON);
//            createBOMBeanList.add(cbb);
//        }
//
//        try {
//            List<HashMap<String, String>> result = mvService.createBom(context, createBOMBeanList);
//            return result;
//        }
//        catch (Exception e) {
//            MV_CREATION_PROCESSOR.error(e.getMessage());
//            throw e;
//        }
//    }

    public List<HashMap<String, String>> createItemInstance(
            MVImportService mvService,
            List<HashMap<String, String>> items, DsServiceCall dsCall,
            Context context) throws Exception
    {
        List<HashMap<String, String>> instParams = new ArrayList();
        List<HashMap<String, String>> existingInstList = new ArrayList();
        for (int i = 0; i < items.size(); i += 2) {
            existingInstList = mvService.isInstanceExists(items.get(i).get("physicalId"), items.get(i + 1).get("physicalId"), context);

            HashMap<String, String> instMap = new HashMap();
            instMap.put("topItemPhysicalId", items.get(i).get("physicalId"));
            instMap.put("childItemPhysicalId", items.get(i + 1).get("physicalId"));
            instParams.add(instMap);
        }
        try {
            List<HashMap<String, String>> result = new ArrayList();
            if (NullOrEmptyChecker.isNullOrEmpty(existingInstList)) {
                result = mvService.createItemInstance(instParams, dsCall);
            } else {
                result.addAll(existingInstList);
            }
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> setEvolution(
            MVImportService mvService,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<HashMap<String, String>> itemList,
            List<HashMap<String, String>> bomList, DsServiceCall dsCall) throws Exception
    {
        List<HashMap<String, String>> setEvolutionParams = new ArrayList();
        if (( !NullOrEmptyChecker.isNullOrEmpty(mvCreationResult) && !mvCreationResult.isEmpty() )
                && ( !NullOrEmptyChecker.isNullOrEmpty(itemList) && !itemList.isEmpty() )
                && ( !NullOrEmptyChecker.isNullOrEmpty(bomList) && !bomList.isEmpty() )) {
            int j = 0;
            for (int i = 0; i < mvCreationResult.size(); i++) {
                HashMap<String, String> setEvoMap = new HashMap();
                setEvoMap.put("modelPhysicalId", mvCreationResult.get(i).getModelPhysicalId());
                setEvoMap.put("modelName", mvCreationResult.get(i).getTnr().getName());
                setEvoMap.put("modelRevision", mvCreationResult.get(i).getTnr().getRevision());
                setEvoMap.put("topItemPhysicalId", itemList.get(j).get("physicalId"));
                j += 2;
                setEvoMap.put("relationPhysicalId", bomList.get(i).get("connectionPhysicalId"));
                setEvolutionParams.add(setEvoMap);
            }
        }
        try {
            List<HashMap<String, String>> result = mvService.setEvolution(setEvolutionParams, dsCall, false);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> addInterface(
            MVImportService mvService, Context context,
            List<MVDataTree> request, String source,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            DsServiceCall dsCall) throws Exception
    {

        List<HashMap<String, String>> result = new ArrayList();
        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {
            List<HashMap<String, String>> intParams = new ArrayList();
            if (request.get(i).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                HashMap<String, String> intMap = new HashMap();
                intMap.put("mvPhysicalId", res.getMvPhysicalId());
                intMap.put("atonRevision", request.get(i).getItem().getTnr().getRevision());
                if (source.equalsIgnoreCase(SOURCE_ATON)) {
                    intMap.put("mastership", MASTERSHIP_ATON);
                } else {
                    if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                        intMap.put("mastership", MASTERSHIP_TOOL);
                    }
                }
                intParams.add(intMap);
                List<String> interfaces = Arrays.asList(request.get(i).getItem().getAttributes().get("interfaces").split(",", -1));
                for (String inter : interfaces) {
                    if (!mvService.isInterfaceExist(context, res.getTnr(), inter)) {
                        try {
                            result.addAll(mvService.addAtonInterface(intParams, inter, dsCall));
                        }
                        catch (Exception e) {
                            MV_CREATION_PROCESSOR.error(e.getMessage());
                            throw e;
                        }
                    }
                }

                //update interface attributes
                intMap = new HashMap();
                intMap.put("mvPhysicalId", res.getMvPhysicalId());
                intMap.put("atonRevision", request.get(i).getItem().getTnr().getRevision());
                if (source.equalsIgnoreCase(SOURCE_ATON)) {
                    intMap.put("mastership", MASTERSHIP_ATON);
                } else {
                    if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                        intMap.put("mastership", MASTERSHIP_TOOL);
                    }
                }
                intMap.putAll(request.get(i).getItem().getAttributes());

                try {
                    if (mvService.updateInterface(context, intMap)) {
                        HashMap<String, String> resMap = new HashMap();
                        resMap.put("isOkay", "true");
                        resMap.put("atonRevision", request.get(i).getItem().getTnr().getRevision());
                        result.add(resMap);
                    } else {
                        HashMap<String, String> resMap = new HashMap();
                        resMap.put("isOkay", "false");
                        resMap.put("error", "Error in interface attributes update.");
                        result.add(resMap);
                    }
                }
                catch (Exception e) {
                    MV_CREATION_PROCESSOR.error(e.getMessage());
                    throw e;
                }
            }
            i++;
        }
        return result;
    }

    public List<HashMap<String, String>> changeMaturityState(
            MVImportService mvService, List<MVDataTree> request,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            DsServiceCall dsCall) throws Exception
    {
        List<HashMap<String, String>> stateParams = new ArrayList();
        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {
            if (request.get(i).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                HashMap<String, String> stateMap = new HashMap();
                stateMap.put("mvPhysicalId", res.getMvPhysicalId());
                stateMap.put("status", request.get(i).getItem().getAttributes().get("status"));
                stateParams.add(stateMap);
            }
            i++;
        }
        try {
            List<HashMap<String, String>> result = mvService.changeMaturityState(stateParams, dsCall);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public HashMap<String, List<MVDataTree>> searchForRevision(
            MVImportService mvService, Context context,
            List<MVDataTree> request, DsServiceCall dsCall, String source) throws Exception
    {
        System.out.println("+++++++++++++ preparing request model ++++++++++++++++++++");
        List<TNR> modelList = new ArrayList();
        request.forEach(req -> {
            TNR tnr = new TNR(modelVersionType, req.getItem().getTnr().getName(), "*");
            modelList.add(tnr);
        });

        AtonRequestModel reqModel = new AtonRequestModel();
        reqModel.setModels(modelList);

        SearchConfigService service = new SearchConfigServiceImpl();
        AtonResponseModel atonResponseModel = null;
        try {
            atonResponseModel = service.execute(reqModel, topItemType, context, source);
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.info(e);
        }
        MV_CREATION_PROCESSOR.info(new JSON(true, true).serialize(atonResponseModel));

        List<MVDataTree> createList = new ArrayList();
        List<MVDataTree> updateList = new ArrayList();
        List<MVDataTree> reviseList = new ArrayList();
        int i = 0;
        for (Model model : atonResponseModel.getModels()) {
            if (!model.getModelExists()) {
                if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                    boolean putInCreate = false;
                    if (createList.isEmpty()) {
                        createList.add(request.get(i));
                    } else {
                        for (MVDataTree tree : createList) {
                            if (model.getTnr().getName().equalsIgnoreCase(tree.getItem().getTnr().getName())
                                    && request.get(i).getItem().getTnr().getRevision().equalsIgnoreCase(tree.getItem().getTnr().getRevision())) {
                                updateList.add(request.get(i));
                                putInCreate = false;
                                break;
                            } else {
                                if (model.getTnr().getName().equalsIgnoreCase(tree.getItem().getTnr().getName())
                                        && !request.get(i).getItem().getTnr().getRevision().equalsIgnoreCase(tree.getItem().getTnr().getRevision())) {
                                    boolean putInRevise = false;
                                    if (reviseList.isEmpty()) {
                                        reviseList.add(request.get(i));
                                    } else {
                                        for (MVDataTree revtree : reviseList) {
                                            if (model.getTnr().getName().equalsIgnoreCase(revtree.getItem().getTnr().getName())
                                                    && request.get(i).getItem().getTnr().getRevision().equalsIgnoreCase(revtree.getItem().getTnr().getRevision())) {
                                                updateList.add(request.get(i));
                                                putInRevise = false;
                                                break;
                                            } else {
                                                putInRevise = true;
                                            }
                                        }
                                    }
                                    if (putInRevise) {
                                        reviseList.add(request.get(i));
                                    }
                                    putInCreate = false;
                                    break;
                                } else {
                                    putInCreate = true;
                                }
                            }
                        }
                        if (putInCreate) {
                            createList.add(request.get(i));
                        }
                    }
                }
            } else {
                if (model.getModelExists()) {
                    Boolean inserted = false;
                    if (!NullOrEmptyChecker.isNullOrEmpty(reviseList)) {
                        for (MVDataTree rev : reviseList) {
                            if (rev.getItem().getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())
                                    && rev.getItem().getTnr().getRevision().equalsIgnoreCase(request.get(i).getItem().getTnr().getRevision())) {
                                request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                                updateList.add(request.get(i));
                                inserted = true;
                                break;
                            }
                        }
                    }
                    if (!inserted) {
                        Map<String, List<String>> queueList = new HashMap<String, List<String>>();
                        List<String> queue = new ArrayList<String>();
                        queue.add(request.get(i).getItem().getTnr().getRevision());
                        queueList.put(model.getTnr().getName(), queue);
                        //                    queue = new ArrayList<String>();
                        //                    queue.add(request.get(i).getItem().getAttributes().get("status"));
                        //                    queueList.put("status", queue);

                        List<Model> modList = new ArrayList();
                        modList.add(model);

                        Map<String, Map<String, Boolean>> updateMap = mvService.checkForRevisions(modList, queueList, dsCall, context);
                        if (!updateMap.get(model.getTnr().getName()).get(request.get(i).getItem().getTnr().getRevision())
                                && !NullOrEmptyChecker.isNull(updateMap.get("latestRevision"))) {
                            if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                                request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                                if (!NullOrEmptyChecker.isNull(model.getManufacturingItemInfo())) {
                                    request.get(i).getItem().getAttributes().put("topItemPhysicalId", model.getManufacturingItemInfo().getPhysicalid());
                                }

                                for (Entry<String, Boolean> entry : updateMap.get("latestRevision").entrySet()) {
                                    if (entry.getValue()) {
                                        request.get(i).getItem().getAttributes().put("latestRevision", entry.getKey());
                                    }
                                }
                                for (Entry<String, Boolean> entry : updateMap.get("latestRevisionPhysicalId").entrySet()) {
                                    if (entry.getValue()) {
                                        request.get(i).getItem().getAttributes().put("latestRevisionPhysicalId", entry.getKey());
                                    }
                                }
                                reviseList.add(request.get(i));
                            }
                        } else {
                            if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                                request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                                updateList.add(request.get(i));
                            }
                        }
                    }
                }
            }
            i++;
        }

        HashMap<String, List<MVDataTree>> reqMap = new HashMap();
        reqMap.put("createList", createList);
        reqMap.put("updateList", updateList);
        reqMap.put("reviseList", reviseList);

        return reqMap;
    }

    public HashMap<String, List<MVDataTree>> searchForRevisionSingle(
            MVImportService mvService, Context context,
            List<MVDataTree> request, DsServiceCall dsCall, String source) throws Exception
    {
        System.out.println("+++++++++++++ preparing request model ++++++++++++++++++++");
        List<TNR> modelList = new ArrayList();
        request.forEach(req -> {
            TNR tnr = new TNR(modelVersionType, req.getItem().getTnr().getName(), "*");
            modelList.add(tnr);
        });

        AtonRequestModel reqModel = new AtonRequestModel();
        reqModel.setModels(modelList);

        SearchConfigService service = new SearchConfigServiceImpl();
        AtonResponseModel atonResponseModel = null;
        try {
            atonResponseModel = service.execute(reqModel, topItemType, context, source);
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.info(e);
        }
        MV_CREATION_PROCESSOR.info(new JSON(true, true).serialize(atonResponseModel));

        List<MVDataTree> createList = new ArrayList();
        List<MVDataTree> updateList = new ArrayList();
        List<MVDataTree> reviseList = new ArrayList();
        int i = 0;
        for (Model model : atonResponseModel.getModels()) {
            if (!model.getModelExists()) {
                createList.add(request.get(i));
            } else {
                if (model.getModelExists()) {
                    Map<String, List<String>> queueList = new HashMap<String, List<String>>();
                    List<String> queue = new ArrayList<String>();
                    queue.add(request.get(i).getItem().getTnr().getRevision());
                    queueList.put(model.getTnr().getName(), queue);

                    List<Model> modList = new ArrayList();
                    modList.add(model);

                    Map<String, Map<String, Boolean>> updateMap = mvService.checkForRevisions(modList, queueList, dsCall, context);
                    if (!updateMap.get(model.getTnr().getName()).get(request.get(i).getItem().getTnr().getRevision())
                            && !NullOrEmptyChecker.isNull(updateMap.get("latestRevision"))) {
                        if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                            request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                            if (model.getManufacturingItemInfo() != null) {
                                //if top item exists, we can assume its a revision request
                                request.get(i).getItem().getAttributes().put("isRevision", "true");
                                request.get(i).getItem().getAttributes().put("topItemPhysicalId", model.getManufacturingItemInfo().getPhysicalid());
                            } else {
                                request.get(i).getItem().getAttributes().put("isRevision", "false");
                            }

                            for (Entry<String, Boolean> entry : updateMap.get("latestRevision").entrySet()) {
                                if (entry.getValue()) {
                                    request.get(i).getItem().getAttributes().put("latestRevision", entry.getKey());
                                }
                            }
                            for (Entry<String, Boolean> entry : updateMap.get("latestRevisionPhysicalId").entrySet()) {
                                if (entry.getValue()) {
                                    request.get(i).getItem().getAttributes().put("latestRevisionPhysicalId", entry.getKey());
                                }
                            }
                            reviseList.add(request.get(i));
                        }
                    } else {
                        if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                            request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                            request.get(i).getItem().getAttributes().put("isRevision", "false");
                            updateList.add(request.get(i));
                        }
                    }

                }
            }
            i++;
        }

        HashMap<String, List<MVDataTree>> reqMap = new HashMap();
        reqMap.put("createList", createList);
        reqMap.put("updateList", updateList);
        reqMap.put("reviseList", reviseList);

        return reqMap;
    }

    public List<MVCreateUpdateResponseFormatter> reviseMV(
            List<MVDataTree> request, String source,
            MVImportService mvService,
            DsServiceCall dsCall, Context context) throws Exception
    {
        List<HashMap<String, String>> revParams = new ArrayList();

        for (MVDataTree req : request) {
            HashMap<String, String> revMap = new HashMap();
            revMap.put("mvPhysicalId", req.getItem().getAttributes().get("latestRevisionPhysicalId"));
            revMap.put("name", req.getItem().getTnr().getName());
            revMap.put("description", req.getItem().getAttributes().get("description") == null ? "" : req.getItem().getAttributes().get("description"));
            revMap.put("basePrice", req.getItem().getAttributes().get("basePrice") == null ? "0.0" : req.getItem().getAttributes().get("basePrice"));
            revMap.put("revision", req.getItem().getTnr().getRevision());
            if (source.equalsIgnoreCase(SOURCE_ATON)) {
                revMap.put("mastership", MASTERSHIP_ATON);
            } else {
                if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                    revMap.put("mastership", MASTERSHIP_TOOL);
                }
            }
            revMap.put("status", req.getItem().getAttributes().get("status"));
            revParams.add(revMap);
        }
        try {
            List<HashMap<String, String>> response = mvService.reviseMV(revParams, dsCall);
            List<MVCreateUpdateResponseFormatter> result = new ArrayList();
            int i = 0;
            for (HashMap<String, String> resp : response) {
                if (resp.get("isOkay").equals("true") && resp.get("derivedFromPhysicalId").equalsIgnoreCase(request.get(i).getItem().getAttributes().get("latestRevisionPhysicalId"))) {
                    List<MVCreateUpdateResponseFormatter> res = mvService.getMV(resp.get("physicalId"), dsCall);
                    //add user group
                    if (!NullOrEmptyChecker.isNullOrEmpty(res)) {
                        List<HashMap<String, String>> params = new ArrayList();
                        HashMap<String, String> map = new HashMap();
                        map.put("mvPhysicalId", res.get(0).getMvPhysicalId());
                        params.add(map);
                        if (mvService.addUserGroupToMV(params, dsCall)) {
                            MV_CREATION_PROCESSOR.info("User group added");
                        }
                        result.addAll(res);
                    }
                } else {
                    throw new Exception(resp.get("error"));
                }
                i++;
            }
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public HashMap<String, List<MVDataTree>> checkRevisionForUpdate(
            MVImportService mvService, Context context,
            List<MVDataTree> request,
            List<MVCreateUpdateResponseFormatter> result,
            DsServiceCall dsCall, String source) throws Exception
    {
        System.out.println("+++++++++++++ checkRevisionForUpdate ++++++++++++++++++++");
        List<TNR> modelList = new ArrayList();
        request.forEach(req -> {
            TNR tnr = new TNR(modelVersionType, req.getItem().getTnr().getName(), req.getItem().getTnr().getRevision());
            modelList.add(tnr);
        });

        AtonRequestModel reqModel = new AtonRequestModel();
        reqModel.setModels(modelList);

        SearchConfigService service = new SearchConfigServiceImpl();
        AtonResponseModel atonResponseModel = null;
        try {
            atonResponseModel = service.execute(reqModel, topItemType, context, source);
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.info(e);
        }
        MV_CREATION_PROCESSOR.info(new JSON(true, true).serialize(atonResponseModel));

        List<MVDataTree> updateList = new ArrayList();
        int i = 0;
        for (Model model : atonResponseModel.getModels()) {
            if (model.getModelExists()) {
                if (model.getTnr().getName().equalsIgnoreCase(request.get(i).getItem().getTnr().getName())) {
                    request.get(i).getItem().getAttributes().put("mvPhysicalId", model.getModelInfo().getPhysicalid());
                    if (model.getManufacturingItemInfo() != null) {
                        //if top item exists, we can assume its a revision request
                        request.get(i).getItem().getAttributes().put("isRevision", "true");
                        request.get(i).getItem().getAttributes().put("topItemPhysicalId", model.getManufacturingItemInfo().getPhysicalid());
                    } else {
                        request.get(i).getItem().getAttributes().put("isRevision", "false");
                    }
                    updateList.add(request.get(i));
                }
            }
            i++;
        }

        HashMap<String, List<MVDataTree>> reqMap = new HashMap();
        reqMap.put("updateList", updateList);

        return reqMap;
    }

    public HashMap<String, List<MVDataTree>> getLatestRevision(
            MVImportService mvCreateService,
            List<MVDataTree> request,
            HashMap<String, HashMap<String, String>> latestRevisionMap) throws Exception
    {
        List<MVDataTree> reviseList = new ArrayList();
        for (MVDataTree tree : request) {
            if (latestRevisionMap.containsKey(tree.getItem().getTnr().getName())) {
                tree.getItem().getAttributes().put("latestRevision", latestRevisionMap.get(tree.getItem().getTnr().getName()).get("latestRevision"));
                tree.getItem().getAttributes().put("latestRevisionPhysicalId", latestRevisionMap.get(tree.getItem().getTnr().getName()).get("latestRevisionPhysicalId"));
                tree.getItem().getAttributes().put("topItemPhysicalId", latestRevisionMap.get(tree.getItem().getTnr().getName()).get("topItemPhysicalId"));
            }
            reviseList.add(tree);
        }
        HashMap<String, List<MVDataTree>> reqMap = new HashMap();
        reqMap.put("reviseList", reviseList);
        return reqMap;
    }

    public String getTopMfgItem(TNR tnr, Context context)
    {
        SearchConfigService search = new SearchConfigServiceImpl();
        String topItemPhysicalId = null;
        try {
            topItemPhysicalId = search.getTopMfgItem(tnr, topItemType, context);
        }
        catch (Exception ex) {
            MV_CREATION_PROCESSOR.error(ex);
        }
        return topItemPhysicalId;
    }
    
//    public List<HashMap<String, String>> childItemsCreationForRevision(
//            Context context, MVImportService mvService,
//            List<MVDataTree> request, String source, DsServiceCall dsCall) throws Exception
//    {
//        ObjectDataBean odb = new ObjectDataBean();
//        List<DataTree> dtList = new ArrayList();
//
//        Boolean childItemExists = false;
//        List<HashMap<String, String>> mfgItemInfo = new ArrayList();
//
//        for (MVDataTree req : request) {
//
//            SearchConfigService searchService = new SearchConfigServiceImpl();
//            TNR tnr2 = new TNR(null, req.getItem().getTnr().getName(), null);
//            mfgItemInfo = searchService.getMfgItemInfo(tnr2, req.getItem().getTnr().getRevision(), topItemType, itemType, context);
//            for (HashMap<String, String> mfgItem : mfgItemInfo) {
//                if (mfgItem.get("type").equalsIgnoreCase(itemType)) {
//                    childItemExists = true;
//                }
//            }
//
//            if (!childItemExists) {
//                DataTree dt = new DataTree();
//                CreateObjectBean cob = new CreateObjectBean();
//                HashMap<String, String> map = new HashMap();
//                map.put("Title in English", req.getItem().getAttributes().get("title"));
//                map.put("Short name in English", req.getItem().getAttributes().get("title"));
//                if (source.equalsIgnoreCase(SOURCE_ATON)) {
//                    map.put("mastership", MASTERSHIP_ATON);
//                } else {
//                    if (source.equalsIgnoreCase(SOURCE_TOOL)) {
//                        map.put("mastership", MASTERSHIP_TOOL);
//                    }
//                }
//                //map.put("Owner Group", req.getItem().getAttributes().get("ownerGroup") == null ? itemOwnerGroup : req.getItem().getAttributes().get("ownerGroup"));
//                map.put("Owner Group", itemOwnerGroup);
//                map.put("Inventory unit", itemInventoryUnit);
//                map.put("Purchase Statistics Group", itemPurchaseStatisticsGroup);
//                map.put("Unit set", itemUnitSet);
//                map.put("Status", req.getItem().getAttributes().get("status"));
//                map.put("LifeCycleStatus", req.getItem().getAttributes().get("status"));
//                map.put("Version", req.getItem().getTnr().getRevision());
//                map.put("ItemCode", req.getItem().getTnr().getName());
//                cob.setAttributes(map);
//                TNR tnr = new TNR();
//                tnr.setType(itemType);
//                //System.out.println("top item="+req.getItem().getAttributes().get("topItemPhysicalId"));
//                HashMap<String, String> existingChildItem = mvService.getChildInfoFromTopItem(req.getItem().getAttributes().get("topItemPhysicalId"), dsCall);
//                TNR childTnr = mvService.getTNRfromPhysicalId(context, existingChildItem.get("childItemPhysicalId"));
//                tnr.setName(childTnr.getName());
//                tnr.setRevision(childTnr.getRevision());
//                cob.setTnr(tnr);
//                cob.setIsAutoName(Boolean.FALSE);
//                cob.setNextVersion("01");
//                dt.setItem(cob);
//                dtList.add(dt);
//            }
//        }
//        odb.setDataTree(dtList);
//        odb.setSource(SOURCE_ATON);
//        try {
//            List<HashMap<String, String>> result = new ArrayList();
//            if (NullOrEmptyChecker.isNullOrEmpty(dtList)) {
//                result.addAll(mfgItemInfo);
//            } else {
//                result = mvService.createItems(context, odb);
//                result.addAll(mfgItemInfo);
//                int i = 0;
//                for (MVDataTree req : request) {
//                    HashMap<String, String> updateMap = new HashMap();
//                    updateMap.put("latestRevision", req.getItem().getTnr().getRevision());
//                    updateMap.put("topItemPhysicalId", req.getItem().getAttributes().get("topItemPhysicalId"));
//                    updateMap.put("status", req.getItem().getAttributes().get("status"));
//                    result.get(i).put("topItemPhysicalId", req.getItem().getAttributes().get("topItemPhysicalId"));
//                    if (mvService.updateTopItemSourceVersion(context, updateMap)) {
//                        MV_CREATION_PROCESSOR.info("Top item updated");
//                    }
//                    i++;
//                }
//            }
//            return result;
//        }
//        catch (Exception e) {
//            MV_CREATION_PROCESSOR.error(e.getMessage());
//            throw e;
//        }
//    }
    public List<HashMap<String, String>> childItemsCreationForRevision(
            Context context, MVImportService mvService,
            List<MVDataTree> request, String source, DsServiceCall dsCall) throws Exception
    {
        List<HashMap<String, String>> itemList = new ArrayList();
        Boolean childItemExists = false;
        List<HashMap<String, String>> mfgItemInfo = new ArrayList();
        for (MVDataTree req : request) {

            SearchConfigService searchService = new SearchConfigServiceImpl();
            TNR tnr2 = new TNR(null, req.getItem().getTnr().getName(), null);
            mfgItemInfo = searchService.getMfgItemInfo(tnr2, req.getItem().getTnr().getRevision(), topItemType, itemType, context);
            for (HashMap<String, String> mfgItem : mfgItemInfo) {
                if (mfgItem.get("type").equalsIgnoreCase(itemType)) {
                    childItemExists = true;
                }
            }

            if (!childItemExists) {
                HashMap<String, String> map = new HashMap();
                HashMap<String, String> existingChildItem = mvService.getChildInfoFromTopItem(req.getItem().getAttributes().get("topItemPhysicalId"), dsCall);
                map.put("itemType", itemType);
                map.put("itemPhysicalId", existingChildItem.get("childItemPhysicalId"));
                itemList.add(map);
            }
        }
        try {
            List<HashMap<String, String>> result = new ArrayList();
            if (NullOrEmptyChecker.isNullOrEmpty(itemList)) {
                result.addAll(mfgItemInfo);
                int i = 0;
                for (HashMap<String, String> mfgItem : mfgItemInfo) {
                    if (mfgItem.get("type").equalsIgnoreCase(itemType)) {
                        HashMap<String, String> updateMap = new HashMap();
                        updateMap.put("physicalId", mfgItem.get("physicalId"));
                        updateMap.put("title", request.get(i).getItem().getAttributes().get("title"));
                        updateMap.put("description", request.get(i).getItem().getAttributes().get("description") == null ? "" : request.get(i).getItem().getAttributes().get("description"));
                        updateMap.put("status", request.get(i).getItem().getAttributes().get("status"));
                        updateMap.put("version", request.get(i).getItem().getTnr().getRevision());
                        if (mvService.updateChildItemInfo(context, updateMap)) {
                            MV_CREATION_PROCESSOR.info("Child item updated");
                        }
                        i++;
                    }
                }

            } else {
                result = mvService.createVersionOfMfgItem(itemList, dsCall, context);
                result.addAll(mfgItemInfo);
                int i = 0;
                for (MVDataTree req : request) {
                    HashMap<String, String> updateMap = new HashMap();
                    updateMap.put("latestRevision", req.getItem().getTnr().getRevision());
                    updateMap.put("topItemPhysicalId", req.getItem().getAttributes().get("topItemPhysicalId"));
                    //updateMap.put("status", req.getItem().getAttributes().get("status"));
                    result.get(i).put("topItemPhysicalId", req.getItem().getAttributes().get("topItemPhysicalId"));
                    if (mvService.updateTopItemSourceVersion(context, updateMap)) {
                        MV_CREATION_PROCESSOR.info("Top item updated");
                    }

                    updateMap = new HashMap();
                    updateMap.put("physicalId", result.get(i).get("physicalId"));
                    if (source.equalsIgnoreCase(SOURCE_ATON)) {
                        updateMap.put("mastership", MASTERSHIP_ATON);
                    } else {
                        if (source.equalsIgnoreCase(SOURCE_TOOL)) {
                            updateMap.put("mastership", MASTERSHIP_TOOL);
                        }
                    }
                    updateMap.put("title", req.getItem().getAttributes().get("title"));
                    updateMap.put("description", req.getItem().getAttributes().get("description") == null ? "" : req.getItem().getAttributes().get("description"));
                    updateMap.put("status", req.getItem().getAttributes().get("status"));
                    updateMap.put("version", req.getItem().getTnr().getRevision());
                    updateMap.put("itemCode", req.getItem().getTnr().getName());
                    if (mvService.updateChildItemInfo(context, updateMap)) {
                        MV_CREATION_PROCESSOR.info("Child item updated");
                    }
                    i++;
                }
            }

            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> createItemInstanceForRevision(
            MVImportService mvService, List<MVDataTree> request,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<HashMap<String, String>> childItemList, DsServiceCall dsCall,
            Context context) throws Exception
    {
        List<HashMap<String, String>> instParams = new ArrayList();
        List<HashMap<String, String>> existingInstList = new ArrayList();
        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {
            if (request.get(i).getItem().getTnr().getName().equalsIgnoreCase(res.getTnr().getName())) {
                existingInstList = mvService.isInstanceExists(request.get(i).getItem().getAttributes().get("topItemPhysicalId"), childItemList.get(i).get("physicalId"), context);
                HashMap<String, String> instMap = new HashMap();
                instMap.put("childItemPhysicalId", childItemList.get(i).get("physicalId"));
                instMap.put("topItemPhysicalId", request.get(i).getItem().getAttributes().get("topItemPhysicalId"));
                instParams.add(instMap);
            }
            i++;
        }
        try {
            List<HashMap<String, String>> result = new ArrayList();
            if (NullOrEmptyChecker.isNullOrEmpty(existingInstList)) {
                result = mvService.createItemInstance(instParams, dsCall);
            } else {
                result.addAll(existingInstList);
            }
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> setEvolutionForRevise(
            MVImportService mvService,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<HashMap<String, String>> itemList,
            List<HashMap<String, String>> bomList, DsServiceCall dsCall) throws Exception
    {
        List<HashMap<String, String>> setEvolutionParams = new ArrayList();
        if (( !NullOrEmptyChecker.isNullOrEmpty(mvCreationResult) && !mvCreationResult.isEmpty() )
                && ( !NullOrEmptyChecker.isNullOrEmpty(itemList) && !itemList.isEmpty() )
                && ( !NullOrEmptyChecker.isNullOrEmpty(bomList) && !bomList.isEmpty() )) {

            for (int i = 0; i < mvCreationResult.size(); i++) {
                HashMap<String, String> setEvoMap = new HashMap();
                setEvoMap.put("modelPhysicalId", mvCreationResult.get(i).getModelPhysicalId());
                setEvoMap.put("modelName", mvCreationResult.get(i).getTnr().getName());
                setEvoMap.put("modelRevision", mvCreationResult.get(i).getTnr().getRevision());
                setEvoMap.put("topItemPhysicalId", bomList.get(i).get("topItemPhysicalId"));
                setEvoMap.put("relationPhysicalId", bomList.get(i).get("connectionPhysicalId"));
                setEvolutionParams.add(setEvoMap);
            }
        }
        try {
            List<HashMap<String, String>> result = mvService.setEvolution(setEvolutionParams, dsCall, false);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> classifyMV(
            MVImportService mvService,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<MVDataTree> mvRequest,
            DsServiceCall dsCall) throws Exception
    {
        List<HashMap<String, String>> classParams = new ArrayList();
        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {
            HashMap<String, String> classMap = new HashMap();
            classMap.putAll(mvRequest.get(i).getItem().getAttributes());
            classMap.put("mvPhysicalId", res.getMvPhysicalId());
            classParams.add(classMap);
            i++;
        }
        String classId = "";
        try {
            List<HashMap<String, String>> result = mvService.classifyItem(classParams, classId, dsCall);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> UpdateClassifcationMV(
            MVImportService mvService,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<MVDataTree> mvRequest,
            DsServiceCall dsCall, Context context) throws Exception
    {
        List<HashMap<String, String>> classParams = new ArrayList();
        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {
            HashMap<String, String> classMap = new HashMap();
            classMap.putAll(mvRequest.get(i).getItem().getAttributes());
            classMap.put("mvPhysicalId", res.getMvPhysicalId());
            classParams.add(classMap);

            i++;
        }
        try {
            List<HashMap<String, String>> result = mvService.updateClassifyItem(classParams, dsCall, context);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<HashMap<String, String>> UpdateClassifcationMVWithCheck(
            MVImportService mvService,
            List<MVCreateUpdateResponseFormatter> mvCreationResult,
            List<MVDataTree> mvRequest,
            DsServiceCall dsCall, Context context) throws Exception
    {
        List<HashMap<String, String>> classParams = new ArrayList();

        //File file = ResourceUtils.getFile(PropertyReader.getProperty("aton.integration.class.attribute.list"));
        //String content = FileUtils.readFileToString(file);
        String filePath = PropertyReader.getProperty("aton.automation.classattributes.path");
        String content = FileUtils.readFileToString(new File(filePath));

        AtributeMapping attributeMapping = new JSON().deserialize(content, AtributeMapping.class);

        int i = 0;
        for (MVCreateUpdateResponseFormatter res : mvCreationResult) {

            List<String> classAttributes = Arrays.asList(mvRequest.get(i).getItem().getAttributes().get("classAttributes").split(",", -1));
            Set<String> classSet = new HashSet();
            mvRequest.get(i).getItem().getAttributes().entrySet().forEach(entry
                    -> {
                System.out.println(entry.getKey() + " " + entry.getValue());
                if (classAttributes.contains(entry.getKey())) {
                    try {
                        List<BasicClassInfo> classes = attributeMapping.getClassList(entry.getKey());
                        for (BasicClassInfo cls : classes) {
                            classSet.add(cls.getPhysicalId());
                        }
                    }
                    catch (Exception ex) {
                        java.util.logging.Logger.getLogger(MVImportServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            for (String classid : classSet) {
                List<HashMap<String, String>> createParams = new ArrayList();
                if (!mvService.checkClassified(res.getMvPhysicalId(), classid, dsCall)) {
                    HashMap<String, String> createMap = new HashMap();
                    createMap.putAll(mvRequest.get(i).getItem().getAttributes());
                    createMap.put("mvPhysicalId", res.getMvPhysicalId());
                    createParams.add(createMap);
                }

                if (!NullOrEmptyChecker.isNullOrEmpty(createParams)) {
                    try {
                        List<HashMap<String, String>> result = mvService.classifyItem(createParams, classid, dsCall);
                    }
                    catch (Exception e) {
                        MV_CREATION_PROCESSOR.error(e.getMessage());
                        throw e;
                    }
                }
            }

            HashMap<String, String> classMap = new HashMap();
            classMap.putAll(mvRequest.get(i).getItem().getAttributes());
            classMap.put("mvPhysicalId", res.getMvPhysicalId());
            classParams.add(classMap);
            i++;
//            if (!mvService.checkClassified(res.getMvPhysicalId(), dsCall)) {
//                HashMap<String, String> createMap = new HashMap();
//                createMap.putAll(mvRequest.get(i).getItem().getAttributes());
//                createMap.put("mvPhysicalId", res.getMvPhysicalId());
//                createParams.add(createMap);
//            }
        }
        try {
            List<HashMap<String, String>> result = null;
            result = mvService.updateClassifyItem(classParams, dsCall, context);
            return result;
        }
        catch (Exception e) {
            MV_CREATION_PROCESSOR.error(e.getMessage());
            throw e;
        }
    }

    public List<MVDataTree> xml_process(Context context,
                                        List<MVDataTree> mvRequest) throws Exception
    {
        String source = PropertyReader.getProperty("aton.integration.source");
        /*---------------------------------------- ||| Process for create object ||| ----------------------------------------*/

        BusinessObjectUtil businessObjectUtil = new BusinessObjectUtil();
        BusinessObjectOperations businessObjectOperations = new BusinessObjectOperations();
        AtonAttributeBusinessLogic AttributeBusinessLogic = new AtonAttributeBusinessLogic();

        MV_CREATION_PROCESSOR.info(" ------------------ Processing XML -----------");
        List<MVDataTree> dataList = new ArrayList();
        for (MVDataTree req : mvRequest) {
            //
            CommonItemParameters commonItemParemeters = new CommonItemParameters(context, req.getItem(), source, businessObjectUtil, businessObjectOperations, ItemImportMapping.class, AttributeBusinessLogic);
            //MV_CREATION_PROCESSOR.info(" ------------------ before validator -----------");
            validatorFactoryForCreateBean(commonItemParemeters);
            //MV_CREATION_PROCESSOR.info(" ------------------ before mapping -----------");
            HashMap<String, String> attributeMap = XMLAttributeMapperFactoryForCreateBean(commonItemParemeters);
            MVDataTree tree = new MVDataTree();
            MVCreateObjectBean cob = new MVCreateObjectBean();
            cob.setAttributes(attributeMap);
            cob.setTnr(req.getItem().getTnr());
            tree.setItem(cob);
            dataList.add(tree);
            //setXMLAttributeMapperProperties(commonItemTypesAndRelationXMLProcessor, commonItemParemeters);
            //MV_CREATION_PROCESSOR.info(" ------------------ after mapping -----------");
//            commonItemTypesAndRelationXMLProcessor.getDestinationSourceMap().entrySet().forEach(entry
//                    -> {
//                System.out.println(entry.getKey() + " => " + entry.getValue());
//            });
//            System.out.println(Arrays.toString(commonItemTypesAndRelationXMLProcessor.getRunTimeInterfaceList().toArray()));
        }

//        HashMap<String, String> logics = commonItemParemeters.getAttributeBusinessLogic().statusLogic(createObjectBean);
//        logics.entrySet().forEach(entry -> {
//            System.out.println(entry.getKey() + " => " + entry.getValue());
//        });
        //String newlyCreatedOrExistingItemsObjectId = itemCreationProcessFactoryForCreateBean(commonItemParemeters);
        //COMMON_IMPORT_PROCESS_LOGGER.info("Processed ObjectId : " + newlyCreatedOrExistingItemsObjectId);
        //responseMessageFormatterBean.setObjectId(newlyCreatedOrExistingItemsObjectId);
        return dataList;
    }


    protected void validatorFactoryForCreateBean(
            CommonItemParameters commonItemParameters) throws Exception
    {
        ItemValidatorFactory ItemValidatorFactory = new ItemValidatorFactory();
        IItemValidator itemValidator = ItemValidatorFactory.getValidator(PropertyReader.getProperty("aton.integration.source"));
        itemValidator.validateItem(commonItemParameters);

    }

    private HashMap<String, String> XMLAttributeMapperFactoryForCreateBean(
            CommonItemParameters commonItemParameters) throws Exception
    {
        XMLAttributeMapperFactory xmlAttributeMapperFactory = new XMLAttributeMapperFactory();
        IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor = xmlAttributeMapperFactory.getTypeRelationMapperProcessor(PropertyReader.getProperty("aton.integration.source"));
        HashMap<String, String> attributeMap = commonItemTypesAndRelationXMLProcessor.processAttributeXMLMapper(commonItemParameters);

        attributeMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        });

        return attributeMap;
    }

    protected void setXMLAttributeMapperProperties(
            IXmlMapperProcessor commonItemTypesAndRelationXMLProcessor,
            CommonItemParameters commonItemParameters) throws Exception
    {
        //this.destinationSourceMap = commonItemTypesAndRelationXMLProcessor.getDestinationSourceMap();

        List<String> runTimeInterfaceList = commonItemTypesAndRelationXMLProcessor.getRunTimeInterfaceList();
        commonItemParameters.setRunTimeInterfaceList(runTimeInterfaceList);
    }

}
