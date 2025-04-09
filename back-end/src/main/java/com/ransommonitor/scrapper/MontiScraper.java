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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MontiScraper implements Scraper {

    private String mainUrl ;
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

    private void extract(Proxy proxy, List<Attack> attacksList) throws IOException, SQLException {
        Document mainPage = Jsoup.connect(this.mainUrl)
                .timeout(60000)
                .proxy(proxy)
                .get();

        if (mainPage.connection().response().statusCode() != 200) {
            throw new IOException("HTTP Error: " + mainPage.connection().response().statusCode());
        }


//      Accessing and dividing the cards using select
        Elements leakCards = mainPage.select(".leak-card");
        System.out.println("Found " + leakCards.size() + " leaks to process");

//      Iterating each card
        for (Element card : leakCards) {

            Attack attack = new Attack();
            if(card.select("h5").text().toString().equals("\uD83D\uDE80 Launch Your Own Ransomware(RAAS) Business with Our Exclusive Ransomware Panel Source Code! \uD83D\uDCB0"))
                continue;

            if(card.select("h5").text().toString().equals("Hello! world by ( Babuk Locker )"))
                continue;
            attack.getVictim().setVictimName(card.select("h5").text());
            attack.setNoOfVisits(Integer.parseInt(card.select(".col-auto span").text()));
            attack.setDeadlines(card.select(".published").text());
            String moreDetails = card.attr("href");
            try {
                Document detailPage = Jsoup.connect(mainUrl.replace(".onion/",".onion") + moreDetails)
                        .timeout(60000)
                        .proxy(proxy)
                        .get();

                extractDetailedInfo(attack, detailPage, proxy);
                System.out.println(attack);
                attacksList.add(attack);
            } catch (Exception e) {
                System.err.println("Error processing " + attack.getVictim().getVictimName()+ ": " + e.getMessage());
            }
        }
        System.out.println("Total " + attacksList.size() + " leaks to process");
        System.out.println(attacksList);
    }



    private void extractDetailedInfo(Attack attack, Document detailPage, Proxy proxy) throws IOException {
        // Extract data size
        Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(detailPage.text());
        if (sizeMatcher.find()) {
            attack.setDataSizes(sizeMatcher.group(1));
        }

        // Extract data description
        StringBuilder description = new StringBuilder();
        for (Element p : detailPage.select("div.content-info p")) {
            String text = p.text().toLowerCase();
            if (text.contains("stolen") || text.contains("data") || text.contains("information")) {
                description.append(p.text()).append("\n");
            }
        }
        attack.setDescription(description.toString());

        // Determine category
        attack.setCategory("Double extortion Ransomware");

        // Check publication status
        attack.setPublished(!detailPage.select("div.download-links a").isEmpty());

        // Check sale status
        attack.setForSale(true);

        for (Element link : detailPage.select("div.download-links a")) {
            attack.getDownloadUrls().add(new DownloadUrl(link.attr("href")));
        }


        // Extract and store image
        Element imgElement = detailPage.select("div.content-info img").first();
        if (imgElement != null) {
            String imgUrl = imgElement.attr("src");

            if (!imgUrl.startsWith("http")) {
                imgUrl =  mainUrl+ imgUrl;
            }

            attack.getImages().add(new Image(imgUrl));
        }
    }

}
