package com.bjit.common.rest.app.service.dataGatherers;

import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.common.rest.app.service.model.BOMCompareRespnose.EnoviaItem;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Tahmid
 */
public class EnoviaPDMStructureFetch {

    private static final Logger ENV_STRUCTURE_LOGGER = Logger.getLogger(EnoviaPDMStructureFetch.class);
    public static HashMap<String, Integer> bomIndexTracker;



    private static String roundQuantityWithPrecision(String value) {
        if (!NullOrEmptyChecker.isNullOrEmpty(value)) {
            try {
                double convertedValue = Double.parseDouble(value);
                Integer precisionPoint = Integer.parseInt(PropertyReader.getProperty("bom.compare.pdm.enovia.quantity.precision"));
                Double roundedValue = new BigDecimal(convertedValue).setScale(precisionPoint, RoundingMode.HALF_UP).doubleValue();
                return roundedValue.toString();
            } catch (NumberFormatException e) {
                ENV_STRUCTURE_LOGGER.error("Error occured during rounding quantity value: " + e.getMessage());
                return value;
            } catch (Exception ex) {
                ENV_STRUCTURE_LOGGER.error("Error occured during rounding quantity value: " + ex.getMessage());
                return value;
            }
        }
        return value;
    }

    private static synchronized String removeTitleValuePrefix(String title) {
        String formattedTitle = "";
        if (!NullOrEmptyChecker.isNullOrEmpty(title)) {
            formattedTitle = title.substring(title.indexOf(":") + 2);
        }
        return formattedTitle;
    }

    private static synchronized String getPropertyVal(String property, JsonObject jsonObject) {
        String value = "";
        if (jsonObject == null || jsonObject.isJsonNull()) {
            return value;
        }
        JsonElement jsonElement = jsonObject.get(property);
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return value;
        }
        value = jsonElement.getAsString();
        return value;
    }

    public  static Object getEnoviaStructure(String type, String name, String rev, String expandLevel, String attributesAsString, String drawingType, boolean pdm) {
        EnoviaItem enoviaRootItem = null;
        try {
            String url = PropertyReader.getProperty("enovia.bom.export.service.url")
                    + "type=" + type
                    + "&name=" + name
                    + "&rev=" + rev
                    + "&expandLevel=" + expandLevel
                    + "&format=json&requester=VALCON"
                    + "&attrs=" + attributesAsString
                    + "&drawingType=" + drawingType;
            ENV_STRUCTURE_LOGGER.info("BOMExport URL for BOM Comparison: " + url);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders bomExportHeaders = new HttpHeaders();
            bomExportHeaders.setContentType(MediaType.APPLICATION_JSON);
            bomExportHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ContextPasswordSecurity contextPassword = new ContextPasswordSecurity();
            bomExportHeaders.set("user", contextPassword.decryptPassword(PropertyReader.getProperty("context.name")));
            bomExportHeaders.set("pass", contextPassword.decryptPassword(PropertyReader.getProperty("context.pass")));
            HttpEntity request = new HttpEntity(bomExportHeaders);
            ResponseEntity<String> enoviaBOMResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class,
                    1
            );

            if (enoviaBOMResponse.getStatusCode() == HttpStatus.OK) {
                ENV_STRUCTURE_LOGGER.info("BOMExport Rest Service Response: " + enoviaBOMResponse.getBody());
                JsonElement jelement = new JsonParser().parse(enoviaBOMResponse.getBody());
                JsonObject jObject = jelement.getAsJsonObject();
                jObject = jObject.getAsJsonObject("data");
                JsonArray resultArray = jObject.getAsJsonArray("results");
                bomIndexTracker = new HashMap<>();
                for (int i = 0; i < resultArray.size(); i++) {
                    JsonObject bomObject = resultArray.get(i).getAsJsonObject();
                    String key = bomObject.get("Type").getAsString() + "_"
                            + bomObject.get("name").getAsString() + "_"
                            + bomObject.get("revision").getAsString();
                    bomIndexTracker.put(key, i);
                }
                enoviaRootItem = new EnoviaItem();
                JsonObject rootNodeJsonObj = resultArray.get(0).getAsJsonObject();
                enoviaRootItem.setType(getPropertyVal("Type", rootNodeJsonObj));
                enoviaRootItem.setName(getPropertyVal("name", rootNodeJsonObj));
                enoviaRootItem.setRevision(getPropertyVal("revision", rootNodeJsonObj));
                enoviaRootItem.setLevel(getPropertyVal("Level", rootNodeJsonObj));
                enoviaRootItem.setItemType(getPropertyVal("ERP Item Type", rootNodeJsonObj));
                enoviaRootItem.setReleasePurpose(getPropertyVal("Release purpose", rootNodeJsonObj));
                enoviaRootItem.setDrawingNumber(getPropertyVal("Drawing Number", rootNodeJsonObj));
                enoviaRootItem.setTitle(removeTitleValuePrefix(getPropertyVal("Title", rootNodeJsonObj)));
                enoviaRootItem.setPdmRevision(getPropertyVal("PDM revision", rootNodeJsonObj));
                enoviaRootItem.setPhysicalid(getPropertyVal("physicalid", rootNodeJsonObj));
                enoviaRootItem.setShortName(getPropertyVal("Short Name", rootNodeJsonObj));
                enoviaRootItem.setWeight(getPropertyVal("Weight", rootNodeJsonObj));
                enoviaRootItem.setSize(getPropertyVal("Size", rootNodeJsonObj));
                enoviaRootItem.setTechnicalDesignation(getPropertyVal("Technical Designation", rootNodeJsonObj));
                enoviaRootItem.setMaterial(getPropertyVal("Material", rootNodeJsonObj));
                enoviaRootItem.setUnit(getPropertyVal("Unit", rootNodeJsonObj));
                enoviaRootItem.setStandard(getPropertyVal("Standard", rootNodeJsonObj));
                enoviaRootItem.setDistributionList(getPropertyVal("DistributionList", rootNodeJsonObj));
                enoviaRootItem.setLength(getPropertyVal("Length", rootNodeJsonObj));
                enoviaRootItem.setWidth(getPropertyVal("Width", rootNodeJsonObj));
                enoviaRootItem.setStatus(getPropertyVal("Status", rootNodeJsonObj));
                enoviaRootItem.setSourceItem(getPropertyVal("Source Item", rootNodeJsonObj));
                enoviaRootItem.setTransferToERP(getPropertyVal("Transfer To ERP", rootNodeJsonObj));
                enoviaRootItem.setItemCommonText(getPropertyVal("item common text", rootNodeJsonObj));
                enoviaRootItem.setItemPurchasingText(getPropertyVal("item purchasing text", rootNodeJsonObj));
                enoviaRootItem.setBomCommonText(getPropertyVal("bom common text", rootNodeJsonObj));
                enoviaRootItem.setBomPurchasingText(getPropertyVal("bom purchasing text", rootNodeJsonObj));
                enoviaRootItem.setBomManufacturingText(getPropertyVal("bom manufacturing text", rootNodeJsonObj));
                enoviaRootItem.setMastership(getPropertyVal("Mastership", rootNodeJsonObj));
                enoviaRootItem.setQty("");
                enoviaRootItem.setPosition("");
                enoviaRootItem = createEnoviaStructure(resultArray, enoviaRootItem, pdm);

            } else {
                ENV_STRUCTURE_LOGGER.error("BOMExport Service ERROR Response: " + enoviaBOMResponse.toString());
                return enoviaRootItem;
            }
        } catch (Exception e) {
            ENV_STRUCTURE_LOGGER.error(e.getCause());
            return e.getMessage();
        }
        return enoviaRootItem;
    }

    public static EnoviaItem createEnoviaStructure(JsonArray bomArray, EnoviaItem enoviaItem, boolean pdm) {

        String key = enoviaItem.getType() + "_" + enoviaItem.getName() + "_" + enoviaItem.getRevision();
        if (bomIndexTracker.get(key) != null) {
            int index = bomIndexTracker.get(key);
            JsonObject itemNode = bomArray.get(index).getAsJsonObject();
            enoviaItem.setType(getPropertyVal("Type", itemNode));
            enoviaItem.setName(getPropertyVal("name", itemNode));
            enoviaItem.setRevision(getPropertyVal("revision", itemNode));
            enoviaItem.setLevel(getPropertyVal("Level", itemNode));
            enoviaItem.setItemType(getPropertyVal("ERP Item Type", itemNode));
            enoviaItem.setReleasePurpose(getPropertyVal("Release purpose", itemNode));
            enoviaItem.setDrawingNumber(getPropertyVal("Drawing Number", itemNode));
            enoviaItem.setTitle(removeTitleValuePrefix(getPropertyVal("Title", itemNode)));
            enoviaItem.setPdmRevision(getPropertyVal("PDM revision", itemNode));
            enoviaItem.setPhysicalid(getPropertyVal("physicalid", itemNode));
            enoviaItem.setShortName(getPropertyVal("Short Name", itemNode));
            enoviaItem.setWeight(getPropertyVal("Weight", itemNode));
            enoviaItem.setSize(getPropertyVal("Size", itemNode));
            enoviaItem.setTechnicalDesignation(getPropertyVal("Technical Designation", itemNode));
            enoviaItem.setMaterial(getPropertyVal("Material", itemNode));
            enoviaItem.setUnit(getPropertyVal("Unit", itemNode));
            enoviaItem.setStandard(getPropertyVal("Standard", itemNode));
            enoviaItem.setDistributionList(getPropertyVal("DistributionList", itemNode));
            enoviaItem.setLength(getPropertyVal("Length", itemNode));
            enoviaItem.setWidth(getPropertyVal("Width", itemNode));
            enoviaItem.setStatus(getPropertyVal("Status", itemNode));
            enoviaItem.setSourceItem(getPropertyVal("Source Item", itemNode));
            enoviaItem.setTransferToERP(getPropertyVal("Transfer To ERP", itemNode));
            enoviaItem.setItemCommonText(getPropertyVal("item common text", itemNode));
            enoviaItem.setItemPurchasingText(getPropertyVal("item purchasing text", itemNode));
            enoviaItem.setBomCommonText(getPropertyVal("bom common text", itemNode));
            enoviaItem.setBomPurchasingText(getPropertyVal("bom purchasing text", itemNode));
            enoviaItem.setBomManufacturingText(getPropertyVal("bom manufacturing text", itemNode));
            enoviaItem.setMastership(getPropertyVal("Mastership", itemNode));

            enoviaItem.setBomLines(new ArrayList<>());
            itemNode.getAsJsonArray("bomLines").forEach(item -> {
                EnoviaItem childItem = new EnoviaItem();
                JsonObject bomLineJson = item.getAsJsonObject();
                childItem.setType(getPropertyVal("Type", bomLineJson));
                childItem.setName(getPropertyVal("name", bomLineJson));
                childItem.setRevision(getPropertyVal("revision", bomLineJson));
                childItem.setLevel(getPropertyVal("Level", bomLineJson));
                childItem.setItemType(getPropertyVal("ERP Item Type", bomLineJson));
                childItem.setReleasePurpose(getPropertyVal("Release purpose", bomLineJson));
                childItem.setDrawingNumber(getPropertyVal("Drawing Number", bomLineJson));
                childItem.setTitle(removeTitleValuePrefix(getPropertyVal("Title", bomLineJson)));
                childItem.setPdmRevision(getPropertyVal("PDM revision", bomLineJson));
                childItem.setPhysicalid(getPropertyVal("physicalid", bomLineJson));
                childItem.setShortName(getPropertyVal("Short Name", bomLineJson));
                childItem.setWeight(getPropertyVal("Weight", bomLineJson));
                childItem.setSize(getPropertyVal("Size", bomLineJson));
                childItem.setTechnicalDesignation(getPropertyVal("Technical Designation", bomLineJson));
                childItem.setMaterial(getPropertyVal("Material", bomLineJson));
                childItem.setUnit(getPropertyVal("Unit", bomLineJson));
                childItem.setStandard(getPropertyVal("Standard", bomLineJson));
                childItem.setDistributionList(getPropertyVal("DistributionList", bomLineJson));
                childItem.setLength(getPropertyVal("Length", bomLineJson));
                childItem.setWidth(getPropertyVal("Width", bomLineJson));
                childItem.setStatus(getPropertyVal("Status", bomLineJson));
                childItem.setSourceItem(getPropertyVal("Source Item", bomLineJson));
                childItem.setTransferToERP(getPropertyVal("Transfer To ERP", bomLineJson));
                childItem.setItemCommonText(getPropertyVal("item common text", bomLineJson));
                childItem.setItemPurchasingText(getPropertyVal("item purchasing text", bomLineJson));
                childItem.setBomCommonText(getPropertyVal("bom common text", bomLineJson));
                childItem.setBomPurchasingText(getPropertyVal("bom purchasing text", bomLineJson));
                childItem.setBomManufacturingText(getPropertyVal("bom manufacturing text", bomLineJson));
                childItem.setMastership(getPropertyVal("Mastership", bomLineJson));
                childItem.setPosition(getPropertyVal("Position", bomLineJson));
                childItem.setQty(getPropertyVal("Qty", bomLineJson));
                String formattedQty = roundQuantityWithPrecision(childItem.getQty());
                childItem.setQty(formattedQty);

                childItem.setBomLines(new ArrayList<>());
                enoviaItem.getBomLines().add(childItem);
                createEnoviaStructure(bomArray, childItem, pdm);
            });
        }

        return enoviaItem;
    }
}
