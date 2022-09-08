/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.maturity;

import com.bjit.common.code.utility.context.Passport;
import com.bjit.common.rest.app.service.GTS.translation.TranslationServiceImpl;
import com.bjit.common.rest.app.service.payload.common_response.CustomResponseBuilder;
import com.bjit.common.rest.app.service.payload.common_response.IResponse;
import com.bjit.common.rest.app.service.utilities.DisableSSLCertificate;
import com.bjit.common.rest.app.service.utilities.NullOrEmptyChecker;

import com.bjit.ewc18x.utils.PropertyReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author BJIT
 */
@Service
public class MaturityChangeServiceImpl implements MaturityChangeService {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MaturityChangeServiceImpl.class);

    @Override
    public Map<String, String> getLogin() {

        String host = PropertyReader.getProperty("matrix.context.ticket.connection.host");
        final String uri = host + "?action=get_auth_params";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                String.class,
                1
        );
        String result = response.getBody();
        HttpHeaders header = response.getHeaders();

        List<String> cookie = header.get("Set-Cookie");
        Map<String, String> casCookies = new HashMap<>();
        try {
            casCookies = getCASCookies(cookie);
        } catch (IOException ex) {
            Logger.getLogger(MaturityChangeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

        }
        JSONObject jsonObject = new JSONObject(result);
        String lt = jsonObject.getString("lt");
        casCookies.put("lt", lt);
        return casCookies;
    }

    @Override
    public Map<String, String> getCSRFToken(Map<String, String> cas, String username, String password) {

        String host = PropertyReader.getProperty("matrix.context.ticket.connection.host");
        String serviceHost = PropertyReader.getProperty("matrix.context.cas.connection.host");
        String serviceUri = serviceHost + PropertyReader.getProperty("maturity.change.csrf.token.url");
        String encodedUri = encodeValue(serviceUri);

        final String uri = host + "?service=" + encodedUri + "";

        StringBuilder requestJsonBuilder = new StringBuilder("lt=").append(cas.get("lt")).append("&username=").append(username).append("&password=").append(password);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, requestJsonBuilder.toString());
        Request request = new Request.Builder()
                .url(uri)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .addHeader("Cookie", cas.get("JSESSIONID") + ";" + cas.get("SERVERID") + ";" + cas.get("afs"))
                .build();

        String result = "";
        Map<String, String> csrfCookies = new HashMap<>();
        try {

            Response response = client.newCall(request).execute();
            result = response.body().string();
            Headers heads = response.headers();
            String jsession = heads.get("Set-Cookie");
            if (!jsession.contains("JSESSIONID")) {
                jsession = heads.value(0);
            }
            try {
                csrfCookies = getCookies(jsession);
            } catch (IOException ex) {
                Logger.getLogger(MaturityChangeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MaturityChangeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        JSONObject jsonObject = new JSONObject(result);
        JSONObject csrfObject = jsonObject.getJSONObject("csrf");
        String csrf = csrfObject.getString("value");
        csrfCookies.put("csrf", csrf);
        return csrfCookies;
    }

    @Override
    public ResponseEntity getRestResponse(String uri, StringBuilder requestJsonBuilder, String securityContext, HttpServletRequest httpRequest, Map<String, String> cas) {
        ResponseEntity<String> response = null;
        String result = "";
        IResponse responseBuilder = new CustomResponseBuilder();
        try {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, requestJsonBuilder.toString());
            Request request = new Request.Builder()
                    .url(uri)
                    .method("POST", body)
                    .addHeader("SecurityContext", securityContext)
                    .addHeader("ENO_CSRF_TOKEN", cas.get("csrf"))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Cookie", cas.get("JSESSIONID"))
                    .build();
            try {
                Response responseTest = client.newCall(request).execute();
                result = responseTest.body().string();
            } catch (IOException ex) {

                Logger.getLogger(MaturityChangeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            JSONObject jsonObject = new JSONObject(result);
            try {
                if (jsonObject.getJSONArray("results").length() == 0) {

                }
            } catch (Exception ex) {

                LOGGER.error("Server Error Response: " + result);

                String errorMessage = "Maturity Change Failed";
                String buildResponse = responseBuilder
                        .addErrorMessage(errorMessage)
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            JSONArray resultObject = jsonObject.getJSONArray("results");
            JSONObject reslt = resultObject.getJSONObject(0);
            String maturityState = reslt.getString("maturityState");
            String id = reslt.getString("id");

            StringBuilder resultBody = new StringBuilder("{\"current\":").append("\"").append(maturityState).append("\"")
                    .append(", \"physicalid\":").append("\"").append(id).append("\"").append("}");

            StringBuilder requestBuilder = new StringBuilder("{\"report\" : [],")
                    .append("\"results\" : [")
                    .append(resultBody.toString())
                    .append("],")
                    .append("\"status\" : \"success\"")
                    .append("}");

            JsonArray reportArray = new JsonParser()
                    .parse(result)
                    .getAsJsonObject()
                    .getAsJsonArray("report");
            if (!NullOrEmptyChecker.isNull(reportArray)
                    && reportArray.size() > 0) {
                String errorMessage = reportArray
                        .get(0)
                        .getAsJsonObject()
                        .get("error")
                        .getAsString();

                String buildResponse = responseBuilder
                        .addErrorMessage(errorMessage)
                        .setStatus(com.bjit.common.rest.app.service.payload.common_response.Status.FAILED)
                        .buildResponse();
                return new ResponseEntity<>(buildResponse, HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(requestBuilder.toString(), HttpStatus.ACCEPTED);
        } catch (RestClientException ex) {
        } catch (Exception ex) {
            Logger.getLogger(MaturityChangeServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static Map<String, String> getCASCookies(List<String> cookie) throws IOException {

        Map<String, String> cookies = new HashMap<>();
        for (int i = 0; i < cookie.size(); i++) {
            String headerValue = cookie.get(i);
            if (headerValue == null) {
                continue;
            }

            String[] fields = headerValue.split(";\\s*");
            String cookieValue = fields[0];
            if (cookieValue.startsWith("JSESSIONID")) {
                cookies.put("JSESSIONID", cookieValue);
            }
            if (cookieValue.startsWith("afs")) {
                cookies.put("afs", cookieValue);
            }
            if (cookieValue.startsWith("SERVERID")) {
                cookies.put("SERVERID", cookieValue);
            }

        }
        return cookies;
    }

    public static Map<String, String> getCookies(String cookie) throws IOException {

        Map<String, String> cookies = new HashMap<>();
        String[] fields = cookie.split(";\\s*");
        String cookieValue = fields[0];
        if (cookieValue.startsWith("JSESSIONID")) {
            cookies.put("JSESSIONID", cookieValue);
        }

        return cookies;
    }

}