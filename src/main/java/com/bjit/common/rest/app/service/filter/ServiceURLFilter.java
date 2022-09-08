/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bjit.common.rest.app.service.context.CreateContext;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationProcess;
import com.bjit.common.rest.app.service.controller.authentication.AuthenticationUserModel;
import com.bjit.common.rest.app.service.controller.authentication.token.HMAC.WebToken;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.payload.common_response.Status;
import com.bjit.common.rest.app.service.utilities.CommonPropertyReader;
import com.bjit.common.rest.app.service.utilities.DateTimeUtils;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import com.bjit.ewc18x.utils.PropertyReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.log4j.MDC;
import org.apache.log4j.Priority;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 *
 * @author Omour Faruq
 */
public class ServiceURLFilter implements Filter {

    private static final org.apache.log4j.Logger URL_FILTER_LOGGER = org.apache.log4j.Logger.getLogger(ServiceURLFilter.class);
    public static Map<String, String> BY_PASS_URLS_FROM_FILTER;
    static List<String> excludeLogs;
    public static String URL_PATTERN = "url.service.public.access.token.validation";
    Date startTime;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        URL_FILTER_LOGGER.info("######################################## Initiating URLFilter filter ########################################");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Instant serviceStartTime = Instant.now();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        URL_FILTER_LOGGER.info("Service URL Filter");
        URL_FILTER_LOGGER.info("Content Type : '" + request.getContentType() + "'");

        HTTPServletResponseHandler.setCORS(response);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        String token = null;
        Context context = null;
        String resourcePath = request.getRequestURI();

        LogFileNames.updateLogFileName(resourcePath);

        try {
            MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest((HttpServletRequest) request);
            String requestDataFromInputStream = getDataFromInputStream(multiReadRequest.getInputStream());

            try {
//                startTime = DateTimeUtils.getTime(new Date());
                Object attribute = multiReadRequest.getHeader("identifier");

                String messageQueueIdentifier = NullOrEmptyChecker.isNull(attribute) ? "" : attribute.toString();
                //loadMDCValues(resourcePath);
                loadMDCValues(resourcePath, messageQueueIdentifier);
                loadEnvironment();
                //String resourcePath = request.getRequestURI();
                URL_FILTER_LOGGER.info("######################################## " + resourcePath + " started ########################################");
                URL_FILTER_LOGGER.info("Request Data : " + requestDataFromInputStream);

                String contextPath = request.getContextPath();

                if (BY_PASS_URLS_FROM_FILTER == null || BY_PASS_URLS_FROM_FILTER.isEmpty() || BY_PASS_URLS_FROM_FILTER.size() < 1) {
                    populateByPassUrlsFromFilter(contextPath);
                }

                //if (!resourcePath.contains("/authentication/generateToken") || (!resourcePath.equals(request.getContextPath() + "/"))) {
                if (validateByToken(resourcePath)) {
                    token = request.getHeader("token");
                    if (isNullOrEmpty(token)) {
                        URL_FILTER_LOGGER.fatal("Token can't be null or empty. Please provide a valid token");
                        throw new NullPointerException("Please provide a valid token");
                    }

                    Instant contextCreatingStartTime = Instant.now();
                    // Verify token
                    WebToken webToken = new WebToken();
                    token = webToken.VerifyToken(token);
                    URL_FILTER_LOGGER.info("Token verified successfully");
                    // Find user credentials from token and create context and add to the request-context
                    AuthenticationUserModel userCredentialsModel = getUserCredentials(token, webToken);
                    context = generateContext(userCredentialsModel);

                    if (context == null) {
                        URL_FILTER_LOGGER.fatal("Couldn't generate context");
                        throw new NullPointerException("Couldn't generate context");
                    }

                    request.setAttribute("context", context);

                    Instant contextCreatingEndTime = Instant.now();
                    long duration = DateTimeUtils.getDuration(contextCreatingStartTime, contextCreatingEndTime);
                    URL_FILTER_LOGGER.log(Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : To generate context it took '" + duration + "' milli-seconds");

                }

                // Add token to the response header
                if (!isNullOrEmpty(token)) {
                    response.addHeader("token", token);
                }

                // Call next filter in the filter chain
                //filterChain.doFilter(request, response);
                filterChain.doFilter(multiReadRequest, responseWrapper);

                // Closing context
                if (context != null) {
                    context.close();
                }

            } catch (JWTVerificationException | IllegalArgumentException exp) {
                URL_FILTER_LOGGER.fatal(exp.getMessage());
                generateErrorMessage(responseWrapper, "Invalid Token. Please provide a valid token", token);
                //throw exp;
            } catch (MatrixException exp) {
                URL_FILTER_LOGGER.fatal(exp.getMessage());
                generateErrorMessage(responseWrapper, "Matrix Exception : " + exp.getMessage(), token);
            } catch (NullPointerException exp) {
                URL_FILTER_LOGGER.fatal(exp.getMessage());
                generateErrorMessage(responseWrapper, "Null Pointer Exception : " + exp.getMessage(), token);
            } catch (Exception exp) {
                URL_FILTER_LOGGER.fatal(exp.getMessage());
                generateErrorMessage(responseWrapper, "Exception : " + exp.getMessage(), token);
            }
        } catch (Exception exp) {
            URL_FILTER_LOGGER.fatal(exp.getMessage());
            generateErrorMessage(responseWrapper, "Exception : " + exp.getMessage(), token);
        } finally {
            try {

//                if(responseWrapper.getStatusCode()==400){
//                    IResponse responseBuilder = new CustomResponseBuilder();
//                    String buildResponse = responseBuilder.setData("The server is not going to process the request due to some error (e.g., malformed request syntax or payload size is too large or invalid request message framing or deceptive request routing)").setStatus(Status.FAILED).buildResponse();
//                    //new ServletResponse();
//                    responseWrapper.getResponse().getOutputStream().write(buildResponse.getBytes("UTF-8"));
//                    responseWrapper.setStatus(200);
//                }
//                
                InputStream responseInputStream = responseWrapper.getContentInputStream();
                responseWrapper.copyBodyToResponse();
                String responseBodyData = getDataFromInputStream(responseInputStream);

                //if (!getExcludedServiceFromResponseWrite().contains(resourcePath)) {
                getExcludedServiceFromResponseWrite();
                if (!excludedResponseFromLogs(resourcePath)) {
                    URL_FILTER_LOGGER.info("Response Data : " + responseBodyData);
                }

            } catch (Exception exp) {
                URL_FILTER_LOGGER.error(exp);
            }
//            Date endTime = DateTimeUtils.getTime(new Date());
//            URL_FILTER_LOGGER.debug("Time elapsed for '" + resourcePath + "' service is : " + DateTimeUtils.elapsedTime(startTime, endTime, null, null));
            Instant serviceEndTime = Instant.now();
            long duration = DateTimeUtils.getDuration(serviceStartTime, serviceEndTime);
            URL_FILTER_LOGGER.log(Boolean.parseBoolean(new CommonPropertyReader().getPropertyValue("log.statistics.info")) ? Priority.INFO : Priority.DEBUG, "Time Statistics : '" + resourcePath + "' has taken '" + duration + "' milli-seconds");

            URL_FILTER_LOGGER.info("######################################## " + resourcePath + " completed ########################################");
        }
    }

    @Override
    public void destroy() {

    }

    private AuthenticationUserModel getUserCredentials(String token, WebToken webToken) throws Exception {
        try {
            AuthenticationProcess userAuthentication = new AuthenticationProcess();
            String authenticationModel = webToken.GetPropertyFromToken(token, "Model");
            AuthenticationUserModel authenticateUser = userAuthentication.getAuthenticUserModel(authenticationModel);
            return authenticateUser;
        } catch (Exception exp) {
            URL_FILTER_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private Context generateContext(AuthenticationUserModel userCredentialsModel) throws Exception {
        try {
            CreateContext generateContext = new CreateContext();
            Context context = generateContext.getContext(userCredentialsModel.getUserId(), userCredentialsModel.getPassword(), userCredentialsModel.getHost(), Boolean.parseBoolean(userCredentialsModel.getIsCasContext()));
            return context;
        } catch (Exception exp) {
            URL_FILTER_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private void generateErrorMessage(HttpServletResponse response, String errorMessage, String token) throws IOException {
        try {
            response.addHeader("token", token);

            IResponse commonResponse = new CustomResponseBuilder();
            String buildResponse = commonResponse.setStatus(Status.FAILED).addErrorMessage(errorMessage).buildResponse();
            //URL_FILTER_LOGGER.debug("Response Data : " + buildResponse);

            byte[] responseToSend = buildResponse.getBytes("UTF-8");
            ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
            ((HttpServletResponse) response).setStatus(203);
            response.getOutputStream().write(responseToSend);
        } catch (IOException exp) {
            URL_FILTER_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private Boolean isNullOrEmpty(String value) {
        return value == null || value.equals("");
    }

    private void populateByPassUrlsFromFilter(String contextPath) {
        BY_PASS_URLS_FROM_FILTER = new HashMap<>();
        BY_PASS_URLS_FROM_FILTER.put("root", contextPath + "/");
        BY_PASS_URLS_FROM_FILTER.put("logs", contextPath + "/logs/*");
        BY_PASS_URLS_FROM_FILTER.put("error", contextPath + "/error");
        BY_PASS_URLS_FROM_FILTER.put("error", contextPath + "/testing");
        BY_PASS_URLS_FROM_FILTER.put("lnIntegration", contextPath + "/bomExportToLN");
        BY_PASS_URLS_FROM_FILTER.put("lnIntegration-delete-lock-file", contextPath + "/deleteLockFile");
        BY_PASS_URLS_FROM_FILTER.put("lnIntegration-delete-scheduler-lock-file", contextPath + "/delete_scheduler_lock_file");
        BY_PASS_URLS_FROM_FILTER.put("token", contextPath + "/authentication/*");
        
        BY_PASS_URLS_FROM_FILTER.put("mastershipChangeScheduler", contextPath + "/pdm/export/mastershipChangeScheduler");

        BY_PASS_URLS_FROM_FILTER.put("swagger", contextPath + "/swagger-ui.html");
        BY_PASS_URLS_FROM_FILTER.put("swagger-jars", contextPath + "/webjars/*");
        BY_PASS_URLS_FROM_FILTER.put("swagger-resources", contextPath + "/swagger-resources/*");
        BY_PASS_URLS_FROM_FILTER.put("swagger-version", contextPath + "/v2/*");
        BY_PASS_URLS_FROM_FILTER.put("swagger-csrf", contextPath + "/csrf");
        BY_PASS_URLS_FROM_FILTER.put("bom-export", contextPath + "/export/*");
        BY_PASS_URLS_FROM_FILTER.put("product-export", contextPath + "/productExport/*");
        BY_PASS_URLS_FROM_FILTER.put("salesforce", contextPath + "/salesforce/*");
        BY_PASS_URLS_FROM_FILTER.put("wbs-import", contextPath + "/wbs-import/*");
        BY_PASS_URLS_FROM_FILTER.put("wbs-search", contextPath + "/wbs-search/*");
        BY_PASS_URLS_FROM_FILTER.put("testService", contextPath + "/testService/*");
        BY_PASS_URLS_FROM_FILTER.put("change-maturity", contextPath + "/change-maturity/*");
        BY_PASS_URLS_FROM_FILTER.put("attribute-values", contextPath + "/attribute-values/*");
        BY_PASS_URLS_FROM_FILTER.put("gts-services", contextPath + "/gts-services/*");
        BY_PASS_URLS_FROM_FILTER.put("reloadCache", contextPath + "/reloadCache");
        BY_PASS_URLS_FROM_FILTER.put("reload-cache", contextPath + "/reload-cache/*");
        BY_PASS_URLS_FROM_FILTER.put("reload-Cache-himelli", contextPath + "/himelli/reload/cache");
        BY_PASS_URLS_FROM_FILTER.put("bomComparison", contextPath + "/compareBOM/*");
        BY_PASS_URLS_FROM_FILTER.put("translation", contextPath + "/vsix/*");
        BY_PASS_URLS_FROM_FILTER.put("common-search-by-item-tnr", contextPath + "/common-search/item/search");
        BY_PASS_URLS_FROM_FILTER.put("autoNameGenerator", contextPath + "/autoname/*");
        BY_PASS_URLS_FROM_FILTER.put("download", contextPath + "/download/*");
        BY_PASS_URLS_FROM_FILTER.put("rnpDownload", contextPath + "/multiLevelBomDataReport/download/*");
        BY_PASS_URLS_FROM_FILTER.put("comos", contextPath + "/comos/*");
        BY_PASS_URLS_FROM_FILTER.put("allBOMAttribute", contextPath + "/allSelectableAttributesForBom");
        BY_PASS_URLS_FROM_FILTER.put("item-search", contextPath + "/valmet/enovia/api/v1/common-search/item/*");
        BY_PASS_URLS_FROM_FILTER.put("class-attributes-config-update", contextPath + "/aton/classification/attributes");
        BY_PASS_URLS_FROM_FILTER.put("ln-transfer", contextPath + "/valmet/enovia/api/items/*");
        BY_PASS_URLS_FROM_FILTER.put("ln-dw", contextPath + "/ln/dw/*");
        BY_PASS_URLS_FROM_FILTER.put("ln-api", contextPath + "/v1/api/ln*");
        BY_PASS_URLS_FROM_FILTER.put("cpqIntegration", contextPath + "/bomExportToCPQ");
        BY_PASS_URLS_FROM_FILTER.put("maturity-state-change-promote", contextPath + "/maturity/stateChange/promote");
        BY_PASS_URLS_FROM_FILTER.put("item-history-fetch", contextPath + "/api/v1/item-history/*");
        BY_PASS_URLS_FROM_FILTER.put("item-history-details", contextPath + "fetch/item-history/details");
        BY_PASS_URLS_FROM_FILTER.put("ln-service", contextPath + "/valmet/enovia/api/v1/export/ln/*");
        BY_PASS_URLS_FROM_FILTER.put("drawing-data", contextPath + "/valmet/enovia/api/v1/drawing/data");
        BY_PASS_URLS_FROM_FILTER.put("encryptData", contextPath + "/secured/encrypt/data");
        BY_PASS_URLS_FROM_FILTER.put("get-model-by-enoviaItem", contextPath + "/valmet/enovia/api/v1/getModelByEnoviaItem");
        BY_PASS_URLS_FROM_FILTER.put("Auto-Number-Generator", contextPath + "/autonumber/generate/*");
        HashMap<String, String> publiclyAccessibleURLs = PropertyReader.getProperties(URL_PATTERN, Boolean.TRUE);

        BY_PASS_URLS_FROM_FILTER.putAll(publiclyAccessibleURLs);
        URL_FILTER_LOGGER.debug("Publicly accessible URL's are " + BY_PASS_URLS_FROM_FILTER);
    }

    private Boolean validateByToken(String resourcePath) {
        URL_FILTER_LOGGER.info(resourcePath);

        if (BY_PASS_URLS_FROM_FILTER.containsValue(resourcePath)) {
            return false;
        } else {
            try {
                BY_PASS_URLS_FROM_FILTER.forEach((String key, String value) -> {
                    if (value.endsWith("*")) {
                        Pattern matchingPattern = Pattern.compile("^" + value + "$*");
                        Matcher matcher = matchingPattern.matcher(resourcePath);

                        if (matcher.find()) {
                            BY_PASS_URLS_FROM_FILTER.put(resourcePath, resourcePath);
                            throw new RuntimeException("New publicly accessible resource path found : " + resourcePath);
                        }
                    }
                });
            } catch (RuntimeException exp) {
                URL_FILTER_LOGGER.warn(exp);
                URL_FILTER_LOGGER.debug("Publicly accessible URL's are " + BY_PASS_URLS_FROM_FILTER);
                return false;
            }
            return true;
        }

        //return !BY_PASS_URLS_FROM_FILTER.containsValue(resourcePath);
    }

    private void loadEnvironment() {
        URL_FILTER_LOGGER.info("PropertyReader.isEnvironmentFileLoaded : " + PropertyReader.isEnvironmentFileLoaded);
        if (!PropertyReader.isEnvironmentFileLoaded) {
            URL_FILTER_LOGGER.debug("Loading environment values");
            PropertyReader.loadEnvironment();
            URL_FILTER_LOGGER.debug("Environment values loaded");
        }
    }

    private void loadMDCValues(String resourcePath) {
        String[] splitedResourcePath = resourcePath.split("/");

        MDC.put("identifier", UniversalUniqueIdentifier.getUUID());
        MDC.put("requestPath", splitedResourcePath[splitedResourcePath.length - 1]);
        MDC.put("threadId", Thread.currentThread().getId());
        MDC.put("threadName", Thread.currentThread().getName());

    }

    private void loadMDCValues(String resourcePath, String messageQueueIdentifier) {
        String[] splitedResourcePath = resourcePath.split("/");

        MDC.put("identifier", UniversalUniqueIdentifier.getUUID());
        MDC.put("messageQueueIdentifier", messageQueueIdentifier);
        MDC.put("requestPath", splitedResourcePath[splitedResourcePath.length - 1]);
        MDC.put("threadId", Thread.currentThread().getId());
        MDC.put("threadName", Thread.currentThread().getName());

    }

    static class UniversalUniqueIdentifier {

        public static String getUUID() {
            return UUID.randomUUID().toString();
        }
    }

    public void getBody(HttpServletRequest request) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        URL_FILTER_LOGGER.info("Request Body Data : " + stringBuilder.toString());
    }

    private List<String> getExcludedServiceFromResponseWrite() {
//        excludeLogs = Optional.ofNullable(excludeLogs).orElse(new ArrayList<>());

        if (NullOrEmptyChecker.isNullOrEmpty(excludeLogs)) {
            excludeLogs = new ArrayList<>();
            excludeLogs.add("/EnoviaRestService/logs/serviceLogs");
            excludeLogs.add("/EnoviaRestService/export/multiLevelBomDataReport/*");
            excludeLogs.add("/EnoviaRestService/export/singleLevelBomDataReport/*");
        }

        return excludeLogs;
    }

    private Boolean excludedResponseFromLogs(String resourcePath) {
        URL_FILTER_LOGGER.debug(resourcePath);

        if (excludeLogs.contains(resourcePath)) {
            return true;
        } else {
            try {
                excludeLogs.forEach((String resourceUrl) -> {
                    if (resourceUrl.endsWith("*")) {
                        Pattern matchingPattern = Pattern.compile("^" + resourceUrl + "$*");
                        Matcher matcher = matchingPattern.matcher(resourcePath);

                        if (matcher.find()) {
                            excludeLogs.add(resourcePath);
                            throw new RuntimeException("From now '" + resourcePath + "' is forbidden from writing response data into the log file");
                        }
                    }
                });
            } catch (RuntimeException exp) {
                URL_FILTER_LOGGER.warn(exp);
                URL_FILTER_LOGGER.debug("Forbidden URLs are  '" + BY_PASS_URLS_FROM_FILTER + "' from writting response in the log");
                return true;
            }
            return false;
        }

        //return !BY_PASS_URLS_FROM_FILTER.containsValue(resourcePath);
    }

    public String getDataFromInputStream(InputStream inputStream) throws Exception {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder requestString = new StringBuilder();
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                requestString.append(output);
            }
            return requestString.toString();
        } catch (Exception exp) {
            throw exp;
        }
    }
}
