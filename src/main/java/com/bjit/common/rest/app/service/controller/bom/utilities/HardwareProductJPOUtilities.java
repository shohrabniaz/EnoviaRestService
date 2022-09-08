/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.bom.utilities;

import com.matrixone.apps.domain.util.MapList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 *
 * @author BJIT
 */
@Component
//@RequestScope
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HardwareProductJPOUtilities {

    private static final org.apache.log4j.Logger HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER = org.apache.log4j.Logger.getLogger(HardwareProductJPOUtilities.class);

    List<Map<String, Map<String, String>>> mapList;

    public HardwareProductJPOUtilities() {
        mapList = new ArrayList<>();
    }

    public void prepareProductConfigurationJPOMap(String relationshipId, Map<String, String> attributesMap) {
        Map<String, Map<String, String>> map = new HashMap();
        map.put(relationshipId, Optional.ofNullable(attributesMap).orElse(new HashMap()));
        mapList.add(map);
//
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("-------------------------------------------- Attribute Map --------------------------------------------");
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("-------------------------------------------- Attribute Map --------------------------------------------");
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("Attribute Map : " + map);
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("-------------------------------------------- Attribute Map --------------------------------------------");
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("-------------------------------------------- Attribute Map --------------------------------------------");
    }

    public void callProductConfigJPOMethod(Context context, String parentObjectId, String childObjectId, String marketingName) throws MatrixException {

        removeOrganization(mapList);

        MapList MapList = new MapList(mapList);

        HashMap programMap = new HashMap();

        programMap.put("username", context.getUser());
        programMap.put("parentObjectId", parentObjectId);
        programMap.put("childObjectId", childObjectId);
        programMap.put("marketingName", marketingName);
        programMap.put("selectedOptionMap", MapList);
        programMap.put("derivedFrom", "");

//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("############################################ Program Map ############################################");
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("############################################ Program Map ############################################");
        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("Program Map : " + programMap);
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("############################################ Program Map ############################################");
//        HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info("############################################ Program Map ############################################");

        try {
            String jpoName = "CustomProdConfigImport";
            String methodName = "connectProductConfig";

            String productConfigurationMessage = JPO.invoke(context, jpoName, null, methodName, JPO.packArgs(programMap), String.class);
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info(productConfigurationMessage);
        } catch (MatrixException exp) {
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.error(exp);
            throw exp;
        }
    }

    public void updateProductConfigJPOMethod(Context context, String parentObjectId, String childObjectId, String marketingName) throws Exception {
        MapList MapList = new MapList(mapList);
        HashMap programMap = new HashMap();
        programMap.put("username", context.getUser());
        programMap.put("parentObjectId", parentObjectId);
        programMap.put("childObjectId", childObjectId);
        programMap.put("marketingName", marketingName);
        programMap.put("selectedOptionMap", MapList);
        programMap.put("derivedFrom", "");
        try {
            String jpoName = "CustomProdConfigImport";
            String methodName = "updateProductConfig";
            String productConfigurationMessage = JPO.invoke(context, jpoName, null, methodName, JPO.packArgs(programMap), String.class);
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.info(productConfigurationMessage);
        } catch (MatrixException exp) {
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.error(exp);
            throw exp;
        } catch (Exception exp) {
            HARDWARE_PRODUCT_JPO_UTILITIES_LOGGER.error(exp);
            throw exp;
        }
    }

//    public void removeOrganization(MapList MapList) {
//        MapList.stream().forEach((Map<String, Map<String, String>> mapOfListAttributes) -> {
//            mapOfListAttributes.entrySet().stream().forEach(action -> {
//                Map<String, String> attributeMap = mapOfListAttributes.get(action.getKey());
//                if (attributeMap.containsKey("organization")) {
//                    attributeMap.remove("organization");
//                }
//
//                if (attributeMap.containsKey("project")) {
//                    attributeMap.remove("project");
//                }
//
//            });
//        });
//    }
    public void removeOrganization(List<Map<String, Map<String, String>>> mapList) {
        mapList.stream().forEach((Map<String, Map<String, String>> mapOfListAttributes) -> {
            mapOfListAttributes.entrySet().stream().forEach(action -> {
                Map<String, String> attributeMap = mapOfListAttributes.get(action.getKey());
                if (attributeMap.containsKey("organization")) {
                    attributeMap.remove("organization");
                }

                if (attributeMap.containsKey("project")) {
                    attributeMap.remove("project");
                }
            });
        });
    }
}
