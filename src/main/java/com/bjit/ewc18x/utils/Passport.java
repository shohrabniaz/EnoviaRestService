package com.bjit.ewc18x.utils;



import com.matrixone.json.JSONArray;
import com.matrixone.json.JSONObject;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Passport {
    
    final static String kernelServlet = "/servlet/MatrixXMLServlet";
    static X509TrustManager tm = new TrustAllTrustManager();
    final static boolean useCertificates = false;
    
   public static String getTicket(String host, String user, String password) throws Exception {
        HttpURLConnection conTemp = getHttpConnection(host + kernelServlet, false);
        int respTemp = conTemp.getResponseCode();
        if (respTemp != HttpURLConnection.HTTP_MOVED_TEMP)
            throw new Exception("Required CAS Redirect not found");
        String redirectUrl = conTemp.getHeaderField("Location");
        System.out.println("redirectUrl: "+redirectUrl);
        HttpURLConnection conCAS = getHttpConnection(redirectUrl, false);   
        Map<String,String> cookiesCAS = getCASCookies(conCAS);
        System.out.println("cookiesCAS: "+cookiesCAS);
        String jSessionId = cookiesCAS.get("JSESSIONID");
        String authParamsCAS = getAuthParams(conCAS);
        System.out.println("authParamsCAS: "+authParamsCAS);
        JSONObject jsonCAS = new JSONObject(authParamsCAS);   
        String lt = (String) jsonCAS.get("lt");  
        String loginUrlCAS = (String) jsonCAS.get("url");                                                 
        HttpURLConnection conCASLogin = getHttpConnection(loginUrlCAS, false);
        conCASLogin.setRequestProperty("Cookie", "JSESSIONID=" + jSessionId);
        conCASLogin.setRequestMethod("POST");                                                         
        conCASLogin.setDoOutput(true);
        Properties casUrlParamProperties = new Properties();
        casUrlParamProperties.put("lt", lt);
        casUrlParamProperties.put("username", user);
        casUrlParamProperties.put("password", password);
        String casUrlParams = encodeUrlParams(casUrlParamProperties);
        try (DataOutputStream wr = new DataOutputStream(conCASLogin.getOutputStream())) {
            wr.writeBytes(casUrlParams);
            wr.flush();
        }  
        int respCodeCASLogin = conCASLogin.getResponseCode();
        if(respCodeCASLogin == HttpURLConnection.HTTP_MOVED_TEMP) {           
            // If response code is HTTP_MOVED_TEMP, it is redirect call to service provider i.e. 3DX server with valid ticket appended in the redirect URL.
            String redirectUrlFromCASLogin = conCASLogin.getHeaderField("Location");
            if(redirectUrlFromCASLogin.contains("?ticket=")){
               String ticket = redirectUrlFromCASLogin.substring(redirectUrlFromCASLogin.lastIndexOf("?ticket=")); 
            return ticket+";"+jSessionId;
            }
                
            else
                throw new Exception("Required CAS Ticket not found");
        } else {
            // If it is not redirect call with valid ticket, there must be authentication error on 3DPassport. Get the error message.
            String authParamsCASLogin = getAuthParams(conCASLogin); 
            JSONObject jsonCASLogin = new JSONObject(authParamsCASLogin);
            JSONArray jsonArrayCASLogin = (JSONArray) jsonCASLogin.get("errorMsgs");
            String errorMsg = "";
            for (int i = 0; i < jsonArrayCASLogin.length(); i++){
                JSONObject tmp = (JSONObject) jsonArrayCASLogin.get(i);
                errorMsg += tmp.get("defaultMessage");
            }
            if (errorMsg.isEmpty()) errorMsg = "Internal Server Error";
            throw new Exception(errorMsg);                                                                                                                                                             
        }
    }
                
    private static HttpURLConnection getHttpConnection(String sUrl ,boolean followRedirect) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
        if (! useCertificates) {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {tm}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }       
        URL url = new URL(sUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(followRedirect);                             
        return con;
    }

    private static Map<String,String> getCASCookies(URLConnection con) {
        Map<String,String> cookies = new HashMap<String,String>();
        Map<String, List<String>> headers = con.getHeaderFields(); 
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerKey = entry.getKey();
//            Util.trace("headerKey: " + headerKey);
            if (headerKey != null && headerKey.equalsIgnoreCase("Set-Cookie")) { 
                for (String headerValue : entry.getValue()) {
                    if(headerValue == null) continue;
//                    Util.trace("headerValue: " + headerValue);
                    String[] fields = headerValue.split(";\\s*"); 
                    String cookieValue = fields[0];                                                                                                                                                                                                        
                    if (cookieValue.startsWith("JSESSIONID") || cookieValue.startsWith("afs") || cookieValue.startsWith("CASTGC")) {  
                       String a[] = cookieValue.split("=", 2);
                       cookies.put(a[0], a[1]);
                    }                                                                                              
                }
                break;
            }
        }
        return cookies;
    }

    private static String encodeUrlParams(Properties p) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Enumeration names = p.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = p.getProperty(name); 
            sb.append("&").append(URLEncoder.encode(name, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
        }                              
        return sb.delete(0, 1).toString();
    }

    private static String getAuthParams(URLConnection con) throws ParserConfigurationException, SAXException, IOException {
        String authParams = null;
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
        Document doc = db.parse(con.getInputStream());
        NodeList nodes = doc.getElementsByTagName("script");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);                                                                
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (e.getAttribute("id").equals("configData") && e.getAttribute("type").equals("application/json")) {
                    authParams = e.getTextContent();    
                    break;
                }
            }
        }
        return authParams;
    }
    
    public static class TrustAllTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
    }
    
}
