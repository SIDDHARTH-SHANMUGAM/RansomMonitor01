package com.ransommonitor.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class TorSeleniumHelper {

    public static WebDriver getTorDriver(int port) {
        WebDriverManager.firefoxdriver().setup();

        FirefoxOptions options = new FirefoxOptions();

        // 💡 This line tells Selenium where Firefox is installed
        options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");

        options.addPreference("network.proxy.type", 1);
        options.addPreference("network.proxy.socks", "127.0.0.1");
        options.addPreference("network.proxy.socks_port", port);
        options.addPreference("network.proxy.socks_remote_dns", true);

        options.addArguments("--headless"); // Optional: don't show browser window
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        return new FirefoxDriver(options);
    }
}
