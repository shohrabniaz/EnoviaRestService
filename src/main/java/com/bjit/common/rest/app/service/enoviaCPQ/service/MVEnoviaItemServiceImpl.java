/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.enoviaCPQ.service;

import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FecthItemAutCycle;
import com.bjit.common.rest.app.service.enoviaCPQ.utilities.FetchEvolution;
import com.bjit.common.rest.app.service.model.mvEnoviaItem.ItemInfo;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ex.integration.model.webservice.Item;
import com.matrixone.apps.domain.util.FrameworkException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author BJIT
 */
@Service
public class MVEnoviaItemServiceImpl implements MVEnoviaItemService {

    private static final org.apache.log4j.Logger MV_ENOVIA_CONTROLLER = org.apache.log4j.Logger.getLogger(MVEnoviaItemServiceImpl.class);

    @Override
    public Map<String, List<String>> getModelByItem(Context context, ItemInfo item, String match) {

        String getParentPhysicalId = "";
        Map<String, List<String>> matchID = new HashMap<>();
        try {

            getParentPhysicalId = getParentPhysicalId(context, item);
            matchID = getModelVersionByItem(context, item, match);
            if (matchID.size() < 1 && !NullOrEmptyChecker.isNullOrEmpty(getParentPhysicalId)) {
                matchID = getModelByParentPhysicalId(context, getParentPhysicalId);
            }

        } catch (Exception ex) {
            Logger.getLogger(MVEnoviaItemServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return matchID;
    }

    @Override
    public Map<String, List<String>> getModelVersionByItem(Context context, ItemInfo item, String match) {

        String getParentPhysicalId = "";
        Map<String, List<String>> matchID = new HashMap<>();
        try {

            getParentPhysicalId = getParentPhysicalId(context, item);

            if (!NullOrEmptyChecker.isNullOrEmpty(getParentPhysicalId)) {
                matchID = getModelVersionByParentPhysicalId(context, getParentPhysicalId, item, match);
            }

        } catch (Exception ex) {
            Logger.getLogger(MVEnoviaItemServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return matchID;
    }

    public Map<String, List<String>> getModelVersionByParentPhysicalId(Context context, String parentPhysicalId, ItemInfo item, String match) {

        FetchEvolution fetchEvolution = new FetchEvolution();
        FecthItemAutCycle fecthItemAutCycle = new FecthItemAutCycle();
        List<String> itemDetails = new ArrayList<>();
        boolean hasEvolution = false;
        boolean connectMV = false;
        Map<String, List<String>> matchID = new HashMap<>();
        try {
            hasEvolution = fetchEvolution.hasEvolution(parentPhysicalId, context);

            if (hasEvolution) {

                if (!NullOrEmptyChecker.isNullOrEmpty(item.getPhysicalid())) {

                    itemDetails = fecthItemAutCycle.getTypeRelationshipId(context, item.getPhysicalid());

                }

                Map<String, List<String>> mvRelid = fetchEvolution.getEvolution(parentPhysicalId, context);

                // connectedInfoToChildId = new ArrayList<>();
                String effectConnectId = "";
                if (match.equalsIgnoreCase("true")) {
                    for (Map.Entry<String, List<String>> entry : mvRelid.entrySet()) {

                        if (!NullOrEmptyChecker.isNullOrEmpty(item.getTnr().getName())) {
                            connectMV = fecthItemAutCycle.getConnectedRelationshipToChildId(context, entry.getKey(), item.getTnr().getName(), item.getTnr().getRevision());
                        } else {
                            connectMV = fecthItemAutCycle.getConnectedRelationshipToChildId(context, entry.getKey(), itemDetails.get(1), itemDetails.get(2));
                        }

                        if (connectMV) {
                            effectConnectId = entry.getKey();
                        }
                    }

                    if (effectConnectId.isEmpty()) {
////
                        Map.Entry<String, List<String>> next = mvRelid.entrySet().iterator().next();
                        effectConnectId = next.getKey();
                    }

////
                    List<String> mvrel = mvRelid.get(effectConnectId);
                    List<String> mvitemInfo = fecthItemAutCycle.getTypeRelationshipId(context, mvrel.get(0));
                    matchID.put(effectConnectId, mvitemInfo);
                } else {
                    for (Map.Entry<String, List<String>> entry : mvRelid.entrySet()) {
                        effectConnectId = entry.getKey();
                        List<String> mvallrel = mvRelid.get(effectConnectId);
                        List<String> mvallitemInfo = fecthItemAutCycle.getTypeRelationshipId(context, mvallrel.get(0));
                        matchID.put(entry.getKey(), mvallitemInfo);
                    }

                }

            }
        } catch (Exception ex) {
            Logger.getLogger(MVEnoviaItemServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return matchID;
    }

    @Override
    public Map<String, List<String>> getModelByParentPhysicalId(Context context, String parentPhysicalId) {

        FetchEvolution fetchEvolution = new FetchEvolution();
        FecthItemAutCycle fecthItemAutCycle = new FecthItemAutCycle();
        Map<String, List<String>> matchID = new HashMap<>();
        boolean hasContext = false;
        try {
            hasContext = fetchEvolution.hasConfigContext(parentPhysicalId, context);

            if (hasContext) {

                List<String> mvRelid = fetchEvolution.fetchConfigContext(parentPhysicalId, context);

                List<String> mvitemInfo = fecthItemAutCycle.getTypeRelationshipId(context, mvRelid.get(0));
//                           
                matchID.put(mvRelid.get(0), mvitemInfo);
                // singleObjResult.put("ItemCode", mvitemInfo.get(1));
                MV_ENOVIA_CONTROLLER.info("Item code value " + mvitemInfo.get(1));

            }
        } catch (Exception ex) {
            Logger.getLogger(MVEnoviaItemServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return matchID;
    }

    public String getParentPhysicalId(Context context, ItemInfo item) {

        FecthItemAutCycle fecthItemAutCycle = new FecthItemAutCycle();
        String physicalId = "";
        String getParentPhysicalId = "";
        try {
            if (NullOrEmptyChecker.isNullOrEmpty(item.getPhysicalid())) {

                physicalId = fecthItemAutCycle.getRelationshipIdByItem(context, item.getTnr().getType(), item.getTnr().getName(), item.getTnr().getRevision());

            } else {
                physicalId = item.getPhysicalid();
            }
            List<String> typeRelationshipToParentId = fecthItemAutCycle.getTypeRelationshipToChildId(context, physicalId);

            if (typeRelationshipToParentId.size() > 0) {
                getParentPhysicalId = fecthItemAutCycle.getRelationshipIdByItem(context, typeRelationshipToParentId.get(3), typeRelationshipToParentId.get(4), typeRelationshipToParentId.get(5));
            }
        } catch (FrameworkException ex) {
            Logger.getLogger(MVEnoviaItemServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getParentPhysicalId;
    }

// String getModelVersionByItem(Context context, Item item);
}
