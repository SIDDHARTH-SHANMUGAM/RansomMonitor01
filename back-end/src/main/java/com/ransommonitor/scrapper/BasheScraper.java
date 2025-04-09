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

public class BasheScraper implements Scraper {
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
        Elements leakCards = mainPage.select(".segment");
        System.out.println("Found " + leakCards.size() + " leaks to process");

//      Iterating each card
        for (Element segment : leakCards) {

            Attack attack = new Attack();

            Element companyElement = segment.select(".segment__text__off").first();
            attack.getVictim().setVictimName(companyElement != null ? companyElement.text() : "Unknown");

            // Extract country
            Element countryElement = segment.select(".segment__country__deadline").first();
            attack.getVictim().setCountry(countryElement != null ? countryElement.text() : "Unknown");



            // Extract status (Published/Not Published)
            Element statusElement = segment.select(".segment__block.published").first();
            attack.setPublished(statusElement != null && statusElement.text().equals("Published"));

            // Extract detail page URL from onclick handler
            String moreDetails="";
            String onclick = segment.attr("onclick");
            if (onclick != null && !onclick.isEmpty()) {
                moreDetails = onclick.replace("window.location.href='", "").replace("'", "");
            }

            try {
                Document detailPage = Jsoup.connect(mainUrl + moreDetails)
                        .timeout(60000)
                        .proxy(proxy)
                        .get();

                extractDetailedInfo(attack, detailPage, proxy);
                attacksList.add(attack);
            } catch (Exception e) {
                           System.err.println("Error processing " + attack.getVictim().getVictimName()+ ": " + e.getMessage());
            }
        }
        System.out.println("Total " + attacksList.size() + " leaks to process");
    }


    private void extractDetailedInfo(Attack attack, Document detailPage, Proxy proxy) throws IOException {

        Element deadlineElement = detailPage.select(".deadline:contains(Deadline:)").first();
        if (deadlineElement != null) {
            String deadlineText = deadlineElement.text().replace("Deadline:", "").trim();
            attack.setDeadlines(deadlineText);
        }

        // Extract views count from detail page
        Element viewsElement = detailPage.select(".deadline:contains(Views:)").first();
        if (viewsElement != null) {
            attack.setNoOfVisits(Integer.parseInt(viewsElement.text().replace("Views:", "").trim()));
        }

        // Extract description
        Element descElement = detailPage.selectFirst("div.dsc__text");
        attack.setDescription(descElement.text());


        // Check if data is for sale (looking for "SOLD" in title)
        attack.setForSale(false);

        // Extract data size
        Matcher sizeMatcher = Pattern.compile("(\\d+\\.?\\d*\\s*(TB|GB|MB|KB|tb|gb|kb|mb))").matcher(detailPage.text());
        if (sizeMatcher.find()) {
            attack.setDataSizes(sizeMatcher.group(1));
        }


        // Determine category
        attack.setCategory("Not Mentioned");

        // Check publication status
        attack.setPublished(!detailPage.select("div.download-links a").isEmpty());




        // Check sale status
        attack.setForSale(true);
        for (Element link : detailPage.select(".block__published table:first-of-type a")) {
            attack.getDownloadUrls().add(new DownloadUrl(link.attr("href")));
        }


        // Extract and store image
        Element imgElement = detailPage.select(".photo__comm").first();
        if (imgElement != null) {
            String imgUrl = imgElement.attr("src");

            if (!imgUrl.startsWith("http")) {
                imgUrl =  mainUrl+ imgUrl;
            }

            attack.getImages().add(new Image(imgUrl));
        }
    }

}
