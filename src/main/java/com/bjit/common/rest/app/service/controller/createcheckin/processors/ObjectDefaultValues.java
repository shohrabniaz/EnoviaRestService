/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.createcheckin.processors;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class ObjectDefaultValues {

    public HashMap<String, String> getObjectUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap;
        switch (createObjectBean.getTnr().getType()) {
            case "CreateAssembly":
                updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
                break;
            case "ProcessContinuousCreateMaterial":
                updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
                break;
            case "Provide":
                updateMap = getObjectDefaultProvideUpdateMap(createObjectBean);
                break;
            case "ProcessContinuous":
                updateMap = getObjectDefaultProcessContinuousUpdateMap(createObjectBean);
                break;
            case "ProcessContinuousProvide":
                updateMap = getObjectDefaultProcessContinuousProvideUpdateMap(createObjectBean);
                break;
            case "VAL_VALComponent":
                updateMap = getObjectDefaultVAL_VALComponentUpdateMap(createObjectBean);
                break;
            case "VAL_VALComponentMaterial":
                updateMap = getObjectDefaultVAL_VALComponentMaterialUpdateMap(createObjectBean);
                break;
            case "Document":
                updateMap = getObjectDefaultDocumentUpdateMap(createObjectBean);
                break;
            case "Product Configuration":
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
            case "CreateMaterial":
                updateMap = getObjectDefaultCreateMaterialUpdateMap(createObjectBean);
                break;
            default:
                updateMap = getObjectDefaultUpdateMap(createObjectBean);
                break;
        }
        return updateMap;
    }

    public HashMap<String, String> getObjectDefaultDocumentUpdateMap(CreateObjectBean createObjectBean) {
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

    public HashMap<String, String> getObjectDefaultCreateAssemblyUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        if (!updateAttributes.containsKey("PLMEntity.V_Name") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_Name"))) {
            updateAttributes.put("PLMEntity.V_Name", createObjectBean.getTnr().getName());
        }

        if (!updateAttributes.containsKey("PLMEntity.PLM_ExternalID") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.PLM_ExternalID"))) {
            updateAttributes.put("PLMEntity.PLM_ExternalID", createObjectBean.getTnr().getName());
        }

        if (!updateAttributes.containsKey("PLMEntity.V_discipline") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_discipline"))) {
            updateAttributes.put("PLMEntity.V_discipline", createObjectBean.getTnr().getType());
        }

        if (!updateAttributes.containsKey("PLMEntity.V_description") || NullOrEmptyChecker.isNullOrEmpty(updateAttributes.get("PLMEntity.V_description"))) {
            updateAttributes.put("PLMEntity.V_description", "");
        }

        return updateAttributes;
    }

    public HashMap<String, String> getItemDefaultUpdateMap(CreateObjectBean createObjectBean) {

        return createObjectBean.getAttributes();
    }

    public HashMap<String, String> getObjectDefaultValValComponentUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        updateAttributes.put("PLMEntity.V_discipline", "Provide");
        updateAttributes.put("MBOM_MBOMReference.MBOM_Type", "Standard");
        updateAttributes = addReleasePurpose(updateAttributes);

        return updateAttributes;
    }

    public HashMap<String, String> getObjectDefaultValValComponentMaterialUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateAttributes;

        if (NullOrEmptyChecker.isNullOrEmpty(createObjectBean.getAttributes())) {
            updateAttributes = new HashMap<>();
        } else {
            updateAttributes = createObjectBean.getAttributes();
        }

        updateAttributes.put("PLMEntity.V_discipline", "ProcessContinuousProvide");
        updateAttributes = addReleasePurpose(updateAttributes);

        return updateAttributes;
    }

    public synchronized HashMap<String, String> addReleasePurpose(HashMap<String, String> updateAttributes) {
        if (NullOrEmptyChecker.isNullOrEmpty(updateAttributes)) {
            updateAttributes = new HashMap<>();
        }

        if (updateAttributes.containsKey("current")) {
            String currentStatus = updateAttributes.get("current");
            if (currentStatus.equalsIgnoreCase("released")) {
                updateAttributes.put("MBOM_MBOMM_.MBOM_Release_Purpose", "Production");
            } else if (currentStatus.equalsIgnoreCase("IN_WORK") || currentStatus.equalsIgnoreCase("OBSOLETE")) {
                updateAttributes.put("MBOM_MBOMM_.MBOM_Release_Purpose", "Planning");
            }
        }

        return updateAttributes;
    }

    public HashMap<String, String> getObjectDefaultProvideUpdateMap(CreateObjectBean createObjectBean) {
        return getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultCreateMaterialUpdateMap(CreateObjectBean createObjectBean) {
        return getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultProcessContinuousUpdateMap(CreateObjectBean createObjectBean) {
        return getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultProcessContinuousProvideUpdateMap(CreateObjectBean createObjectBean) {
        return getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultVAL_VALComponentMaterialUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);

        createObjectBean.setAttributes(updateMap);
        return getObjectDefaultValValComponentMaterialUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultVAL_VALComponentUpdateMap(CreateObjectBean createObjectBean) {
        HashMap<String, String> updateMap = getObjectDefaultCreateAssemblyUpdateMap(createObjectBean);

        createObjectBean.setAttributes(updateMap);
        return getObjectDefaultValValComponentUpdateMap(createObjectBean);
    }

    public HashMap<String, String> getObjectDefaultUpdateMap(CreateObjectBean createObjectBean) {
        return getItemDefaultUpdateMap(createObjectBean);
    }
}
