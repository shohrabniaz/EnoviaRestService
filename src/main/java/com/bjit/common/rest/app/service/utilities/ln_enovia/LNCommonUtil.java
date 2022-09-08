package com.bjit.common.rest.app.service.utilities.ln_enovia;

import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.model.common.CommonItemSearchBean;
import com.bjit.common.rest.app.service.model.ln_enovia.LNCostData;
import com.bjit.common.rest.app.service.search.SearchService;
import com.bjit.common.rest.app.service.search.SearchServiceImpl;
import com.bjit.common.rest.app.service.utilities.CommonSearchUtil;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.common.rest.app.service.utilities.PropertiesFileUtil;
import com.bjit.ewc18x.utils.MqlQueries;
import com.bjit.ewc18x.utils.PropertyReader;
import com.bjit.mapper.mapproject.jsonOutput.Items;
import com.bjit.mapper.mapproject.util.Constants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import matrix.db.Context;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author BJIT
 */
public class LNCommonUtil {

    private static final Logger LN_COMMON_UTIL_LOGGER = Logger.getLogger(LNCommonUtil.class);

    public static synchronized String getCostDataAccessToken() throws MalformedURLException, ExecutionException, InterruptedException {
        // Get Access Token from Azure AD OAuth
        LN_COMMON_UTIL_LOGGER.info("Getting Access Token");
        Instant startTime = Instant.now();
        OAuthManager oAuthManager = new OAuthManager();
        String accessToken = oAuthManager.getAccessToken();
        Instant endTime = Instant.now();
        LN_COMMON_UTIL_LOGGER.info("Access Token Acquired. Taken time: " + Duration.between(startTime, endTime));
        return accessToken;
    }

    public static synchronized String addQueryParamToURL(String url, String startAt) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url)
                .append("env=").append(PropertyReader.getProperty("ln.cost.service.env")).append("&")
                .append("product_type=").append(PropertyReader.getProperty("ln.cost.service.product.type")).append("&")
                .append("timestamp=").append(URLEncoder.encode(startAt, "utf-8")).append("&")
                .append("subscription-key=").append(PropertyReader.getProperty("ln.cost.service.subscription.key"));
        return sb.toString();
    }

    public static synchronized String addQueryParamToURLForItem(String url, String startAt, String itemId) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(url)
                .append("env=").append(PropertyReader.getProperty("ln.cost.service.env")).append("&")
                .append("product_type=").append(PropertyReader.getProperty("ln.cost.service.product.type")).append("&")
                .append("timestamp=").append(URLEncoder.encode(startAt, "utf-8")).append("&")
                .append("subscription-key=").append(PropertyReader.getProperty("ln.cost.service.subscription.key")).append("&")
                .append("itemid=").append(itemId);
        return sb.toString();
    }

    public static synchronized Date getPreviousDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static synchronized String getCurrentTimeForCostData() {
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(PropertyReader.getProperty("ln.cost.service.date.pattern"));
        String currentDateAsString = formatter.format(current);
        return currentDateAsString;
    }

    public static synchronized String getCostDataUpdateStartDate() throws Exception {
        String date;
        date = PropertiesFileUtil.readProperty(PropertyReader.getProperty("gts.scheduler.properties.filename"),
                PropertyReader.getProperty("ln.cost.data.sucessful.update.date.key"),
                PropertyReader.getProperty("app.integration.scheduler.information"));

        if (NullOrEmptyChecker.isNullOrEmpty(date)) {
            date = PropertiesFileUtil.readProperty(PropertyReader.getProperty("gts.scheduler.properties.filename"),
                    PropertyReader.getProperty("ln.cost.data.failed.update.date.key"),
                    PropertyReader.getProperty("app.integration.scheduler.information"));
        }

        if (NullOrEmptyChecker.isNullOrEmpty(date)) {
            Date previousDate = LNCommonUtil.getPreviousDate();
            SimpleDateFormat formatter = new SimpleDateFormat(PropertyReader.getProperty("ln.cost.service.date.pattern"));
            date = formatter.format(previousDate);
        }
        return date;
    }

    public static synchronized void updateCostUpdateDate(String propertiesKey) throws Exception {
        PropertiesFileUtil.updateProperty(PropertyReader.getProperty("gts.scheduler.properties.filename"),
                propertiesKey,
                LNCommonUtil.getCurrentTimeForCostData(),
                PropertyReader.getProperty("app.integration.scheduler.information"));
    }

    public String updateCostData(Context context, LNCostData costData) {
        MqlQueries mqlQuery = new MqlQueries();
        String response = "";
        CommonItemSearchBean commonItemSearchBean = new CommonItemSearchBean();
        CommonSearchUtil commonSearchUtil = new CommonSearchUtil();
        List<Map<String, String>> responseFromTNR = new ArrayList<>();
        String type = "costUpdate";
        commonItemSearchBean.setName(costData.getItem());
        responseFromTNR = commonSearchUtil.getResponseFromTNR(context, commonItemSearchBean, type);
        if (responseFromTNR.size() < 1) {
            LN_COMMON_UTIL_LOGGER.info("No Item Found in PLM");
            response = "Error";
        } else {
            StringBuilder queryBuilder = new StringBuilder();
            for (int i = 0; i < responseFromTNR.size(); i++) {
                queryBuilder = queryBuilder.append("modify bus ").append(responseFromTNR.get(i).get("objectid")).append(" ").append(PropertyReader.getProperty("cost.data.update.attribute")).append(" '").append(costData.getCost()).append("'");
                String itemName = responseFromTNR.get(i).get("name");
                String query = queryBuilder.toString();
                LN_COMMON_UTIL_LOGGER.info("Update Query " + query);
                String queryResult = null;
                try {
                    queryResult = MqlUtil.mqlCommand(context, query);
                    if (NullOrEmptyChecker.isNullOrEmpty(queryResult)) {
                        LN_COMMON_UTIL_LOGGER.info("Successfully Executed Updated Query " + responseFromTNR.get(i).get("id"));
                        response = itemName;
                    } else {
                        LN_COMMON_UTIL_LOGGER.info("Failed Executed Updated Query");
                        response = "Error";
                    }
                } catch (FrameworkException ex) {
                    LN_COMMON_UTIL_LOGGER.error("Error generated query: " + mqlQuery);
                    LN_COMMON_UTIL_LOGGER.error(ex.getMessage());
                    response = "Error";

                }
            }
        }
        return response;
    }

}
