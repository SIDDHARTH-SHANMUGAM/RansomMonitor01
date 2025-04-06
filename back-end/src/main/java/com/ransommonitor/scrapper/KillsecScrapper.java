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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KillsecScrapper implements Scrapper {

    private String mainUrl ;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        Elements leakCards = mainPage.select("a[post][href]");
        System.out.println("Found " + leakCards.size() + " leaks to process");

//      Iterating each card
        for (Element postLink : leakCards) {

            Attack attack = new Attack();
            // Victim name from title
            String title = postLink.attr("title");
            attack.getVictim().setVictimName(title.split("-")[0].trim());

            Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(mainPage.text());
            if (sizeMatcher.find()) {
                attack.setDataSizes(sizeMatcher.group(1));
            }

//            // Deadline from countdown script
//            Element countdownScript = postLink.select("script").first();
//            if (countdownScript != null) {
//                String scriptText = countdownScript.html();
//                String timestampStr = scriptText.replaceAll(".*?(\\d{10}).*", "$1");
//                try {
//                    long timestamp = Long.parseLong(timestampStr);
//                    attack.setDeadlines(convertTimestampToDate(timestamp));
//                } catch (NumberFormatException e) {
//                    attack.setDeadlines("Unknown deadline");
//                }
//            }


            // Category from tags
            Elements tags = postLink.select("cat img[title]");
            List<String> tagTitles = new ArrayList<>();
            for (Element tag : tags) {
                tagTitles.add(tag.attr("title"));
            }
            if (tagTitles.contains("Financial Data") && tagTitles.contains("Sensitive Data")) {
                attack.setCategory("Double Extortion");
            } else {
                attack.setCategory("Simple Ransom");
            }


            String postUrl = postLink.absUrl("href");
            Document postPage = Jsoup.connect(postUrl)
                    .timeout(60000)
                    .proxy(proxy)
                    .get();

            extractDetailedInfo(attack, postPage, proxy );
            attacksList.add(attack);
        }
        System.out.println("Total " + attacksList.size() + " leaks to process");
        System.out.println(attacksList);
    }

    private static String convertTimestampToDate(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(DATE_FORMATTER);
    }

    private void extractDetailedInfo(Attack attack, Document detailPage, Proxy proxy) throws IOException {
        Element vpost = detailPage.select("vpost").first();
        if (vpost != null) {
            // Data description (from tags)
            Element descriptionElement = vpost.select("brief card in p").first();
            if (descriptionElement != null) {
                attack.setDescription(descriptionElement.text().trim());
            }


            // Ransom/sale amounts
            Element paymentElement = vpost.select("brief card.rs h1").first();
            if (paymentElement != null) {
                String amount = paymentElement.text();
//                System.out.println(amount);
                if(amount.equals("€???"))
                {
                    attack.setPublished(false);
                    attack.setForSale(false);
                }
                else if(amount.equals("PUBLISHED"))
                {
                    attack.setPublished(true);
                    attack.setForSale(false);
                }
                else{
                    attack.setPublished(false);
                    attack.setForSale(true);
                    attack.setSaleAmount(amount);
                }
            }

            // Negotiation status (check if price is shown)
            attack.setNegotiated(attack.getRansomAmount() != null || attack.getSaleAmount() != null);



            // Extract and store image
            Elements images = vpost.select("gallery img[src]");
            for (Element img : images) {
                attack.getImages().add(new Image(img.absUrl("src")));
            }

            // Download URLs
            for (Element link : vpost.select("disc")) {
                attack.getDownloadUrls().add(new DownloadUrl(link.attr("href")));
            }
        }
    }
}
