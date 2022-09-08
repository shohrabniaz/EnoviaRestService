package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.rest.app.service.payload.common_response.CommonResponse;
import javax.servlet.http.*;
import org.springframework.web.context.request.*;
import java.net.*;
import java.io.*;
import java.util.HashMap;

public class VerifyToken {

    private static String token;

    private static final org.apache.log4j.Logger VERIFY_TOKEN_LOGGER = org.apache.log4j.Logger.getLogger(VerifyToken.class);
    //public AuthenticationUserModel authenticationUserModel;

    /*public Context getDataFromHttpRequest(final HttpServletRequest httpRequest) throws Exception {
        try {
            System.out.println("\n\n\n");
            final JSON json = new JSON();
            this.authenticationUserModel = (AuthenticationUserModel)json.deserialize(this.verifyToken(httpRequest.getHeader("token")), (Class)AuthenticationUserModel.class);
            return CreateContext.getContext(this.authenticationUserModel.getUserId(), this.authenticationUserModel.getPassword(), this.authenticationUserModel.getHost(), Boolean.parseBoolean(this.authenticationUserModel.getIsCasContext()), false, false);
        }
        catch (Exception exp) {
            exp.printStackTrace(System.out);
            VerifyTokenAndGetContext.VERIFY_TOKEN_AND_GET_CONTEXT_LOGGER.log((Priority)Level.FATAL, (Object)exp.getMessage());
            VerifyTokenAndGetContext.VERIFY_TOKEN_AND_GET_CONTEXT_LOGGER.log((Priority)Level.TRACE, (Object)exp);
            throw exp;
        }
    }*/
    private Boolean isNullOrEmpty(String checkString) {
        return checkString == null || checkString.equalsIgnoreCase("");
    }

    public String verifyToken(final String token) throws MalformedURLException, IOException, Exception {
        try {

            if (isNullOrEmpty(token)) {
                throw new NullPointerException("Please provide a valid token");
            }

            final HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            final StringBuffer baseUrl = new StringBuffer();

            VERIFY_TOKEN_LOGGER.debug("currentRequest.getLocalName() : " + currentRequest.getLocalName());
            VERIFY_TOKEN_LOGGER.debug("currentRequest.getLocalName() equals : " + currentRequest.getLocalName().equals("0:0:0:0:0:0:1"));
            VERIFY_TOKEN_LOGGER.debug("Inet4Address.getLocalHost().getHostAddress() : " + Inet4Address.getLocalHost().getHostAddress());
            baseUrl
                    .append(currentRequest.getScheme())
                    .append("://")
                    .append(currentRequest.getLocalName().equals("0:0:0:0:0:0:0:1") ? Inet4Address.getLocalHost().getHostAddress() : currentRequest.getLocalName())
                    .append(":")
                    .append(currentRequest.getLocalPort())
                    .append("/")
                    .append(currentRequest.getContextPath());
            final String serviceUrl = baseUrl
                    .append("/authentication/verifyToken")
                    .toString();
            VERIFY_TOKEN_LOGGER.debug("Verification url : " + serviceUrl);
            final URL url = new URL(serviceUrl);
            final HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestProperty("token", token);
            httpUrlConnection.setDoOutput(true);
            if (httpUrlConnection.getResponseCode() != 200) {
                String errorMessage = getDataFromInputStream(httpUrlConnection.getErrorStream());
                VERIFY_TOKEN_LOGGER.debug("Error : " + errorMessage);
                //throw new RuntimeException("Failed : HTTP error code : " + httpUrlConnection.getResponseCode() + " Error message : " + errorMessage);
                throw new Exception(errorMessage);
            }

            setToken(httpUrlConnection.getHeaderField("token"));

            return getDataFromInputStream(httpUrlConnection.getInputStream());
        } catch (Exception exp) {
            VERIFY_TOKEN_LOGGER.error(exp.getMessage());
            throw exp;
        }
    }

    private String getDataFromInputStream(InputStream inputStream) throws Exception {
        //final InputStream inputStream = httpUrlConnection.getInputStream();
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final StringBuilder jsonString = new StringBuilder();
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                jsonString.append(output);
            }
            //httpUrlConnection.disconnect();
            return jsonString.toString();
        } catch (Exception exp) {
            VERIFY_TOKEN_LOGGER.debug(exp.getMessage());
            throw new Exception("Invalid Token. Please provide a valid token");
        }
    }

    public HashMap<String, Object> getCredentialsMap(HttpServletRequest httpRequest) throws Exception {
        try {
            String commonResponseData;
            HashMap<String, Object> credentialMap = new HashMap<>();
            try {
                commonResponseData = this.verifyToken(httpRequest.getHeader("token"));
            } catch (Exception exp) {
                throw exp;
            }

            JSON json = new JSON();
            VERIFY_TOKEN_LOGGER.debug(commonResponseData);
            CommonResponse commonResponse = json.deserialize(commonResponseData, CommonResponse.class);

            /*ObjectMapper<Object, AuthenticationUserModel> objectMapper = new ObjectMapper();
            objectMapper.setObjects(commonResponse.getData(), AuthenticationUserModel.class);

            AuthenticationUserModel authenticationUserModel = objectMapper.getObject();*/
            
            credentialMap.put("token", getToken());
            credentialMap.put("credentials", commonResponse.getData());

            return credentialMap;
        } catch (Exception exp) {
            VERIFY_TOKEN_LOGGER.debug(exp.getMessage());
            throw exp;
        }
    }
    
    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        VerifyToken.token = token;
    }
}
