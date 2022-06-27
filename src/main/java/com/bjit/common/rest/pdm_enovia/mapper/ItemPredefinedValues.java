/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.pdm_enovia.mapper;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class ItemPredefinedValues {

    public static HashMap<String, String> getObjectUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap;
        switch (createObjectBean.getTnr().getType()) {
            case "VAL_VALComponent":
                updateMap = getObjectDefaultVAL_VALComponentUpdateMap(createObjectBean);
                break;
            case "VAL_VALComponentMaterial":
                updateMap = getObjectDefaultVAL_VALComponentMaterialUpdateMap(createObjectBean);
                break;
            case "Document":
                updateMap = getObjectDefaultDocumentUpdateMap(createObjectBean);
                break;
            default:
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
        }
        return updateMap;
    }

    public static HashMap<String, String> getObjectDefaultDocumentUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        if (!updateAttributes.containsKey("Title")) {
            updateAttributes.put("Title", createObjectBean.getTnr().getName());
        }

        return updateAttributes;
    }

    public static HashMap<String, String> getObjectDefaultCreateAssemblyUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

//        if (!updateAttributes.containsKey("PLMEntity.V_Name") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_Name"))) {
//            updateAttributes.put("PLMEntity.V_Name", createObjectBean.getTnr().getName());
//        }
//
//        if (!updateAttributes.containsKey("PLMEntity.PLM_ExternalID") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.PLM_ExternalID"))) {
//            updateAttributes.put("PLMEntity.PLM_ExternalID", createObjectBean.getTnr().getName());
//        }
//
//        if (!updateAttributes.containsKey("PLMEntity.V_discipline") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_discipline"))) {
//            updateAttributes.put("PLMEntity.V_discipline", createObjectBean.getTnr().getType());
//        }
//
//        if (!updateAttributes.containsKey("PLMEntity.V_description") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_description"))) {
//            updateAttributes.put("PLMEntity.V_description", "");
//        }

        return updateAttributes;
    }

    public static HashMap<String, String> getObjectDefaultValValComponentUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        updateAttributes = addVALReleasePurpose(updateAttributes);

        return updateAttributes;
    }

    public static HashMap<String, String> getObjectDefaultValValComponentMaterialUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }
        
        updateAttributes = addVALMaterialReleasePurpose(updateAttributes);

        return updateAttributes;
    }
    
    public static synchronized HashMap<String, String> addVALReleasePurpose(HashMap<String, String> updateAttributes){
        if(NullOrEmptyChecker.isNullOrEmpty(updateAttributes)){
            updateAttributes = new HashMap<>();
        }
        
        if(updateAttributes.containsKey("current")){
            String currentStatus = updateAttributes.get("current");
            if(currentStatus.equalsIgnoreCase("RELEASED")){
                updateAttributes.put(PropertyReader.getProperty("import.item.val.component.attr.release.purpose"), "Production");
            }
            else if(currentStatus.equalsIgnoreCase("IN_WORK") || currentStatus.equalsIgnoreCase("OBSOLETE")){
                updateAttributes.put(PropertyReader.getProperty("import.item.val.component.attr.release.purpose"), "Planning");
            }
        }
        
        return updateAttributes;
    }
    
    public static synchronized HashMap<String, String> addVALMaterialReleasePurpose(HashMap<String, String> updateAttributes){
        if(NullOrEmptyChecker.isNullOrEmpty(updateAttributes)){
            updateAttributes = new HashMap<>();
        }
        
        if(updateAttributes.containsKey("current")){
            String currentStatus = updateAttributes.get("current");
            if(currentStatus.equalsIgnoreCase("RELEASED")){
                updateAttributes.put(PropertyReader.getProperty("import.item.val.component.material.attr.release.purpose"), "Production");
            }
            else if(currentStatus.equalsIgnoreCase("IN_WORK") || currentStatus.equalsIgnoreCase("OBSOLETE")){
                updateAttributes.put(PropertyReader.getProperty("import.item.val.component.material.attr.release.purpose"), "Planning");
            }
        }
        
        return updateAttributes;
    }

    public static HashMap<String, String> getObjectDefaultVAL_VALComponentMaterialUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
        
        createObjectBean.setAttributes(updateMap);
        return getObjectDefaultValValComponentMaterialUpdateMap(createObjectBean);
    }

    public static HashMap<String, String> getObjectDefaultVAL_VALComponentUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
        
        createObjectBean.setAttributes(updateMap);
        return getObjectDefaultValValComponentUpdateMap(createObjectBean);
    }

    public static HashMap<String, String> getObjectDefaultUpdateMap(CreateObjectBean createObjectBean) {
        return getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
    }
}
