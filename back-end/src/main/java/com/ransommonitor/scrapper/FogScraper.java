package com.ransommonitor.scrapper;

import com.ransommonitor.bean.*;
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

public class FogScraper implements Scraper{
    private static String BASE_URL ;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; rv:91.0) Gecko/20100101 Firefox/91.0";

    public List<Attack> scrapeData(String url) {
        List<Attack> attacks = new ArrayList<>();
        BASE_URL = url;
        try {
            // Set up Tor proxy
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 9150));

            // Get main page with list of attacks
            Document mainPage = Jsoup.connect(BASE_URL)
                    .timeout(60000)
                    .proxy(proxy)
                    .userAgent(USER_AGENT)
                    .get();

            // Extract all attack cards
            Elements attackCards = mainPage.select(".mb-4.basis-1.last\\:mb-0");

            for (Element card : attackCards) {
                try {
                    Attack attack = new Attack();

                    // Extract basic info from card
                    extractBasicInfo(card, attack);

                    // Follow link to detailed page
                    String detailUrl = card.select("a").first().attr("href");
                    Document detailPage = Jsoup.connect(BASE_URL.replace("/posts/", "") + detailUrl)
                            .timeout(60000)
                            .proxy(proxy)
                            .userAgent(USER_AGENT)
                            .get();

                    // Extract detailed info
                    extractDetailedInfo(detailPage, attack);
                    System.out.println(attack);
                    attacks.add(attack);
                } catch (Exception e) {
                    System.err.println("Error processing attack card: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to site: " + e.getMessage());
        }

        return attacks;
    }

    private void extractBasicInfo(Element card, Attack attack) {
        // Victim name
        String victimName = card.select("p.text-lg.font-bold").text();
        attack.getVictim().setVictimName(victimName);

        // Posted date
        String postedAt = card.select("p:contains(Mon,), p:contains(Tue,), p:contains(Wed,), " +
                "p:contains(Thu,), p:contains(Fri,), p:contains(Sat,), " +
                "p:contains(Sun,)").text();
        attack.setPostedAt(postedAt);

        // Data size
        String dataSize = card.select("p.line-clamp-6.pt-4").text();
        attack.setDataSizes(dataSize);
    }

    private void extractDetailedInfo(Document detailPage, Attack attack) {
        // Attack description
        Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(detailPage.text());
        if (sizeMatcher.find()) {
            attack.setDataSizes(sizeMatcher.group(1));
        }

        String description = detailPage.select("article p").text();
        attack.setDescription(description);

        // Revenue (if available)
        Element revenueElement = detailPage.select("p:contains(Revenue:)").first();
        if (revenueElement != null) {
            String revenueText = revenueElement.text().replace("Revenue:", "").trim();
            attack.getVictim().setRevenue(revenueText);
        }
        // Data categories
        Elements categoryElements = detailPage.select("p:contains(Categories of files found:)");
        if (!categoryElements.isEmpty()) {
            StringBuilder categories = new StringBuilder();
            Element next = categoryElements.first().nextElementSibling();
            while (next != null && !next.tagName().equals("p")) {
                categories.append(next.text()).append(", ");
                next = next.nextElementSibling();
            }
            attack.setDescription(attack.getDescription() + "\nCategories: " + categories.toString());
        }

        // Download URLs (torrent links)
        Elements downloadLinks = detailPage.select("a[href*=.torrent]");
        for (Element link : downloadLinks) {
            String url = link.attr("href");
            attack.getDownloadUrls().add(new DownloadUrl(0, attack.getAttackId(), url, ""));
        }

        attack.setPublished(true);
        attack.setForSale(false);
        attack.setNegotiated(false);
        attack.setLastVisitedAt("");

    }

}