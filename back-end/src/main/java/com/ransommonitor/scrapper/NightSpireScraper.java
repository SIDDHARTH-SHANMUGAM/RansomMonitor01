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

public class NightSpireScraper implements Scraper {

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
                System.out.println(attacksList);
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

        // Accessing and dividing the cards using select
        Elements companyItems = mainPage.select(".company-item");
        System.out.println("Found " + companyItems.size() + " leaks to process");

        // Iterating each card
        for (Element item : companyItems) {
            Attack attack = new Attack();

            // Skip empty or invalid items
            if (item.select(".name").text().startsWith("Views:")) {
                continue;
            }

            // Set basic attack info
            String name = item.select(".name").text().trim();
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(name);
            String country = "";
            if (matcher.find()) {
                country = matcher.group(1);
            }
            attack.getVictim().setVictimName(name);
            attack.getVictim().setCountry(country);

            // Set URLs and dates
            String vurl = item.select(".url a").attr("href").replace("https://https", "https").replace("http://https", "https");
            attack.getVictim().setVictimURL(vurl);

            attack.setPostedAt(item.select(".hacked_at div:nth-child(2)").text());
            attack.setDeadlines(item.select(".leak_at div:nth-child(2)").text());
            attack.setDataSizes(item.select(".data_size div:nth-child(2)").text());

            // Set status and visits
            String result = item.select(".result").text();
            attack.setPublished(!result.equals("SELLING"));
            attack.setForSale(result.equals("SELLING"));

            try {
                attack.setNoOfVisits(Integer.parseInt(item.select(".buttons .align-items-center").text().trim()));
            } catch (NumberFormatException e) {
                attack.setNoOfVisits(0);
            }

            // Extract more details from the data directory
            String dataDir = item.select("button").attr("onClick")
                    .replace("increaseView(\"", "").replace("\")", "");

            if (!dataDir.isEmpty()) {
                try {
                    String url = mainUrl.replace("datas.php", "") + dataDir.replace("//", "/");
                    Document detailPage = Jsoup.connect(url)
                            .timeout(60000)
                            .proxy(proxy)
                            .get();

                    extractDetailedInfo(attack, detailPage, proxy, url);
                } catch (Exception e) {
                    System.err.println("Error processing " + attack.getVictim().getVictimName() + ": " + e.getMessage());
                }
            }

            attacksList.add(attack);
        }

        System.out.println("Total " + attacksList.size() + " leaks processed");
    }

    private void extractDetailedInfo(Attack attack, Document detailPage, Proxy proxy, String url) {
        // Extract all files and images from the data directory
        Elements files = detailPage.select("tr td a");
        for (Element file : files) {
            String href = file.attr("href");
            String text = file.text();

            // Skip parent directory link
            if (text.equals("Parent Directory")) {
                continue;
            }

            // Determine if it's an image or file
            if (text.matches(".*\\.(png|jpg|jpeg|gif)$")) {
                attack.getImages().add(new Image(url + href));
            } else {
                attack.getDownloadUrls().add(new DownloadUrl(url + href));
            }
        }


        // Set category based on the presence of both files and images
        if (!attack.getImages().isEmpty() && !attack.getDownloadUrls().isEmpty()) {
            attack.setCategory("Double Extortion");
        } else {
            attack.setCategory("Data Leak");
        }
    }
}