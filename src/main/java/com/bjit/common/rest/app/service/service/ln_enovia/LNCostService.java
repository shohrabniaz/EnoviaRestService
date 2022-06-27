package com.bjit.common.rest.app.service.service.ln_enovia;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.ln_enovia.LNCostData;
import com.bjit.common.rest.app.service.utilities.JSON;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.ln_enovia.LNCommonUtil;
import com.bjit.common.rest.app.service.utilities.ln_enovia.OAuthManager;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.bjit.mapper.mapproject.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author BJIT
 */
@Service("lnCostService")
public class LNCostService {

    private static final Logger LN_COST_SERVICE_LOGGER = Logger.getLogger(LNCostService.class);

    public List<LNCostData> getCostData(String startAt, String itemId, String serviceType) throws MalformedURLException, ExecutionException, InterruptedException, IOException, Exception {
        String accessToken = LNCommonUtil.getCostDataAccessToken();
        String response = "";
        String uri = "";
        LN_COST_SERVICE_LOGGER.info("Getting LN Cost Data");
        if (serviceType.equalsIgnoreCase("byItem")) {
            uri = LNCommonUtil.addQueryParamToURLForItem(PropertyReader.getProperty("ln.cost.service.url"), startAt, itemId);
        } else {
            uri = LNCommonUtil.addQueryParamToURL(PropertyReader.getProperty("ln.cost.service.url"), startAt);
        }
        URL url = new URL(uri);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        int responseCode = conn.getResponseCode();

        if (responseCode == HTTPResponse.SC_OK) {
            StringBuilder responseBuilder;
            try ( BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                responseBuilder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }
                in.close();
            }
            response = responseBuilder.toString();
            Type collectionType = new TypeToken<List<LNCostData>>() {
            }.getType();
            List<LNCostData> lnCostDataList = (List<LNCostData>) new Gson().fromJson(response, collectionType);
            return lnCostDataList;
        } else if (responseCode == HTTPResponse.SC_SERVER_ERROR) {
            return new ArrayList<>();
        } else {
            StringBuilder responseBuilder;
            try ( BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String inputLine;
                responseBuilder = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }
                in.close();
            }
            response = responseBuilder.toString();
            throw new Exception(response);
        }
    }

    public  List<String>  updateCostData(List<LNCostData> costData) {
        Context context = null;
        LNCommonUtil lnCommonUtil = new LNCommonUtil();
        List<String> successResponse = new ArrayList<>();
        List<String> errorResponse = new ArrayList<>();
        String response = "";

        try {
            CreateContext createContext = new CreateContext();
            context = createContext.getAdminContext();
            if (!context.isConnected()) {
                throw new Exception(Constants.CONTEXT_EXCEPTION);
            }
            for (int i = 0; i < costData.size(); i++) {
                LN_COST_SERVICE_LOGGER.info("Cost Data Response ---- " + costData.get(i).getItem() + costData.get(i).getCost() + costData.get(i).getProductType() + costData.get(i).getTimestamp());
                LN_COST_SERVICE_LOGGER.info("Updating LN Data Data : " + costData.get(i).getItem());
                response = lnCommonUtil.updateCostData(context, costData.get(i));
               if (!response.equalsIgnoreCase("Error") && !NullOrEmptyChecker.isNullOrEmpty(response)) {
                    successResponse.add(response);
                }

            }

        } catch (Exception exp) {
            LN_COST_SERVICE_LOGGER.error(exp.getMessage());
            response = "Error";
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
        return successResponse;
    }
}
