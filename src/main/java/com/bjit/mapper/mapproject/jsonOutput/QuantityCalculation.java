/**
 *
 */
package com.bjit.mapper.mapproject.jsonOutput;

import com.bjit.project_structure.utilities.NullOrEmptyChecker;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author BJIT
 */
public class QuantityCalculation {

    private final HashMap<String, Double> cumulativeCalculationMap;
    private final HashMap<String, Double> tempCumulativeCalculationMap;
    HashMap<String, String> itemLastWidthLengthInStructureMap;
    HashMap<String, HashMap> parentsChildMap;
    HashSet<String> childLevelMap;
    HashMap<String, String> parentWidthLength;
    private final HashMap<String, String> tempCumulativeCalculationWidthLengthMap;

    public QuantityCalculation() {
        cumulativeCalculationMap = new HashMap<>();
        itemLastWidthLengthInStructureMap = new HashMap<>();
        parentsChildMap = new HashMap<>();
        tempCumulativeCalculationMap = new HashMap<>();
        tempCumulativeCalculationWidthLengthMap = new HashMap<>();
        childLevelMap = new HashSet<>();
        parentWidthLength = new HashMap<>();
    }

    /*
     *  This method implements following logic for Net QUantity calculation:
     *  Cumulative quantity = item's net quantity * parent's quantity 
     *                      + item's quantity calculated previously in the structure
     */
    public String getCummulativeNetQty(int level, Map<String, Object> parentData, Map<String, Object> childData, String itemWidthLengthCombo, String itemWidthLengthComboSet) {
        // No Qty calculation for Root item. Also for objects like Drawing, we can send level 0 to avoid Qty calculation.
        if (level == 0) {
            return "";
        }

        String currentItemNetQty = NullOrEmptyChecker.isNullOrEmpty(childData.get("Qty").toString()) ? "0.0" : childData.get("Qty").toString();
        String currentItemName = childData.get("name").toString();
        String parentName = parentData.get("name").toString();
        String key = parentName + "_" + currentItemName;

        if (level > 1) {

            if (cumulativeCalculationMap.containsKey(itemLastWidthLengthInStructureMap.get(parentName))) {
                // if parent qty is 0, we are putting net QTY of item.
                Double temp = (cumulativeCalculationMap.get(itemLastWidthLengthInStructureMap.get(parentName)).equals(0.0)) ? 0.0 : cumulativeCalculationMap.get(itemLastWidthLengthInStructureMap.get(parentName)) * Double.parseDouble(currentItemNetQty);

                // Then if there is a cumulative qty already for the item, we are adding that with the current calculation value
                if (!childLevelMap.contains(key)) {
                    temp = temp + ((cumulativeCalculationMap.containsKey(itemWidthLengthCombo)) ? cumulativeCalculationMap.get(itemWidthLengthCombo) : 0.0);
                    childLevelMap.add(key);

                } else {

                    List<String> comboSet = new ArrayList<>();
                    tempCumulativeCalculationWidthLengthMap.forEach((keyValue, value) -> {
                        if (value.equals(parentName)) {
                            comboSet.add(keyValue);
                        }
                    });

                    if (comboSet.size() > 1) {
                        for (int c = 0; c < comboSet.size(); c++) {
                            String kyValue = comboSet.get(c);
                            if (!kyValue.equals(itemLastWidthLengthInStructureMap.get(parentName))) {
                                if (cumulativeCalculationMap.containsKey(kyValue)) {
                                    temp = temp + (cumulativeCalculationMap.get(kyValue) * Double.parseDouble(currentItemNetQty));
                                }
                            }
                        }
                    } else {
                        //   temp = temp + ((tempCumulativeCalculationMap.containsKey(itemWidthLengthComboSet)) ? tempCumulativeCalculationMap.get(itemWidthLengthComboSet) : 0.0);
                    }
                }
                cumulativeCalculationMap.put(itemWidthLengthCombo, temp);
                tempCumulativeCalculationMap.put(itemWidthLengthComboSet, temp);
                tempCumulativeCalculationWidthLengthMap.put(itemWidthLengthCombo, currentItemName);
                itemLastWidthLengthInStructureMap.put(currentItemName, itemWidthLengthCombo);
            }

        } else {
            Double currentQty = Double.parseDouble(currentItemNetQty);
            itemLastWidthLengthInStructureMap.put(currentItemName, itemWidthLengthCombo);

            if (cumulativeCalculationMap.containsKey(itemWidthLengthCombo)) {
                currentQty = currentQty + (cumulativeCalculationMap.get(itemWidthLengthCombo));
                cumulativeCalculationMap.put(itemWidthLengthCombo, currentQty);
                tempCumulativeCalculationMap.put(itemWidthLengthComboSet, currentQty);
            } else {
                cumulativeCalculationMap.put(itemWidthLengthCombo, currentQty);
                tempCumulativeCalculationMap.put(itemWidthLengthComboSet, currentQty);

            }
            childLevelMap.add(key);
            tempCumulativeCalculationWidthLengthMap.put(itemWidthLengthCombo, currentItemName);

        }

        return cumulativeCalculationMap.get(itemWidthLengthCombo)
                .toString();
    }
}
