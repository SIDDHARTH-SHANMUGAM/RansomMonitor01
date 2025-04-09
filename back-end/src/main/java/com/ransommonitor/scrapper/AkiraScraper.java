package com.ransommonitor.scrapper;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.utils.TorSeleniumHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AkiraScraper implements Scraper {

    @Override
    public List<Attack> scrapeData(String url) {
        int[] torPorts = {9050, 9150};
        for (int port : torPorts) {
            try {
                System.out.println("Trying Tor port: " + port);
                WebDriver driver = TorSeleniumHelper.getTorDriver(port);
                driver.get(url);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                WebElement terminalInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("cmd-cursor-line")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", terminalInput);
                Thread.sleep(2000);

                Actions actions = new Actions(driver);
                actions.moveToElement(terminalInput).click().sendKeys("leaks").sendKeys(Keys.ENTER).perform();

                Thread.sleep(10000);

                String pageSource = driver.getPageSource();
                Document mainPage = Jsoup.parse(pageSource);
                List<Attack> attacks = new ArrayList<>();
                extract(mainPage, attacks);

                driver.quit();
                return attacks;
            } catch (Exception e) {
                System.out.println("Failed with port " + port + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private void extract(Document mainPage, List<Attack> attacks) {
        Elements elements = mainPage.select("div[style='width: 100%;']");
        int size=0;
        for (Element element: elements)
        {
            String[] parts = element.text().split("\\s*\\|\\s*");

            if (parts.length == 5) {
                size++;
                Attack temp = new Attack();
                attacks.add(temp);
                temp.getVictim().setVictimName(parts[1]);
                temp.setDescription(parts[2]);

                temp.getDownloadUrls().add(new DownloadUrl(element.select("a[href]").attr("href")));
            }
            if(parts.length == 3) {
                Attack temp = attacks.get(size-1);
                temp.getVictim().setVictimName(temp.getVictim().getVictimName()+" "+parts[1]);
                temp.setDescription(temp.getDescription()+" "+parts[2]);
            }

        }
        System.out.println(attacks);
    }

}
