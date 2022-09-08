package com.bjit.common.rest.app.service.utilities;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 *
 * @author Md. Omour Faruq Sumon
 */
public class DisableSSLCertificate {

    private static TrustManager[] trustAllCerts;
    private static HostnameVerifier allHostsValid;

    /**
     *
     * Dissables the SSL checking of the host by
     * returning true always where checking is going on
     *
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static void DisableCertificate() throws KeyManagementException, NoSuchAlgorithmException {
        try {
            trustAllCertificates();
            installTrustManager();
            validateAllHosts();
            installTrustedHost();
            doClear();
        } catch (KeyManagementException | NoSuchAlgorithmException exp) {
            exp.printStackTrace();
            throw exp;
        }
    }

    private static void trustAllCertificates() {
        // Create a trust manager that does not validate certificate chains
        try {
            trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };
        } catch (Exception exp) {
            exp.printStackTrace();
            doClear();
            throw exp;
        }

    }

    private static void installTrustManager() throws KeyManagementException, NoSuchAlgorithmException {
        // Install the all-trusting trust manager
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException exp) {
            doClear();
            throw exp;
        }
    }

    private static void validateAllHosts() {
        // Create all-trusting host name verifier
        //allHostsValid = (String hostname, SSLSession session) -> true;
        try {
            allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
        } catch (Exception exp) {
            exp.printStackTrace();
            doClear();
            throw exp;
        }

    }

    private static void installTrustedHost() {
        // Install the all-trusting host verifier
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception exp) {
            exp.printStackTrace();
            doClear();
            throw exp;
        }
    }

    private static void doClear() {
        try {
            trustAllCerts = null;
            allHostsValid = null;
        } catch (Exception exp) {
            exp.printStackTrace();
            throw exp;
        }
    }
}