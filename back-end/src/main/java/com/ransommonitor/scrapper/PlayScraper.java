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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayScraper implements Scraper {
    private String baseUrl;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final Pattern DATA_SIZE_PATTERN = Pattern.compile("amount of data:\\s*(\\d+(\\.\\d+)?)\\s*(TB|GB|MB|KB)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MONEY_PATTERN = Pattern.compile("\\$(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)");

    @Override
    public List<Attack> scrapeData(String url) {
        this.baseUrl = url.replaceAll("/index\\.php.*", "");
        int[] torPorts = { 9150, 9050};

        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", port));
                List<Attack> attacksList = new ArrayList<>();
                processAllPages(proxy, attacksList);
                return attacksList;
            } catch (Exception e) {
                System.out.println("Failed with port " + port + ": " + e.getMessage());
            }
        }
        return new ArrayList<>(); // Return empty list instead of null
    }

    private void processAllPages(Proxy proxy, List<Attack> attacksList) throws IOException, ParseException {
        int currentPage = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            String pageUrl = currentPage == 1 ?
                    baseUrl :
                    baseUrl + "index.php?page=" + currentPage;

            System.out.println("Processing page: " + pageUrl);

            Document page;
            try {
                System.out.println("Trying to get page: " + pageUrl);
                page = Jsoup.connect(pageUrl)
                        .timeout(60000)
                        .proxy(proxy)
                        .get();

                if (page.connection().response().statusCode() != 200) {
                    System.err.println("HTTP Error: " + page.connection().response().statusCode());
                    break;
                }
            } catch (IOException e) {
                System.err.println("Failed to fetch page " + currentPage + ": " + e.getMessage());
                break;
            }

            processPageContent(page, proxy, attacksList);

            // Check pagination - look for next page number
            Elements paginationLinks = page.select("th span.page");
            hasMorePages = false;
            System.out.println(paginationLinks.toString());
            for (Element pageLink : paginationLinks) {
                try {
                    int pageNum = Integer.parseInt(pageLink.text());
                    if (pageNum > currentPage) {
//                        hasMorePages = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                }
            }

            currentPage++;

            // Safety limit
            if (currentPage > 100) {
                System.err.println("Reached maximum page limit (100)");
                break;
            }
        }
    }

    private void processPageContent(Document page, Proxy proxy, List<Attack> attacksList) {
        Elements newsItems = page.select("th.News");
        System.out.println("Found " + newsItems.size() + " leaks on page");

        for (Element newsItem : newsItems) {
            Attack attack = new Attack();
            attack.getAttacker().setAttackerName("PLAY");

            try {
                extractBasicInfo(newsItem, attack);

                String topicId = extractTopicId(newsItem);
                if (topicId != null) {
                    extractDetailedInfo(attack, topicId, proxy);
                }

                attacksList.add(attack);
            } catch (Exception e) {
                System.err.println("Error processing item: " + e.getMessage());
            }
            System.out.println(attack);
        }
    }

    private String extractTopicId(Element newsItem) {
        String onclick = newsItem.attr("onclick");
        if (onclick != null && onclick.startsWith("viewtopic(")) {
            try {
                int start = onclick.indexOf("'") + 1;
                int end = onclick.lastIndexOf("'");
                if (start > 0 && end > start) {
                    return onclick.substring(start, end);
                }
            } catch (Exception e) {
                System.err.println("Error extracting topic ID: " + e.getMessage());
            }
        }
        return null;
    }

    private void extractBasicInfo(Element newsItem, Attack attack) throws ParseException {
        try {
            // Victim name extraction
            Element locationIcon = newsItem.select("i.location").first();
            String country="";
            if (locationIcon != null) {
                country = locationIcon.nextSibling().toString().replace("&nbsp;", "").trim();
                attack.getVictim().setCountry(country.isEmpty() ? "Unknown" : country);
            }


            String victimText = newsItem.selectFirst("th.News").text().split(country)[0];
            String[] lines = victimText.split("\n");
            attack.getVictim().setVictimName(lines[0].trim());


            // Website extraction
            Element linkIcon = newsItem.select("i.link").first();
            if (linkIcon != null) {
                String website = linkIcon.nextSibling().toString().trim();
                attack.getVictim().setVictimURL(website.isEmpty() ? "" : website.replace("&nbsp;", ""));
            }

            if(victimText.contains("views:")) {
                attack.setNoOfVisits(Integer.parseInt(victimText.split(" views:")[1].split(" ")[0]));
            }

            if(victimText.contains("added:")) {
                attack.setPostedAt(victimText.split(" added: ")[1].split(" ")[0]);
            }

            if(victimText.contains("publication date:")) {
                attack.setPostedAt(victimText.split(" publication date: ")[1].split(" ")[0]);
            }


            // Default values
            attack.setCategory("Double Extortion");
            attack.setForSale(false);
            attack.setNegotiated(false);
            attack.setLastVisitedAt(new Date().toString());

        } catch (Exception e) {
            System.err.println("Error extracting basic info: " + e.getMessage());
            attack.getVictim().setVictimName("Unknown");
            attack.getVictim().setCountry("Unknown");
            attack.setNoOfVisits(0);
            attack.setPostedAt(new Date().toString());
        }
    }

    private void extractDetailedInfo(Attack attack, String topicId, Proxy proxy) {
        String detailUrl = baseUrl + "topic.php?id=" + topicId;
        Document detailPage;
        try {
            detailPage = Jsoup.connect(detailUrl)
                    .timeout(60000)
                    .proxy(proxy)
                    .get();
        } catch (IOException e) {
            System.err.println("Failed to fetch details for topic " + topicId + ": " + e.getMessage());
            return;
        }
        Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(detailPage.text());
        if (sizeMatcher.find()) {
            attack.setDataSizes(sizeMatcher.group(1));
        }

        // Extract information section
        Element infoElement = detailPage.select("div:contains(information:)").first();
        if (infoElement != null) {
            String infoText = infoElement.text().replace("information:", "").trim();
            System.out.println(infoText);
            attack.setDescription(infoText);

            List<String> urls = extractOnionLinks(infoText);

            for (String url : urls) {
                attack.getDownloadUrls().add(new DownloadUrl(url));
            }

            // Try to extract ransom amount from description
            Matcher moneyMatcher = MONEY_PATTERN.matcher(infoText);
            if (moneyMatcher.find()) {
                attack.setRansomAmount(moneyMatcher.group());
            }
        }

        // Extract comment section
        Element commentElement = detailPage.select("div:contains(comment:)").first();
        if (commentElement != null) {
            String commentText = commentElement.text().replace("comment:", "").trim();
            attack.setDescription(commentText);
            System.out.println(commentText);
            List<String> urls = extractOnionLinks(commentText);

            for (String url : urls) {
                attack.getDownloadUrls().add(new DownloadUrl(url));
            }
            // Check for negotiation/sale status
            String lowerComment = commentText.toLowerCase();
            attack.setNegotiated(lowerComment.contains("negotiat") || lowerComment.contains("discuss"));
            attack.setForSale(lowerComment.contains("for sale") || lowerComment.contains("part of the data"));
        }

        // Extract images
        Elements imgElements = detailPage.select("img");
        for (Element img : imgElements) {
            String imgUrl = img.absUrl("src");
            if (!imgUrl.isEmpty()) {
                attack.getImages().add(new Image(imgUrl));
            }
        }

        // Extract download links
        Elements downloadLinks = detailPage.select("a[href*='download']");
        for (Element link : downloadLinks) {
            String downloadUrl = link.absUrl("href");
            if (!downloadUrl.isEmpty()) {
                attack.getDownloadUrls().add(new DownloadUrl(downloadUrl));
                attack.setForSale(true);
            }
        }

        // Set update time
        attack.setUpdatedAt(new Date().toString());
    }
    public static List<String> extractOnionLinks(String text) {
        List<String> onionLinks = new ArrayList<>();
        System.out.println("here");
        // Regex to find .onion links
        Pattern pattern = Pattern.compile("http://[a-z2-7]{16,56}\\.onion/[a-zA-Z0-9/]*");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            onionLinks.add(matcher.group());
        }
        return onionLinks;
    }
}