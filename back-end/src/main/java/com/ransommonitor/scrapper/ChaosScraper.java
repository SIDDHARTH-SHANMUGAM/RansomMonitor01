package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.bean.Victim;
import com.ransommonitor.utils.TorSeleniumHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChaosScraper implements Scraper {

    private String mainUrl;

    @Override
    public List<Attack> scrapeData(String url) {
        this.mainUrl = url;
        int[] torPorts = {9050, 9150};
        List<Attack> attacksList = new ArrayList<>();
        WebDriver driver = null;

        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                driver = TorSeleniumHelper.getTorDriver(port);
                driver.get(mainUrl);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                List<WebElement> victimElements = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("body"))
                );
                for (WebElement victimElement : victimElements) {
                    System.out.println("==> "+victimElement.getText());
                }
                System.out.println("Found " + victimElements.size() + " victim elements.");

                attacksList.addAll(extractDataFromElements(driver, victimElements));
                System.out.println(attacksList);
                return attacksList;

            } catch (Exception e) {
                System.err.println("Error during Selenium scraping with port " + port + ": " + e.getMessage());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
        System.err.println("Failed to scrape data after trying all Tor ports with Selenium.");
        return null;
    }

    private List<Attack> extractDataFromElements(WebDriver driver, List<WebElement> victimElements) {
        List<Attack> attacksList = new ArrayList<>();

        for (WebElement victimElement : victimElements) {
            try {
                Attack attack = new Attack();
                Victim victim = new Victim();
                attack.setVictim(victim);
                attack.setDownloadUrls(new ArrayList<>());
                attack.setImages(new ArrayList<>());

                // Extract Company Name and Onion Link
                WebElement nameLinkElement = victimElement.findElement(By.cssSelector("div.flex-row > div.text-lg.font-semibold.text-white > a"));
                victim.setVictimName(nameLinkElement.getText().trim());
                String onionUrl = nameLinkElement.getAttribute("href");
                if (onionUrl.startsWith("http")) {
                    attack.getDownloadUrls().add(new DownloadUrl(onionUrl));
                }

                // Extract Website URL (if present)
                WebElement websiteLinkElement = null;
                try {
                    websiteLinkElement = victimElement.findElement(By.cssSelector("div.text-sm.text-gray-400 > a"));
                    victim.setVictimURL(websiteLinkElement.getAttribute("href"));
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Website URL not always present
                }

                // Extract Description
                WebElement descriptionElement = victimElement.findElement(By.cssSelector("div.text-sm.text-gray-500"));
                attack.setDescription(descriptionElement.getText().trim());

                // Extract Leaked Size
                WebElement leakedSizeElement = victimElement.findElement(By.xpath(".//div[contains(text(), 'Leaked size:')]/span"));
                if (leakedSizeElement != null) {
                    attack.setDataSizes(leakedSizeElement.getText().trim());
                }

                attack.setUpdatedAt(LocalDateTime.now().toString());
                attack.setCategory("Data Leak");
                attack.setPublished(true);
                attack.setNegotiated(false);
                attack.setRansomAmount(null);
                attack.setSaleAmount(null);
                attack.setLastVisitedAt(null);
                attack.setPostedAt(null);
                attack.setNoOfVisits(0);
                attack.setDeadlines(null);

                attacksList.add(attack);

            } catch (Exception e) {
                System.err.println("Error processing a victim element: " + e.getMessage());
            }
        }

        return attacksList;
    }
}