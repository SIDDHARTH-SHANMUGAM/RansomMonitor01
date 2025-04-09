package com.ransommonitor.utils;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class CaptchaSolver {
    public static String solveCaptcha(String siteKey, String pageUrl) {
        try {
            String apiKey = "";
            String requestUrl = "http://2captcha.com/in.php?key=" + apiKey +
                    "&method=userrecaptcha&googlekey=" + siteKey + "&pageurl=" + pageUrl;

            String response = Jsoup.connect(requestUrl).ignoreContentType(true).execute().body();
            if (!response.startsWith("OK|")) {
                throw new RuntimeException("2Captcha error: " + response);
            }

            String requestId = response.split("\\|")[1];

            for (int i = 0; i < 24; i++) {
                TimeUnit.SECONDS.sleep(5);
                String result = Jsoup.connect("http://2captcha.com/res.php?key=" + apiKey + "&action=get&id=" + requestId)
                        .ignoreContentType(true).execute().body();
                if (result.startsWith("OK|")) {
                    return result.split("\\|")[1];
                } else if (!result.equals("CAPCHA_NOT_READY")) {
                    throw new RuntimeException("2Captcha failed: " + result);
                }
            }

            throw new RuntimeException("Timeout solving CAPTCHA");
        } catch (Exception e) {
            throw new RuntimeException("Error solving CAPTCHA", e);
        }
    }
}
