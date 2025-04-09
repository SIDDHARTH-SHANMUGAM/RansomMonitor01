package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.bean.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArkanaScraper implements Scraper {

    private String baseUrl;
    private static final int TOR_TIMEOUT_MS = 120000;

    @Override
    public List<Attack> scrapeData(String url) {
        this.baseUrl = url;
        int[] torPorts = {9050, 9150};

        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", port));
                List<Attack> attacksList = new ArrayList<>();
                if (extractHomepageData(proxy, attacksList)) {
                    System.out.println(attacksList);
                    return attacksList;
                }
            } catch (Exception e) {
                System.err.println("Failed with port " + port + ": " + e.getMessage());
            }
        }
        return null;
    }

    private boolean extractHomepageData(Proxy proxy, List<Attack> attacksList) throws IOException {
        Document homePage = Jsoup.connect(baseUrl)
                .timeout(TOR_TIMEOUT_MS)
                .proxy(proxy)
                .get();
        if (homePage == null) {
            return false;
        }

        if (homePage.connection().response().statusCode() != 200) {
            throw new IOException("HTTP Error: " + homePage.connection().response().statusCode());
        }

        Attacker attacker = new Attacker();
        attacker.setAttackerName("Arkana Security");

        Elements postCards = homePage.select("[data-post-card]");
        System.out.println("Found " + postCards.size() + " attack entries");

        for (Element card : postCards) {
            Attack attack = new Attack();
            attack.setAttacker(attacker);

            String victimName = card.select("[data-post-card-title]").text();
            attack.getVictim().setVictimName(victimName);

            String detailUrl = card.select("[data-post-card-title] a").attr("href");
            if (!detailUrl.startsWith("http")) {
                detailUrl = this.baseUrl + detailUrl;
            }

            attack.setPostedAt(card.select("time").attr("datetime"));

            try {
                if (extractDetailedPage(proxy, attack, detailUrl)) {
                    attacksList.add(attack);
                }
            } catch (Exception e) {
                System.err.println("Error processing " + victimName + ": " + e.getMessage());
            }
        }
        return true;
    }

    private boolean extractDetailedPage(Proxy proxy, Attack attack, String detailUrl) throws IOException {
        Document detailPage = Jsoup.connect(detailUrl)
                .timeout(TOR_TIMEOUT_MS)
                .proxy(proxy)
                .get();
        if (detailPage == null) {
            return false;
        }

        // Extract description
        Element descriptionEl = detailPage.selectFirst(".ghost-content");
        if(descriptionEl.text().indexOf("Arkana Security (Аркана Секьюрити)")==0) {
            return false;
        }
        if (descriptionEl != null){
            attack.setDescription(descriptionEl.text());
        }


        // Extract data size
        Element dataSizeEl = detailPage.select("p:contains(Data Extracted)").first();
        if (dataSizeEl != null) {
            attack.setDataSizes(dataSizeEl.text());
        }
        String content = descriptionEl.text();
        System.out.println(content);
        Pattern revenuePattern = Pattern.compile("Revenue:\\s*([\\d.]+\\s*[MB]?)");
        Pattern industryPattern = Pattern.compile("Industry:\\s*(.*?)\\s*(?:Website|COUNTDOWN|\\n)");
        Pattern websitePattern = Pattern.compile("Website:\\s*(\\S+)");
        Pattern countdownPattern = Pattern.compile("The countdown ends on ([A-Za-z]+ \\d{1,2}, \\d{4} at \\d{2}:\\d{2})");

        Matcher matcher;

        matcher = revenuePattern.matcher(content);
        if (matcher.find()) {
            attack.getVictim().setRevenue(matcher.group(1).trim());
        }

        matcher = industryPattern.matcher(content);
        if (matcher.find()) {
            attack.getVictim().setDescription(matcher.group(1).trim());
        }

        matcher = websitePattern.matcher(content);
        if (matcher.find()) {
            attack.getVictim().setVictimURL(matcher.group(1).trim());
        }

        matcher = countdownPattern.matcher(content);
        if (matcher.find()) {
            attack.setDeadlines(matcher.group(1).trim());
        }



        // Determine category
        boolean hasFiles = detailPage.select(".kg-file-card").size() > 0;
        boolean hasImages = detailPage.select(".kg-image-card, .kg-gallery-image").size() > 0;
        attack.setCategory(hasFiles && hasImages ? "Double Extortion" : "Simple Ransom");

        // Extract download URLs
        Elements downloadLinks = detailPage.select(".kg-file-card a");
        for (Element link : downloadLinks) {
            String url = link.attr("href");
            if (!url.startsWith("http")) {
                url = this.baseUrl + url;
            }
            attack.getDownloadUrls().add(new DownloadUrl(url));
        }

        // Extract images
        Elements images = detailPage.select(".kg-image-card img, .kg-gallery-image img");
        for (Element img : images) {
            String src = img.attr("src");
            if (!src.startsWith("http")) {
                src = this.baseUrl + src;
            }
            attack.getImages().add(new Image(src));
        }

        attack.setPublished(true);
        attack.setForSale(detailPage.select("a[href*='tag/sale']").size() > 0);

        return true;
    }


}