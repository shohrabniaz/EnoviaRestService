package com.bjit.ewc18x.context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Passport {

    final static String kernelServlet = "/servlet/MatrixXMLServlet";
    static X509TrustManager tm = new TrustAllTrustManager();
    final static boolean useCertificates = false;

    public static String getTicket(String host, String user, String password) throws Exception {
        HttpURLConnection conTemp = getHttpConnection(host + kernelServlet, false);
        int respTemp = conTemp.getResponseCode();//java.net.ConnectException: Connection timed out: connect
        if (respTemp != HttpURLConnection.HTTP_MOVED_TEMP) {
            throw new Exception("Required CAS Redirect not found");
        }
        String redirectUrl = conTemp.getHeaderField("Location");
        System.out.println("redirectUrl: " + redirectUrl);
        HttpURLConnection conCAS = getHttpConnection(redirectUrl, false);
        Map<String, String> cookiesCAS = getCASCookies(conCAS);
        System.out.println("cookiesCAS: " + cookiesCAS);
        String jSessionId = cookiesCAS.get("JSESSIONID");
        String lt = getLT(jSessionId, redirectUrl);
        String loginUrlCAS = redirectUrl;

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{tm}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        URL url = new URL(loginUrlCAS);
        HttpsURLConnection conCASLogin = (HttpsURLConnection) url.openConnection();
        conCASLogin.setInstanceFollowRedirects(false);
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
        if (respCodeCASLogin == HttpURLConnection.HTTP_MOVED_TEMP) {
            // If response code is HTTP_MOVED_TEMP, it is redirect call to service provider i.e. 3DX server with valid ticket appended in the redirect URL.
            String redirectUrlFromCASLogin = conCASLogin.getHeaderField("Location");
            if (redirectUrlFromCASLogin.contains("?ticket=")) {
                String ticket = redirectUrlFromCASLogin.substring(redirectUrlFromCASLogin.lastIndexOf("?ticket="));
                return ticket + ";" + jSessionId;
            } else {
                throw new Exception("Required CAS Ticket not found");
            }
        }
        return null;
    }

    private static HttpURLConnection getHttpConnection(String sUrl, boolean followRedirect) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyManagementException {
        if (!useCertificates) {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{tm}, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        URL url = new URL(sUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(followRedirect);
        return con;
    }

    private static Map<String, String> getCASCookies(URLConnection con) {
        Map<String, String> cookies = new HashMap<String, String>();
        Map<String, List<String>> headers = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerKey = entry.getKey();
//            Util.trace("headerKey: " + headerKey);
            if (headerKey != null && headerKey.equalsIgnoreCase("Set-Cookie")) {
                for (String headerValue : entry.getValue()) {
                    if (headerValue == null) {
                        continue;
                    }
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

    private static String getLT(String jSessionId, String loginUrl) {
        int i = loginUrl.indexOf('?');
        String loginTicketUrl = loginUrl.substring(0, i) + "?action=get_auth_params"; // "https://vm3dxpassport.plm.valmet.com/3dpassport/login?action=get_auth_params";
        URL url = null;
        try {
            url = new URL(loginTicketUrl);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Passport.class.getName()).log(Level.SEVERE, null, ex);
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(Passport.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.setRequestProperty("Cookie", "JSESSIONID=" + jSessionId); // this is a must needed otherwise won't work
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            Logger.getLogger(Passport.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            // extraxt LT from content
            String resStr = content.toString(); //{"response":"login","lt":"LT-720-B4ox9OP5ZQy5fUf7aSmiyreIaZsqKe"}
            int index = resStr.indexOf("LT-");
            String lt = resStr.substring(index, resStr.length() - 2);
            System.out.println("LT: " + lt);
            return lt;
        } catch (IOException ex) {
            Logger.getLogger(Passport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static class TrustAllTrustManager implements X509TrustManager {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

}
