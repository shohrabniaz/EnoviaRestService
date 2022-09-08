/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.webServiceConsumer;

import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;
import java.io.*;
import java.net.*;
import java.util.HashMap;

/**
 *
 * @author BJIT
 */
public class URLConnection {

    private static final org.apache.log4j.Logger URL_CONNECTION_LOGGER = org.apache.log4j.Logger.getLogger(URLConnection.class);

    public String callService(final String serviceURL, final RequestModel serviceRequesterModel) throws MalformedURLException, IOException, Exception {
        String processStart = "Creating Service Message Start";
        String processStop = "Creating Service Message  Stop";
        URL_CONNECTION_LOGGER.info("--------------------------- ||| " + processStart + " ||| ---------------------------");

        String responseData = "";
        
        ResponseValidator responseValidator = new ResponseValidator();
        try {
            DisableSSLCertificate.DisableCertificate();

            URL_CONNECTION_LOGGER.info("Service URL : '" + serviceURL + "'");
            URL url = new URL(serviceURL);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();

            httpUrlConnection = setHeaderData(httpUrlConnection, serviceRequesterModel);
            responseData = NullOrEmptyChecker.isNull(httpUrlConnection.getInputStream()) ? getDataFromInputStream(httpUrlConnection.getErrorStream()) : getDataFromInputStream(httpUrlConnection.getInputStream());

            httpUrlConnection.disconnect();

            responseData = responseValidator.validateResponseData(responseData);
        } catch (MalformedURLException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            responseData = responseValidator.validateResponseData(exp.getMessage());
            throw exp;
        } catch (IOException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            responseData = responseValidator.validateResponseData(exp.getMessage());
            throw exp;
        } catch (NullPointerException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            responseData = responseValidator.validateResponseData(exp.getMessage());
            throw exp;
        } catch (RuntimeException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            responseData = responseValidator.validateResponseData(exp.getMessage());
            throw exp;
        } catch (Exception exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            responseData = responseValidator.validateResponseData(exp.getMessage());
            throw exp;
        } finally {
            URL_CONNECTION_LOGGER.info("--------------------------- ||| " + processStop + " ||| ---------------------------");
        }
        
        return responseData;
    }

    private HttpURLConnection setHeaderData(HttpURLConnection httpUrlConnection, RequestModel serviceRequesterModel) throws ProtocolException, Exception {
        try {

            String methodType = serviceRequesterModel.getServiceMethodType();
            methodType = NullOrEmptyChecker.isNullOrEmpty(methodType) ? "GET" : methodType;
            httpUrlConnection.setRequestMethod(methodType);

            URL_CONNECTION_LOGGER.debug("Service method type : " + methodType);

            String contentType = serviceRequesterModel.getContentType();
            contentType = "application/json";
            httpUrlConnection.setRequestProperty("Content-Type", contentType);

            URL_CONNECTION_LOGGER.debug("Content-Type : " + contentType);

            String accepts = serviceRequesterModel.getAccepts();
            accepts = NullOrEmptyChecker.isNullOrEmpty(accepts) ? contentType : accepts;
            httpUrlConnection.setRequestProperty("Accept", accepts);

            URL_CONNECTION_LOGGER.debug("Accept : " + accepts);

            Boolean cacheControl = serviceRequesterModel.getCacheControl();
            cacheControl = NullOrEmptyChecker.isNull(cacheControl) ? Boolean.FALSE : cacheControl;
            httpUrlConnection.setUseCaches(cacheControl);

            URL_CONNECTION_LOGGER.debug("Cache-Control : " + cacheControl);

            HashMap<String, String> requestHeaders = serviceRequesterModel.getRequestHeaders();
            requestHeaders.forEach((String headerName, String headerValue) -> {
                httpUrlConnection.setRequestProperty(headerName, headerValue);
                URL_CONNECTION_LOGGER.debug("Header Name : '" + headerName + "'");
            });

            httpUrlConnection.setRequestProperty("Content-Type", contentType);

            String bodyData = serviceRequesterModel.getBodyData();
            if (!NullOrEmptyChecker.isNull(bodyData)) {
                httpUrlConnection.setDoOutput(Boolean.TRUE);
                setBodyData(httpUrlConnection, bodyData);
                URL_CONNECTION_LOGGER.debug("Body Data : " + bodyData);
            }

            return httpUrlConnection;
        } catch (ProtocolException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            throw exp;
        } catch (NullPointerException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            throw exp;
        } catch (SocketTimeoutException | RuntimeException exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            throw exp;
        } catch (Exception exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            throw exp;
        }
    }

    private void setBodyData(HttpURLConnection httpUrlConnection, String bodyData) throws Exception {
        try (OutputStream outputStream = httpUrlConnection.getOutputStream()) {
            outputStream.write(bodyData.getBytes("UTF-8"));
            outputStream.flush();
        } catch (Exception exp) {
            URL_CONNECTION_LOGGER.fatal(exp);
            throw exp;
        }
    }

    private String getDataFromInputStream(InputStream inputStream) throws Exception {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder responseMessage = new StringBuilder();
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                responseMessage.append(output);
            }
            String response = responseMessage.toString();
            URL_CONNECTION_LOGGER.info("Response from the service : '" + response + "'");
            return response;
        } catch (Exception exp) {
            URL_CONNECTION_LOGGER.error(exp);
            throw exp;
        }
    }
}
