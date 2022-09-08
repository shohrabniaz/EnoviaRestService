package com.bjit.common.rest.pdm_enovia.formatter;

import com.bjit.common.rest.app.service.model.createobject.CreateObjectBean;
import com.bjit.common.rest.item_bom_import.item_import.AttributeBusinessLogic;
import com.bjit.common.rest.pdm_enovia.result.ResultUtil;
import com.bjit.ewc18x.utils.PropertyReader;
import com.matrixone.json.JSONArray;
import com.matrixone.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import matrix.util.MatrixException;
import org.apache.log4j.Logger;

/**
 *
 * @author Mashuk/BJIT
 */
public abstract class AttributeFormatter {

    private static final Logger ATTRIBUTE_FORMATTER_LOGGER = Logger.getLogger(AttributeFormatter.class);
    
    public HashMap<String, String> propertyMap;
    public CreateObjectBean createObjectBean;
    public List<String> substituteItemList;
    
    public AttributeFormatter(CreateObjectBean createObjectBean, HashMap<String, String> propertyMap) {
        this.createObjectBean = createObjectBean;
        this.propertyMap = propertyMap;
    }
    
    public AttributeFormatter(CreateObjectBean createObjectBean, HashMap<String, String> propertyMap, List<String> substituteItemList) {
        this.createObjectBean = createObjectBean;
        this.propertyMap = propertyMap;
        this.substituteItemList = substituteItemList;
    }
    
    public static String getBundleIdFromTranslationName(String translationName) throws MatrixException {
        ATTRIBUTE_FORMATTER_LOGGER.debug("------------- ||| GTS Bundle Fetch Process Started ||| -------------");
        String bundleId = "";
        try {
            URL url = new URL(PropertyReader.getProperty("gts.bundle.id.service.url") + translationName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException(">>>>> Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            String jsonText = "";
            ATTRIBUTE_FORMATTER_LOGGER.debug(">>>>> GTS BUNDLE SERVICE RESPONSE: ");
            while ((output = br.readLine()) != null) {
                ATTRIBUTE_FORMATTER_LOGGER.debug(output);
                jsonText += output;
            }
            JSONObject json = new JSONObject(jsonText);
            
            JSONArray jArray = json.getJSONArray("data");
            bundleId = jArray.getJSONObject(0).getString("bundle_id");
            ATTRIBUTE_FORMATTER_LOGGER.debug(">>>>> Got Bundle ID: " + bundleId + " For Translation name: " + translationName);
            conn.disconnect();
            
        } catch (MalformedURLException e) {
            ATTRIBUTE_FORMATTER_LOGGER.error(">>>>> Error: " + e);
        } catch (IOException e) {
            ATTRIBUTE_FORMATTER_LOGGER.error(">>>>> Error: " + e);
        } finally {
            ATTRIBUTE_FORMATTER_LOGGER.debug("------------- ||| GTS Bundle Fetch Process Completed ||| -------------\n");
        }
        return bundleId;
    }

    public abstract CreateObjectBean getFormattedObjectBean(ResultUtil resultUtil, AttributeBusinessLogic attributeBusinessLogic) throws IOException;
}
