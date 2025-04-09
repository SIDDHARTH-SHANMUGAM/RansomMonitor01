package com.ransommonitor.servlet;

import com.ransommonitor.bean.*;
import com.ransommonitor.dao.*;
import com.ransommonitor.scrapper.Scraper;
import com.ransommonitor.scrapper.ScraperFactory;
import com.ransommonitor.scrapper.URLStatusChecker;
import com.ransommonitor.service.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/doScrap")
public class DoScrapServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(DoScrapServlet.class.getName());
    private AttackersService attackerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("Scraping process initiated via /doScrap");

        AttackersService attackersService = new AttackersServiceImpl();
        AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
        AttacksService attacksService = new AttacksServiceImpl();


        try {
            List<Attacker> attackers = attackersService.getAttackers();
            logger.info("Fetched attackers count: " + attackers.size());

            for (Attacker attacker : attackers) {
                logger.fine("Checking attacker: " + attacker.getAttackerName());

                if (attacker.getMonitorStatus()) {
                    logger.fine("Attacker is enabled for monitoring: " + attacker.getAttackerName());

                    List<AttackerSiteUrl> attackerSiteUrls =
                            attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName());

                    logger.fine("Fetched site URLs for attacker " + attacker.getAttackerName() +
                            ", count: " + attackerSiteUrls.size());

                    for (AttackerSiteUrl attackerSiteUrl : attackerSiteUrls) {

                        logger.fine("Checking URL: " + attackerSiteUrl.getURL());
                        if(attackerSiteUrl.isMonitorStatus())
                        {
                            updateUrl(attackerSiteUrl);
                            System.out.println("updating Url status");
                            if ( attackerSiteUrl.getActiveStatus()){

                                logger.info("URL is live and monitoring enabled: " + attackerSiteUrl.getURL());

                                Scraper scrapper = ScraperFactory.getScrapper(attacker.getAttackerName());
                                List<Attack> attacks = scrapper.scrapeData(attackerSiteUrl.getURL());
                                if(attacks==null||attacks.isEmpty())
                                {
                                    attackerSiteUrl.setScraped(false);
                                }
                                else
                                {
                                    attackerSiteUrl.setScraped(true);
                                    logger.info("Scraped attacks count: " + attacks.size());
                                    attacksService.addNewAttacks(attacks, attacker);

                                }
                                attackersSiteUrlsDao.updateUrl(attackerSiteUrl);

                            } else {
                                logger.fine("URL not live or monitoring disabled: " + attackerSiteUrl.getURL());
                            }
                        }
                    }
                } else {
                    logger.fine("Skipping attacker (monitorStatus is false): " + attacker.getAttackerName());
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException during scraping process: ", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected exception during scraping process: ", e);
        }
    }


    public void updateUrl(AttackerSiteUrl attackerSiteUrl) throws SQLException {
        AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
        boolean b=URLStatusChecker.checkOnionStatus(attackerSiteUrl.getURL(), 9050)||
                URLStatusChecker.checkOnionStatus(attackerSiteUrl.getURL(), 9150);
        attackerSiteUrl.setStatus(b);
        if(!b)
        {
            attackerSiteUrl.setScraped(false);
        }
        attackersSiteUrlsDao.updateUrl(attackerSiteUrl);
    }
}
