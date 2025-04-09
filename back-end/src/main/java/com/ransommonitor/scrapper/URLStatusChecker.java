package com.ransommonitor.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.Connection;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class URLStatusChecker {

    static {
        disableSSLCertificateChecking();
    }

    public static boolean checkOnionStatus(String onionUrl, int port) {
        try {
            if (!onionUrl.startsWith("http://") && !onionUrl.startsWith("https://")) {
                onionUrl = "http://" + onionUrl;
            }

            System.out.println(onionUrl + " " + port + " in Onion Status Check");

            Proxy torProxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", port));

            Connection.Response response = Jsoup.connect(onionUrl)
                    .proxy(torProxy)
                    .timeout(30000)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .method(Connection.Method.HEAD)
                    .execute();

            int statusCode = response.statusCode();
            System.out.println("Status code for " + onionUrl + ": " + statusCode);

            return statusCode < 400;
        } catch (IOException e) {
            System.out.println("Error checking URL " + onionUrl + ": " + e.getMessage());
            return false;
        }
    }

    private static void disableSSLCertificateChecking() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            System.err.println("Failed to disable SSL checking: " + e.getMessage());
        }
    }
}
