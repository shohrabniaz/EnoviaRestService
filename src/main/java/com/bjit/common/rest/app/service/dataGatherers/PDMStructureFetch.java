/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.dataGatherers;

import com.bjit.common.rest.app.service.model.BOMCompareRespnose.PDMItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Tanzir
 */
public class PDMStructureFetch {

    public static HashMap<String, Integer> bomIndexTracker;

    public static PDMItem getPDMStructure(String response) {
        PDMItem pdmRootItem = null;

        try {

            JSONObject first = new JSONObject(response);
            JSONArray second = first.getJSONArray("boms");
            JSONObject fort = second.getJSONObject(0);

            JSONArray third = fort.getJSONArray("bom");
            JSONObject jObject = third.getJSONObject(0);
            JSONArray resultArray = jObject.getJSONArray("bomLines");
            pdmRootItem = new PDMItem();
            pdmRootItem.setType(jObject.get("type").toString());
            pdmRootItem.setName(jObject.get("name").toString());
            pdmRootItem.setRevision(jObject.get("revision").toString());
            pdmRootItem.setQty(jObject.get("qty").toString());
            pdmRootItem.setPosition(jObject.get("position").toString());
            pdmRootItem.setDrawingNumber(jObject.get("drawingNumber").toString());
            String transferToErp = jObject.get("transferToERP").toString();
            pdmRootItem.setTransferToERP(getErpValue(transferToErp));
            pdmRootItem.setTitle(jObject.get("title").toString());
            pdmRootItem = createPDMStructure(resultArray, pdmRootItem);

        } catch (Exception e) {

            System.out.println(" error " + e.getCause());
            return pdmRootItem;
        }
        return pdmRootItem;
    }

    public static PDMItem createPDMStructure(JSONArray bomArray, PDMItem pdmItem) {

        JSONObject inputObject = bomArray.getJSONObject(0);
        JSONObject bomLineChildJson = bomArray.getJSONObject(0);

        List<PDMItem> item = new ArrayList<>();
        for (int in = 0; in < bomArray.length(); in++) {

            PDMItem childItem = new PDMItem();
            JSONObject bomLineJson = bomArray.getJSONObject(in);
            childItem.setType(bomLineJson.get("type").toString());
            childItem.setName(bomLineJson.get("name").toString());
            childItem.setRevision(bomLineJson.get("revision").toString());
            childItem.setPosition(bomLineJson.get("position").toString());
            childItem.setQty(bomLineJson.get("qty").toString());
            childItem.setDrawingNumber(bomLineJson.get("drawingNumber").toString());
            String transferToErp = bomLineJson.get("transferToERP").toString();
            childItem.setTransferToERP(getErpValue(transferToErp));
            childItem.setTitle(bomLineJson.get("title").toString());
            //  childItem.setBomLines(new ArrayList<>());
            item.add(childItem);

            // if (bomLineJson.has("bomLines")) {
            try {

                if (bomLineJson.getJSONArray("bomLines").length() == 0) {

                } else {
                    JSONArray children = bomLineJson.getJSONArray("bomLines");
                    if (children.length() > 0) {
                        createPDMStructure(children, childItem);
                    }

                }
            } catch (Exception ex) {
                return pdmItem;

            }
            //    }
        }
        pdmItem.setBomLines(item);
        return pdmItem;
    }

    private static String getErpValue(String transferToErp) {
        String transferErp = "FALSE";

        String[] value = transferToErp.split("\\|");
        for (int i = 0; i < value.length; i++) {

            if (value[i].equalsIgnoreCase("LN1001") ) {
                transferErp = "TRUE";
                break;
            }
        }

        return transferErp;
    }
}
