/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.mapper.mapproject.jsonOutput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import matrix.db.RelationshipWithSelect;
import org.apache.log4j.Logger;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;

/**
 *
 * @author Tahmid
 */

public class TotalPcsCalculation {
    private final HashMap<String, Double> cumulativeCalculationMap;
    private static final Logger TOTALPCS_CALCULATION_LOGGER = Logger.getLogger(TotalPcsCalculation.class);
    public TotalPcsCalculation(){
        cumulativeCalculationMap = new HashMap<>();
    }
    /*
     *  This method implements following logic for PCS calculation:
     *  Cumulative/Total PCS = item's PCS * parent's net quantity 
     *                      + item's total PCS calculated previously in the structure
    */
    public String getCumulativeTotalPcs(String type, RelationshipWithSelect relationshipWithSelect, List<Map<String, String>> resultsMap, String currentItemPcs, String currentItemName){
        if (!type.equals("VAL_VALComponentMaterial")) {
            return "";
        }
        int currentRelLevel = relationshipWithSelect.getLevel();
        if (currentRelLevel == -1) {
            return "";
        }
        currentItemPcs = NullOrEmptyChecker.isNullOrEmpty(currentItemPcs) ? "0.0" : currentItemPcs;
        double currentPcs = Double.parseDouble(currentItemPcs);
        if (currentRelLevel==1) {
            TOTALPCS_CALCULATION_LOGGER.debug("Level 1. No Multiplication required.");
            if (cumulativeCalculationMap.containsKey(currentItemName)) {
                cumulativeCalculationMap.put(currentItemName, cumulativeCalculationMap.get(currentItemName) + currentPcs);
            } else {
                cumulativeCalculationMap.put(currentItemName, currentPcs);
            }
        } else {
            int resultSize = resultsMap.size() - 1;
            TOTALPCS_CALCULATION_LOGGER.debug("Item Level : " + currentRelLevel + ". Looking for parent in result set.");
            for (int i=resultSize;i>0;i--) {
                String level = resultsMap.get(i).get("Level");
                level = NullOrEmptyChecker.isNullOrEmpty(level) ? "-1" : level;
                if (Integer.parseInt(level)<currentRelLevel){
                    String parentQty = resultsMap.get(i).get("Qty");
                    parentQty = NullOrEmptyChecker.isNullOrEmpty(parentQty) ? "1" : parentQty;
                    currentPcs *= Double.parseDouble(parentQty);
                    if (cumulativeCalculationMap.containsKey(currentItemName)) {
                        TOTALPCS_CALCULATION_LOGGER.debug("Parent Level : " + level);
                        double previousPcs = cumulativeCalculationMap.get(currentItemName);
                        currentPcs += previousPcs;
                    }
                    cumulativeCalculationMap.put(currentItemName, currentPcs);
                    break;
                }
            }
        }
        return cumulativeCalculationMap.get(currentItemName) + "";
    }
}
