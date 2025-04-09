package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Secp0Scraper implements Scraper {

    private String mainUrl;

    @Override
    public List<Attack> scrapeData(String url) {
        this.mainUrl = url;
        int[] torPorts = {9050, 9150};
        List<Attack> attacksList = new ArrayList<>();

        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", port));
                Document mainDoc = Jsoup.connect(mainUrl).timeout(10000).proxy(proxy).get();
                List<String> postUrls = extractPostUrls(mainDoc);

                for (String postUrl : postUrls) {
                    try {
                        Document postDoc = Jsoup.connect(mainUrl + postUrl).timeout(10000).proxy(proxy).get();
                        extractPostDetails(postDoc, attacksList);
                    } catch (IOException e) {
                        System.err.println("Error connecting to post URL: " + mainUrl + postUrl + " - " + e.getMessage());
                    }
                }
                System.out.println(attacksList);
                return attacksList;

            } catch (IOException e) {
                System.err.println("Failed to connect to main URL via Tor port " + port + ": " + e.getMessage());
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
            }
        }
        System.err.println("Failed to scrape data after trying all Tor ports.");
        return null;
    }

    private List<String> extractPostUrls(Document mainPage) {
        List<String> postUrls = new ArrayList<>();
        Elements postElements = mainPage.select("div.post > a.header-a");
        for (Element link : postElements) {
            String href = link.attr("href");
            if (href.startsWith("/post/")) {
                postUrls.add(href);
            }
        }
        System.out.println("Found " + postUrls.size() + " post URLs.");
        return postUrls;
    }

    private void extractPostDetails(Document postPage, List<Attack> attacksList) {
        Elements postElements = postPage.select("div.post");
        System.out.println("Found " + postElements.size() + " detailed post elements on post page.");

        for (Element postElement : postElements) {
            try {
                Attack attack = new Attack();
                Victim victim = new Victim();
                attack.setVictim(victim);
                attack.setDownloadUrls(new ArrayList<>());
                attack.setImages(new ArrayList<>());

                // Extract Title (Victim Name)
                Element titleElement = postElement.selectFirst("h2");
                if (titleElement != null) {
                    victim.setVictimName(titleElement.text().trim());
                }

                // Extract Date
                Element dateElement = postElement.selectFirst("div.date");
                if (dateElement != null) {
                    String dateString = dateElement.text().trim();
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                        LocalDate postedDate = LocalDate.parse(dateString, formatter);
                        attack.setPostedAt(postedDate.toString());
                    } catch (DateTimeParseException e) {
                        System.err.println("Could not parse date: " + dateString + " for victim: " + victim.getVictimName());
                        attack.setPostedAt(LocalDateTime.now().toString());
                    }
                } else {
                    attack.setPostedAt(LocalDateTime.now().toString());
                }

                // Extract Text (Detailed Description) and Images
                Element textElement = postElement.selectFirst("div.text");
                if (textElement != null) {
                    attack.setDescription(textElement.text().trim());

                    // Extract download link
                    Element downloadLink = textElement.selectFirst("a[href^='/files/']");
                    if (downloadLink != null) {
                        String fileUrl = downloadLink.attr("href");
                        attack.getDownloadUrls().add(new DownloadUrl(mainUrl + fileUrl));
                    }

                    // Extract image URLs
                    Elements imageElements = textElement.select("img[src]");
                    for (Element imgElement : imageElements) {
                        String imageUrl = imgElement.attr("src");
                        if (!imageUrl.startsWith("http")) {
                            imageUrl = mainUrl + imageUrl; // Construct absolute URL if necessary
                        }
                        attack.getImages().add(new Image(imageUrl));
                    }
                }

                // Extract potential leaked size from the text (using regex)
                if (attack.getDescription() != null) {
                    Pattern sizePattern = Pattern.compile("Leaked size:?\\s*(\\d+(\\.\\d+)?\\s*(GB|MB|TB))", Pattern.CASE_INSENSITIVE);
                    Matcher sizeMatcher = sizePattern.matcher(attack.getDescription());
                    if (sizeMatcher.find()) {
                        attack.setDataSizes(sizeMatcher.group(1));
                    }
                }

                attack.setUpdatedAt(LocalDateTime.now().toString());
                attack.setCategory("Data Leak Announcement");
                attack.setPublished(true);
                attack.setNegotiated(false);
                attack.setRansomAmount(null);
                attack.setSaleAmount(null);
                attack.setLastVisitedAt(null);
                attack.setNoOfVisits(0);
                attack.setDeadlines(null);

                // Check if an attack for this victim already exists and update or add
                boolean found = false;
                for (Attack existingAttack : attacksList) {
                    if (existingAttack.getVictim().getVictimName().equals(victim.getVictimName())) {
                        if (attack.getDescription() != null) {
                            existingAttack.setDescription(attack.getDescription());
                        }
                        if (!attack.getDownloadUrls().isEmpty()) {
                            existingAttack.getDownloadUrls().addAll(attack.getDownloadUrls());
                        }
                        if (attack.getDataSizes() != null) {
                            existingAttack.setDataSizes(attack.getDataSizes());
                        }
                        if (!attack.getImages().isEmpty()) {
                            existingAttack.getImages().addAll(attack.getImages());
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    attacksList.add(attack);
                }

            } catch (Exception e) {
                System.err.println("Error processing a detailed post element: " + e.getMessage());
            }
        }
    }
}