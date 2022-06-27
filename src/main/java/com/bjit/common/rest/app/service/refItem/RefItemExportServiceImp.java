/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.refItem;

import com.bjit.common.rest.app.service.refItems.utilities.RefItemExportUtil;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.ewc18x.utils.PropertyReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author Suvonkar Kundu
 */
public class RefItemExportServiceImp implements RefItemExportService {

    RefItemExportUtil refItemExportUtil = new RefItemExportUtil();
    private static final org.apache.log4j.Logger REF_Item_Export_Service_LOGGER = org.apache.log4j.Logger.getLogger(RefItemExportServiceImp.class);

    /**
     *get JsonArray Data.
     *
     * @param code is reference item code
     * @param codestatus is reference item code status
     * @return prepare final response
     */
    @Override
    public String getJsonArrayData(String code, String codeStatus) {
        JSONArray jsonArray = new JSONArray();
        String url = PropertyReader.getProperty("ref.export.item.url");
        try {
            CommonPropertyReader commonProperty = new CommonPropertyReader();
            String getServiceData = refItemExportUtil.executeService(code, url);
            jsonArray = new JSONArray(getServiceData);
            String[] refKeys = commonProperty.getPropertyValue("ref.items.export.response.keys").split("\\|");
            jsonArray = refItemExportUtil.filterJsonobjectsOfJsonarray(jsonArray, refKeys);
            if (codeStatus != null) {
                jsonArray = refItemExportUtil.getJsonArrayByCodeStatus(jsonArray, codeStatus);

            }
        } catch (Exception ex) {
            REF_Item_Export_Service_LOGGER.debug(ex);
        }

        return jsonArray.toString();
    }

    /**
     * convert JSON to XML
     *
     * @param jsonString contains final response data
     * @return XML response.
     */
    @Override
    public String convertJsonToXml(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        return XML.toString(jsonObject, "root");
    }

}
