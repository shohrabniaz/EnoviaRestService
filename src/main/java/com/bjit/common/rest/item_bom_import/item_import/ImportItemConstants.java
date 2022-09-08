/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.item_bom_import.item_import;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author BJIT
 */
public class ImportItemConstants {

    public static final String TYPE_DOCUMENT = "Document";
    public static final String TYPE_COMPONENT = "VAL_VALComponent";
    public static final String TYPE_COMPONENT_MATERIAL = "VAL_VALComponentMaterial";
    public static final String CM_LENGTH = "DELFmiContQuantity_Length";
    public static final String CM_AREA = "DELFmiContQuantity_Area";
    public static final String CM_VOLUME = "DELFmiContQuantity_Volume";
    public static final String CM_MASS = "DELFmiContQuantity_Mass";
    public static final List<String> INVENTORY_UNIT_LIST_VAL_COMPONENT = Arrays.asList("pc");
    public static final List<String> INVENTORY_UNIT_LIST_VAL_COMPONENT_MATERIAL = Arrays.asList("m", "in", "ft", "in2", "m2", "m3", "kg", "lb", "gal", "l", "in3", "g");
}
