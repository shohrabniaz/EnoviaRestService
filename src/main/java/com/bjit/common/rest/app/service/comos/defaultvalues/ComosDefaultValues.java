/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.comos.defaultvalues;

import com.bjit.common.rest.app.service.controller.createcheckin.processors.ObjectDefaultValues;
import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.util.HashMap;
import java.util.Optional;

/**
 *
 * @author BJIT
 */
public class ComosDefaultValues extends ObjectDefaultValues {

    @Override
    public HashMap<String, String> getObjectUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap;
        switch (createObjectBean.getTnr().getType()) {
            case "Plant":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "Mill":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "Unit":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "Sub Unit":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "ProcessContinuousProvide":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "Device Position":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "Schema_Log":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            default:
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
        }
        return updateMap;
    }

    @Override
    public HashMap<String, String> getObjectDefaultUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes = Optional.ofNullable(createObjectBean.getAttributes()).orElseGet(() -> new HashMap<>());

        updateAttributes = getVName(updateAttributes, createObjectBean);
        updateAttributes = getExternalId(updateAttributes, createObjectBean);
        updateAttributes = getDiscipline(updateAttributes, createObjectBean);
        updateAttributes = getDescription(updateAttributes, createObjectBean);
        
        return updateAttributes;
    }

    protected HashMap<String, String> getVName(HashMap<String, String> updateAttributes, CreateObjectBean createObjectBean) {
        if (!updateAttributes.containsKey("PLMEntity.V_Name") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_Name"))) {
            updateAttributes.put("PLMEntity.V_Name", createObjectBean.getTnr().getName());
        }
        return updateAttributes;
    }

    protected HashMap<String, String> getExternalId(HashMap<String, String> updateAttributes, CreateObjectBean createObjectBean) {
        if (!updateAttributes.containsKey("PLMEntity.PLM_ExternalID") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.PLM_ExternalID"))) {
            updateAttributes.put("PLMEntity.PLM_ExternalID", createObjectBean.getTnr().getName());
        }
        return updateAttributes;
    }

    protected HashMap<String, String> getDiscipline(HashMap<String, String> updateAttributes, CreateObjectBean createObjectBean) {
        if (!updateAttributes.containsKey("PLMEntity.V_discipline") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_discipline"))) {
            updateAttributes.put("PLMEntity.V_discipline", PropertyReader.getProperty("import.type.map.Enovia.comos." + createObjectBean.getTnr().getType()));
        }
        return updateAttributes;
    }

    protected HashMap<String, String> getDescription(HashMap<String, String> updateAttributes, CreateObjectBean createObjectBean) {
        if (!updateAttributes.containsKey("PLMEntity.V_description") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_description"))) {
            updateAttributes.put("PLMEntity.V_description", "");
        }
        return updateAttributes;
    }
}
