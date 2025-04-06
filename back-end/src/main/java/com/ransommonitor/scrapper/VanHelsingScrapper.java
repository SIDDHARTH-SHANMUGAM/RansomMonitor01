package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.DownloadUrl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VanHelsingScrapper implements Scrapper {

    private String mainUrl;

    @Override
    public List<Attack> scrapData(String url) {

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
                System.out.println("Failed with port " + port + ": " + e.getMessage());
            }
        }
        return null;
    }

    private void extract(Proxy proxy, List<Attack> attacksList) throws IOException, SQLException {
        Document mainPage = Jsoup.connect(this.mainUrl)
                .timeout(60000)
                .proxy(proxy)
                .get();

        if (mainPage.connection().response().statusCode() != 200) {
            throw new IOException("HTTP Error: " + mainPage.connection().response().statusCode());
        }


//      Accessing and dividing the cards using select
        Elements leakCards = mainPage.select("div.card");
        System.out.println("Found " + leakCards.size() + " leaks to process");

//      Iterating each card
        for (Element entry : leakCards) {

            Attack attack = new Attack();
            // Extract company name
            Element titleElement = entry.select("h3.mt-0 a").first();
            if (titleElement != null) {
                attack.getVictim().setVictimName(titleElement.text().trim());
            }

            // Extract status
            Element statusElement = entry.select("p.font-15").first();
            if (statusElement != null) {
                String status = statusElement.text().toLowerCase();
                attack.setPublished(status.contains("published"));
                attack.setForSale(status.contains("for sale"));
            }

            // Extract description
            Element descElement = entry.select("p.text-muted a").first();
            if (descElement != null) {
                attack.setDescription(descElement.text());
            }

            // Extract read more link
            if (titleElement != null) {
                String postUrl = titleElement.attr("href");

                Document postPage = Jsoup.connect(mainUrl.replace("index.php","")+postUrl)
                        .timeout(60000)
                        .proxy(proxy)
                        .get();
                Elements downloadLinks = postPage.select("a[href*='.tar.gz'], a[href*='.zip']");
                for (Element link : downloadLinks) {
                    String downloadUrl = link.attr("href");
                    if (!downloadUrl.startsWith("http")) {
                        downloadUrl = mainUrl + downloadUrl;
                    }
                    attack.getDownloadUrls().add(new DownloadUrl(downloadUrl));
                }
                attacksList.add(attack);
            }
        }
        System.out.println("Total " + attacksList.size() + " leaks to process");
        System.out.println(attacksList);
    }

}
