package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.bean.Image;
import com.ransommonitor.bean.Victim;
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

public class SafePayScraper implements Scraper {

    private String mainUrl;

    @Override
    public List<Attack> scrapeData(String url) {
        this.mainUrl = url;
        int[] torPorts = {9050, 9150};

        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", port));
                List<Attack> attacksList = new ArrayList<>();
                Document doc = Jsoup.connect(this.mainUrl).timeout(6000).proxy(proxy).get();
                extract(doc,  attacksList);
                System.out.println(attacksList);
                return attacksList;
            } catch (Exception e) {
                System.err.println("Failed with port " + port + ": " + e.getMessage());
            }
        }
        return null;
    }

    private void extract(Document mainPage, List<Attack> attacksList) {
        Elements articles = mainPage.select("#main article");
        System.out.println("Found " + articles.size() + " articles to process");

        for (Element article : articles) {
            Attack attack = new Attack();
            Victim victim = new Victim();
            List<DownloadUrl> downloadUrls = new ArrayList<>();
            List<Image> images = new ArrayList<>();
            attack.setVictim(victim);
            attack.setDownloadUrls(downloadUrls);
            attack.setImages(images);

            String victimNameWithPossibleRevenue = article.select("h2").text();
            victim.setVictimName(victimNameWithPossibleRevenue);
            if(victim.getVictimName().equals("Contact")) {
                return;
            }
            String revenueText = "";
            Element revenueElement = article.select("h3:contains(Revenue)").first();
            if (revenueElement != null) {
                revenueText = revenueElement.text().replace("Revenue", "").trim();
                victim.setRevenue(revenueText);
            }

            String zipSizeText = "";
            Element zipElement = article.select("h3:contains(ZIP)").first();
            if (zipElement != null) {
                zipSizeText = zipElement.text().replace("ZIP -", "").trim();
                attack.setDataSizes(zipSizeText);
            }

            // Extract download listing URL
            Element downloadListingElement = article.select("h3 a:contains(Download Listing)").first();
            if (downloadListingElement != null) {
                String downloadListingUrl = downloadListingElement.attr("href");
                if (!downloadListingUrl.isEmpty() && !downloadListingUrl.equals("#")) {
                    attack.getDownloadUrls().add(new DownloadUrl(mainUrl+downloadListingUrl));
                }
            }

            // Extract view data URL (likely on a .onion address)
            Element viewDataElement = article.select("h3 a:contains(View Data)").first();
            if (viewDataElement != null) {
                String viewDataUrl = viewDataElement.attr("href");
                if (!viewDataUrl.isEmpty() && !viewDataUrl.equals("#")) {
                    attack.getDownloadUrls().add(new DownloadUrl(viewDataUrl));
                }
            }

            // Attempt to extract country from the victim name (assuming format like 'victim.com (US)')
            Pattern countryPattern = Pattern.compile("\\(([^)]+)\\)$");
            Matcher countryMatcher = countryPattern.matcher(victim.getVictimName());
            if (countryMatcher.find()) {
                victim.setCountry(countryMatcher.group(1));
                victim.setVictimName(victim.getVictimName().replace(" (" + victim.getCountry() + ")", "").trim());
            }

            // SafePay site doesn't explicitly provide deadlines, description, isNegotiated, ransomAmount, saleAmount, lastVisitedAt
            attack.setDeadlines(null);
            attack.setDescription(null);
            attack.setNegotiated(false);
            attack.setRansomAmount(null);
            attack.setSaleAmount(null);
            attack.setLastVisitedAt(null);
            attack.setPostedAt(null); // Posted at isn't clearly available per victim
            attack.setNoOfVisits(0); // Number of visits isn't directly available

            // Determine category - based on available download links, assuming data leak if links are present
            if (!attack.getDownloadUrls().isEmpty()) {
                attack.setCategory("Data Leak"); // Assuming if they provide download/view links, it's a leak
                attack.setPublished(true); // If data is available, it's likely published
                attack.setForSale(false); // Not explicitly stated as for sale
            } else {
                attack.setCategory("Simple Ransom"); // If no download links, assume simple ransom (though less info available)
                attack.setPublished(false);
                attack.setForSale(false);
            }

            attack.setUpdatedAt(java.time.LocalDateTime.now().toString());

            attacksList.add(attack);
        }
    }
}