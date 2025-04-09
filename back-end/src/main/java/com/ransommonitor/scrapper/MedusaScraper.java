package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.bean.Image;
import com.ransommonitor.bean.Victim;
import com.ransommonitor.utils.CaptchaSolver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedusaScraper implements Scraper {

    private static final Logger logger = Logger.getLogger(MedusaScraper.class.getName());
    private String mainUrl;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Attack> scrapeData(String url) {
        this.mainUrl = url;
        int[] torPorts = {9050, 9150};
        for (int port : torPorts) {
            try {
                logger.info("Trying Tor port: " + port);
                List<Attack> attacksList = new ArrayList<>();
                extractUsingSelenium(port, attacksList);
                return attacksList;
            } catch (Exception e) {
                logger.warning("Failed with Tor port " + port + ": " + e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    private void extractUsingSelenium(int torPort, List<Attack> attacksList) throws Exception {
        // Configure Tor proxy for Selenium
        Proxy proxy = new Proxy();
        proxy.setSocksProxy("127.0.0.1:" + torPort);
        proxy.setSocksVersion(5);

        ChromeOptions options = new ChromeOptions();
        options.setProxy(proxy);
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(mainUrl);
            WebDriverWait wait = new WebDriverWait(driver , Duration.ofSeconds(600));

            // Wait for page or CAPTCHA to load
            if (driver.getPageSource().contains("g-recaptcha")) {
                logger.warning("CAPTCHA detected, solving...");

                // Extract sitekey
                WebElement captchaElement = driver.findElement(By.cssSelector(".g-recaptcha"));
                String siteKey = captchaElement.getAttribute("data-sitekey");

                String token = CaptchaSolver.solveCaptcha(siteKey, mainUrl);
                logger.info("CAPTCHA solved with token: " + token);

                // Inject token using JS
                String script = "document.getElementById('g-recaptcha-response').innerHTML='" + token + "';" +
                        "document.querySelector('form').submit();";
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script);

                // Wait for content
                Thread.sleep(10000);  // allow redirect
            }

            // Use Jsoup to parse rendered HTML
            Document doc = Jsoup.parse(driver.getPageSource());
            parseLeakedData(doc, attacksList);

        } finally {
            driver.quit();
        }
    }

    private void parseLeakedData(Document mainPage, List<Attack> attacksList) throws Exception {
        String currentTimestamp = dateFormat.format(new Date());
        System.out.println(mainPage.text());
        Elements leakCards = mainPage.select("#list-card-container .card");
        logger.info("Found " + leakCards.size() + " leaks to process");

        for (Element card : leakCards) {
            Attack attack = new Attack();
            attack.setUpdatedAt(currentTimestamp);
            attack.setLastVisitedAt(currentTimestamp);

            Victim victim = new Victim();
            victim.setVictimName(card.select(".card-title").text());
            attack.setVictim(victim);

            Element visitElement = card.select(".visit-count").first();
            if (visitElement != null) {
                try {
                    attack.setNoOfVisits(Integer.parseInt(visitElement.text().replaceAll("[^0-9]", "")));
                } catch (NumberFormatException e) {
                    attack.setNoOfVisits(0);
                }
            }

            Element dateElement = card.select(".date").first();
            if (dateElement != null) {
                String dateText = dateElement.text();
                attack.setDeadlines(dateText);
                attack.setPostedAt(dateText);
            }

            attack.setPublished(true);
            attack.setForSale(true);
            attack.setNegotiated(false);

            String moreDetails = card.select(".card-link").attr("href");
            if (!moreDetails.startsWith("http")) {
                moreDetails = mainUrl + moreDetails;
            }

            try {
                Document detailPage = Jsoup.connect(moreDetails)
                        .timeout(60000)
                        .proxy(new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9050)))
                        .userAgent("Mozilla/5.0")
                        .get();

                extractDetailedInfo(attack, detailPage);
                attacksList.add(attack);
            } catch (Exception e) {
                logger.warning("Error processing detail page: " + e.getMessage());
            }
        }

        logger.info("Total " + attacksList.size() + " leaks processed");
    }

    private void extractDetailedInfo(Attack attack, Document detailPage) {
        Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(detailPage.text());
        if (sizeMatcher.find()) {
            attack.setDataSizes(sizeMatcher.group(1));
        }

        StringBuilder description = new StringBuilder();
        Elements contentElements = detailPage.select("#container p, #container div");
        for (Element element : contentElements) {
            String text = element.text().trim();
            if (!text.isEmpty()) {
                description.append(text).append("\n");
            }
        }
        attack.setDescription(description.toString());
        attack.setCategory("Double extortion Ransomware");
        extractFinancialInfo(attack, detailPage);

        if (detailPage.text().toLowerCase().contains("negotiation") ||
                detailPage.text().toLowerCase().contains("contact us")) {
            attack.setNegotiated(true);
        }

        Elements downloadLinks = detailPage.select("a[href*='download']");
        for (Element link : downloadLinks) {
            String href = link.attr("href");
            if (!href.startsWith("http")) {
                href = mainUrl + href;
            }
            attack.getDownloadUrls().add(new DownloadUrl(href));
        }

        Elements imgElements = detailPage.select("#container img");
        for (Element imgElement : imgElements) {
            String imgUrl = imgElement.attr("src");
            if (!imgUrl.startsWith("http")) {
                imgUrl = mainUrl + imgUrl;
            }
            attack.getImages().add(new Image(imgUrl));
        }
    }

    private void extractFinancialInfo(Attack attack, Document detailPage) {
        Matcher ransomMatcher = Pattern.compile(
                "(ransom|demand|price)[^\\d]*([\\d,]+(\\.\\d{1,2})?\\s*(USD|usd|\\$|BTC|btc))",
                Pattern.CASE_INSENSITIVE
        ).matcher(detailPage.text());

        if (ransomMatcher.find()) {
            attack.setRansomAmount(ransomMatcher.group(2));
        }

        Matcher saleMatcher = Pattern.compile(
                "(sale|buy|purchase)[^\\d]*([\\d,]+(\\.\\d{1,2})?\\s*(USD|usd|\\$|BTC|btc))",
                Pattern.CASE_INSENSITIVE
        ).matcher(detailPage.text());

        if (saleMatcher.find()) {
            attack.setSaleAmount(saleMatcher.group(2));
            attack.setForSale(true);
        }
    }
}
