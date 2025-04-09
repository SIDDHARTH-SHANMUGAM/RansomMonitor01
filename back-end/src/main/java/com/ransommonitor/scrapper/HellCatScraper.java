    package com.ransommonitor.scrapper;

    import com.ransommonitor.bean.Attack;
    import com.ransommonitor.bean.DownloadUrl;
    import com.ransommonitor.bean.Image;
    import com.ransommonitor.bean.Victim;
    import com.ransommonitor.utils.TorSeleniumHelper;
    import org.openqa.selenium.By;
    import org.openqa.selenium.WebDriver;
    import org.openqa.selenium.WebElement;
    import org.openqa.selenium.support.ui.ExpectedConditions;
    import org.openqa.selenium.support.ui.WebDriverWait;

    import java.time.Duration;
    import java.util.ArrayList;
    import java.util.List;

    public class HellCatScraper implements Scraper {

        private String mainUrl;

        @Override
        public List<Attack> scrapeData(String url) {
            this.mainUrl = url;
            int[] torPorts = {9050, 9150};
            List<Attack> attacksList = new ArrayList<>();
            WebDriver driver = null;
            for(int port : torPorts) {
                try {

                    System.out.println("Trying Tor port: " + port);
                    driver = TorSeleniumHelper.getTorDriver(port);
                    driver.get(mainUrl);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
                    List<WebElement> victimCards = wait.until(
                            ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#victim-cards .col-12"))
                    );
                    System.out.println("Found " + victimCards.size() + " victim cards");

                    for (WebElement cardContainer : victimCards) {
                        try {

                            WebElement card = cardContainer.findElement(By.cssSelector(".card-custom.victim-card"));
                            Attack attack = new Attack();
                            Victim victim = attack.getVictim();

                            WebElement titleElement = card.findElement(By.cssSelector(".card-title"));
                            victim.setVictimName(titleElement.getText().trim());

                            WebElement domainElement = card.findElement(By.cssSelector(".domain"));
                            victim.setVictimURL(domainElement.getText().trim());

                            WebElement countdownElement = card.findElement(By.cssSelector(".countdown"));
                            attack.setDeadlines(countdownElement.getAttribute("data-deadline"));
                            attack.setPublished(countdownElement.getText().trim().equals("PUBLISHED"));

                            WebElement descriptionElement = card.findElement(By.cssSelector(".truncated-description"));
                            attack.setDescription(descriptionElement.getText().trim());


                            String dataId = card.getAttribute("data-id");
                            try {
                                victim.setVictimId(Integer.parseInt(dataId));
                            } catch (NumberFormatException e) {
                                System.out.println("Number of data ids is not an integer");
                            }

                            card.click();

                            WebDriverWait modalWait = new WebDriverWait(driver, Duration.ofSeconds(10));
                            WebElement modal = modalWait.until(
                                    ExpectedConditions.visibilityOfElementLocated(By.id("victimModal"))
                            );

                            // Extract detailed information from the modal
                            WebElement modalDomainElement = modal.findElement(By.id("modal-victim-domain"));
                            victim.setVictimURL(modalDomainElement.getText().trim());

                            WebElement sizeElement = modal.findElement(By.id("modal-victim-size"));
                            attack.setDataSizes(sizeElement.getText().replace("Unknown", "").trim());

                            WebElement modalDescriptionElement = modal.findElement(By.id("modal-victim-description"));
                            attack.setDescription(modalDescriptionElement.getText().trim());

                            List<WebElement> downloadLinkElements = modal.findElements(By.cssSelector("#modal-download-links a"));
                            for (WebElement linkElement : downloadLinkElements) {
                                attack.getDownloadUrls().add(new DownloadUrl(linkElement.getAttribute("href")));
                            }

                            List<WebElement> imageElements = modal.findElements(By.cssSelector("#modal-image-slider .swiper-slide a[data-fancybox='gallery'] img"));
                            for (WebElement imgElement : imageElements) {
                                attack.getImages().add(new Image(imgElement.getAttribute("src")));
                            }

                            WebElement ransomUsdElement = null;
                            try {
                                ransomUsdElement = modal.findElement(By.id("ransom-usd"));
                                attack.setRansomAmount(ransomUsdElement.getText().replace("USD", "").trim());
                                attack.setNegotiated(true);
                            } catch (org.openqa.selenium.NoSuchElementException e) {
                                attack.setRansomAmount(null);
                                attack.setNegotiated(false);
                            }

                            // Determine category
                            attack.setCategory("Double Extortion");


                            attack.setUpdatedAt(java.time.LocalDateTime.now().toString());
                            attacksList.add(attack);

                            // Close the modal
                            WebElement closeButton = modal.findElement(By.cssSelector(".modal-body .close-btn button"));
                            closeButton.click();

                            // Wait for the modal to close
                            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("victimModal")));


                        } catch (Exception e) {
                            System.err.println("Error processing a card: " + e.getMessage());
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error during Selenium scraping: " + e.getMessage());
                } finally {
                    if (driver != null) {
                        driver.quit();
                    }
                }
            }
            System.out.println(attacksList);
            return attacksList;
        }
    }