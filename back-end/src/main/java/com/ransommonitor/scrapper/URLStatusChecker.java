package com.ransommonitor.scrapper;


import org.jsoup.Jsoup;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

public class URLStatusChecker {
    public static boolean checkOnionStatus(String onionUrl, int port) {
        try {
            Proxy TOR_PROXY = new Proxy(Proxy.Type.SOCKS,
                    new InetSocketAddress("localhost", port));
            Jsoup.connect(onionUrl)
                    .proxy(TOR_PROXY)
                    .timeout(30000) // 30 seconds timeout
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .method(org.jsoup.Connection.Method.HEAD)
                    .execute();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}