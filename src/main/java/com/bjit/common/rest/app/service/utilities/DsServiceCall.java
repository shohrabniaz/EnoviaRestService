/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.utilities;

import com.bjit.common.code.utility.http.client.Authentication;
import com.bjit.common.code.utility.http.client.CSRFTokenGenerationService;
import com.bjit.common.code.utility.http.client.SimpleHttpClient;
import com.bjit.common.code.utility.http.v2.impl.SimpleHttpClientService;
import com.bjit.common.code.utility.security.ContextPasswordSecurity;
import com.bjit.ewc18x.utils.PropertyReader;
import com.fasterxml.jackson.core.JsonParseException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.springframework.http.HttpHeaders;

/**
 *
 * @author Ahmad R Farabi [PLM Team, BJIT] <ahmad.farabi@bjitgroup.com>
 */
public class DsServiceCall {
    
    String spaceUrl = "";
    String passUrl = "";
    String user = "";
    String pass = "";
    String sc = "";
    ContextPasswordSecurity contextPasswordSecurity;

    Authentication auth;
    SimpleHttpClient httpClient;
    SimpleHttpClientService httpClient2;
    
    CSRFTokenGenerationService csrfService;
    
    public DsServiceCall() throws Exception{
        this.sc = PropertyReader.getProperty("preferred.security.context.dslc");
        this.authenticate();
    }
    
    public DsServiceCall(Boolean csrf, String securityContext) throws Exception{
        this.sc = securityContext;
        this.authenticate();
        this.setCSRFTokenGenerationService();
        httpClient2 = new SimpleHttpClientService();
        httpClient2.setCookieStore(this.getCSRFTokenService().getSimpleHttpClient().getCookieStore());
    }

    private void authenticate() throws Exception {
        this.contextPasswordSecurity = new ContextPasswordSecurity();
        try {
            //get all variables from property file
            this.spaceUrl = PropertyReader.getProperty("matrix.context.cas.connection.host.dslc");
            this.passUrl = PropertyReader.getProperty("matrix.context.cas.connection.passport");
            this.user = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.name"));
            this.pass = contextPasswordSecurity.decryptPassword(PropertyReader.getProperty("context.pass"));

            //create auth object
            this.auth = new Authentication(this.passUrl, this.user, this.pass);
            //do the authentication and save cookies
            this.auth.doAuthenticate();
            //System.out.println("From DS FTS -> isAuthenticated()===== "+this.auth.isAuthenticated());
            
            //get http client for authetication class
            this.httpClient = this.auth.getSimpleHttpClient();
            //this.httpClient.printAllCookies(); //check the cookies
        }
        catch(JsonParseException e){
            //json parse error in case of HTML reply/not authenticated
            throw new Exception("Unexpected response");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String callService(String url, String params) throws Exception {
        try {
            //check if authenticated or not
            if (this.auth.isAuthenticated()) {
                //set headers
                Header[] header = new Header[2];
                header[0] = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                header[1] = new BasicHeader("SecurityContext", sc);

                //prepare body/query params
                String searchString = URLEncoder.encode(params, StandardCharsets.UTF_8.toString());

                CloseableHttpResponse httpResp = null;
                try {
                    //call get/post request
                    httpResp = this.httpClient.doGetRequest(url + searchString, true, header);

                    //System.out.println("Response code = "+httpResp.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    //if there is an error, then we assume the login token expired and need to authenticate again.
                    this.authenticate();
                    httpResp = this.httpClient.doGetRequest(url + searchString, true, header);
                }

                //check the response status
                switch (httpResp.getStatusLine().getStatusCode()) {
                    //if status 200 return response
                    case HttpStatus.SC_OK:
                        String resp = this.httpClient.getResponseStr(httpResp);
                        //System.out.println("Response = "+resp);
                        return resp;
                    //for other status throw error
                    default:
                        throw new Exception("Response : " + httpResp.getStatusLine());
                }
            }
        } catch (JsonParseException e) {
            //json parse error in case of HTML reply/not authenticated
            throw new Exception("Unexpected response");
        }
        catch(Exception e) {
            throw e;
        }
        return null;
    }
    
    private void setCSRFTokenGenerationService(){
        csrfService = new CSRFTokenGenerationService(this.spaceUrl, this.passUrl, this.user , this.pass);
    }
    
    public CSRFTokenGenerationService getCSRFTokenService() throws Exception {
        return csrfService;
    }
    
    public String callMVService(String url, String body, String reqType) throws Exception {
        try
        {
            //check if authenticated or not
            if(this.auth.isAuthenticated()){
                //set headers
                Header [] header = new Header[3];
                header[0] = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                header[1] = new BasicHeader("SecurityContext", sc);
                header[2] = new BasicHeader("ENO_CSRF_TOKEN", this.getCSRFTokenService().getCSRFToken().getValue());
                System.out.println("CSRF="+this.getCSRFTokenService().getCSRFToken().getValue());
                System.out.println("body="+body);
                HttpResponse httpResp = null;
                try {
                    //call get/post request
                    switch (reqType) {
                        case "GET":
                            httpResp = httpClient2.doGetRequest(url, true, header);
                            break;
                        case "POST":
                            httpResp = httpClient2.doPostRequest(url, true, body, header);
                            break;
                        case "PATCH":
                            httpResp = httpClient2.doPatchRequest(url, true, Arrays.asList(header), body);
                            break;
                        default:
                            break;
                    }
                    System.out.println("Response code = "+httpResp.getStatusLine().getStatusCode());
                }
                catch(Exception e){
                    //if there is an error, then we assume the login token expired and need to authenticate again.
                    this.authenticate();
                    switch (reqType) {
                        case "GET":
                            httpResp = httpClient2.doGetRequest(url, true, header);
                            break;
                        case "POST":
                            httpResp = httpClient2.doPostRequest(url, true, body, header);
                            break;
                        case "PATCH":
                            httpResp = httpClient2.doPatchRequest(url, true, Arrays.asList(header), body);
                            break;
                        default:
                            break;
                    }
                }
                
                //check the response status
                switch (httpResp.getStatusLine().getStatusCode()) {
                    //if status 200 return response
                    case HttpStatus.SC_OK: 
                        String resp = httpClient2.getResponseStr(httpResp);
                        //System.out.println("Response = "+resp);
                        httpClient2.closeResponse(httpResp);
                        return resp;
                    //for other status throw error
                    default: 
                        throw new Exception("Response : "+ httpResp.getStatusLine());
                } 
            }
        }
        catch(JsonParseException e){
            //json parse error in case of HTML reply/not authenticated
            throw new Exception("Unexpected response");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public String callPostService(String url, String params) throws Exception {
        try {
            //check if authenticated or not
            if (this.auth.isAuthenticated()) {
                //set headers
                Header[] header = new Header[2];
                header[0] = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                header[1] = new BasicHeader(HttpHeaders.ACCEPT, "application/json");

                //prepare body/query params
                // String searchString = URLEncoder.encode(params,StandardCharsets.UTF_8.toString());
                CloseableHttpResponse httpResp = null;
                try {
                    //call get/post request

                    httpResp = this.httpClient.doPostRequest(url, true, params, header);
                     int statusCode = httpResp.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        httpResp = this.httpClient.doPostRequest(url, true, params, header);
                    }

                  

                } catch (Exception e) {
                    //if there is an error, then we assume the login token expired and need to authenticate again.
                    this.authenticate();
                    httpResp = this.httpClient.doPostRequest(url, false, params, header);
                }

                //check the response status
                switch (httpResp.getStatusLine().getStatusCode()) {
                    //if status 200 return response
                    case HttpStatus.SC_OK:
                        String resp = this.httpClient.getResponseStr(httpResp);
                        //System.out.println("Response = "+resp);
                        return resp;
                    //for other status throw error
                    default:
                        throw new Exception("Response : " + httpResp.getStatusLine());
                }
            }
        } catch (JsonParseException e) {
            //json parse error in case of HTML reply/not authenticated
            throw new Exception("Unexpected response");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public boolean getIsAuthenticated() {
        return this.auth.isAuthenticated();
    }
    
    public void closeClients(CSRFTokenGenerationService service) {
        try {
            if(httpClient2 != null) {
                httpClient2.closeClient();
            }
            if(service != null){
                service.getAuthentication().loggingOut();
                service.getSimpleHttpClient().closeClient();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
