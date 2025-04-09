package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
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

public class WeyhroScraper implements Scraper {

    private String mainUrl;

    @Override
    public List<Attack> scrapeData(String url) {
        mainUrl = url;
        int[] torPorts = {9050, 9150};
        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", port));
                List<Attack> attacksList = new ArrayList<>();
                extract(proxy, attacksList);
                return attacksList;
            } catch (Exception e) {
                System.err.println("Failed with port " + port + ": " + e);
            }
        }
        return null;
    }

    private void extract(Proxy proxy, List<Attack> attacksList) throws IOException {
        Document mainPage = Jsoup.connect(this.mainUrl)
                .timeout(60000)
                .proxy(proxy)
                .get();

        if (mainPage.connection().response().statusCode() != 200) {
            throw new IOException("HTTP Error: " + mainPage.connection().response().statusCode());
        }

        // Accessing leak cards
        Elements leakCards = mainPage.select("div.overflow-hidden.relative"); // Selector for leak cards
        System.out.println("Found " + leakCards.size() + " leaks to process");

        // Iterating each card
        for (Element card : leakCards) {
            Attack attack = new Attack();

            // Extract basic info from card
            Element linkElement = card.select("a").first();
            if (linkElement == null) continue;

            String detailUrl = linkElement.attr("href");
            if (!detailUrl.startsWith("http")) {
                detailUrl = mainUrl.replace("/leaks", "") + detailUrl;
            }

            attack.getVictim().setVictimName(card.select("h2").text());
            attack.setNoOfVisits(extractViewCount(card.select("span.text-zinc-500").text()));
            attack.setPostedAt(card.select("time").attr("datetime"));

            // Extract ransom amount and country from the card
            String[] metaInfo = card.select("p.text-sm").text().split(",");
            if (metaInfo.length >= 2) {
                attack.getVictim().setRevenue(metaInfo[0].trim());
                attack.getVictim().setCountry(metaInfo[1].trim());
            }

            try {
                Document detailPage = Jsoup.connect(detailUrl)
                        .timeout(60000)
                        .proxy(proxy)
                        .get();

                extractDetailedInfo(attack, detailPage, proxy);
                System.out.println(attack);
                attacksList.add(attack);
            } catch (Exception e) {
                System.err.println("Error processing " + attack.getVictim().getVictimName() + ": " + e.getMessage());
            }
        }
        System.out.println("Total " + attacksList.size() + " leaks processed");
    }

    private void extractDetailedInfo(Attack attack, Document detailPage, Proxy proxy) throws IOException {
        // Set default values
        attack.setCategory("Double Extortion");
        attack.setPublished(true);
        attack.setForSale(false);
        attack.setNegotiated(false);

        // Extract description from header
        Element descriptionElement = detailPage.select("p.mt-6.text-lg").first();
        if (descriptionElement != null) {
            attack.setDescription(descriptionElement.text());
        }

        // Extract critical data sections
        StringBuilder criticalData = new StringBuilder();
        Elements criticalSections = detailPage.select("h2#1-critical-data ~ *");
        for (Element section : criticalSections) {
            if (section.tagName().equals("h2") && !section.id().equals("1-critical-data")) break;
            criticalData.append(section.text()).append("\n");
        }
        attack.setDescription(attack.getDescription() + criticalData.toString().replace("Critical Data", "").trim());

        // Extract geography scope
        StringBuilder geographyData = new StringBuilder();
        Elements geographySections = detailPage.select("h2#2-geography-scope ~ *");
        for (Element section : geographySections) {
            if (section.tagName().equals("h2") && !section.id().equals("2-geography-scope")) break;
            geographyData.append(section.text()).append("\n");
        }

        // Extract time frame
        StringBuilder timeFrameData = new StringBuilder();
        Elements timeFrameSections = detailPage.select("h2#3-time-frame ~ *");
        for (Element section : timeFrameSections) {
            if (section.tagName().equals("h2") && !section.id().equals("3-time-frame")) break;
            timeFrameData.append(section.text()).append("\n");
        }

        // Extract all images
        Elements images = detailPage.select("img.rounded-md");
        for (Element img : images) {
            String imgUrl = img.attr("src");
            if (!imgUrl.startsWith("http")) {
                imgUrl = mainUrl.replace("/leaks","") + imgUrl;
            }
            attack.getImages().add(new Image(imgUrl));
        }

        // Extract download links
        Elements downloadLinks = detailPage.select("div.grid a[target=_blank]");
        for (Element link : downloadLinks) {
            String href = link.attr("href");
            if (href.contains("onion")) { // Only include Tor links
                attack.getDownloadUrls().add(new DownloadUrl(href));
            }
        }

        // Extract victim website
        Elements websiteLinks = detailPage.select("div.grid a[target=_blank]");
        for (Element link : websiteLinks) {
            String href = link.attr("href");
            if (!href.contains("onion")) {
                attack.getVictim().setVictimURL(href);
                break;
            }
        }

    }

    private int extractViewCount(String text) {
        try {
            Matcher matcher = Pattern.compile("([\\d.]+)K").matcher(text);
            if (matcher.find()) {
                double value = Double.parseDouble(matcher.group(1));
                return (int) (value * 1000);
            }
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}