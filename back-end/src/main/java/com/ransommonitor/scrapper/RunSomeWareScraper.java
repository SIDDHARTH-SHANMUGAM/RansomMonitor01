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

public class RunSomeWareScraper implements Scraper {

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
        Elements leakCards = mainPage.select(".card");
        System.out.println("Found " + leakCards.size() + " cards to process");

        // Iterating each card
        for (Element card : leakCards) {
            Attack attack = new Attack();

            // Set attacker name (same for all victims)
            attack.getAttacker().setAttackerName("Run Some Wares");

            // Extract victim name
            Element titleElement = card.select(".card-title").first();
            if (titleElement != null) {
                attack.getVictim().setVictimName(titleElement.text());
            }

            // Extract description
            Element descriptionElement = card.select(".card-text").first();
            if (descriptionElement != null) {
                attack.setDescription(descriptionElement.text());
            }


            // Extract logo image
            Element logoElement = card.select(".card-logo").first();
            if (logoElement != null) {
                String logoUrl = logoElement.attr("src");
                if (!logoUrl.startsWith("http")) {
                    logoUrl = mainUrl + logoUrl;
                }
                attack.getImages().add(new Image(logoUrl));
            }

            // Set default values for other fields
            attack.setCategory("Double Extortion");
            attack.setPublished(true);
            attack.setForSale(false);
            attack.setNegotiated(false);

            attacksList.add(attack);
        }

        System.out.println("Total " + attacksList.size() + " victims processed");
    }
}