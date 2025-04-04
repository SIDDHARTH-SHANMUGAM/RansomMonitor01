package com.ransommonitor.servlet;


import com.ransommonitor.bean.*;
import com.ransommonitor.dao.*;
import com.ransommonitor.scrapper.Scrapper;
import com.ransommonitor.scrapper.ScrapperFactory;
import com.ransommonitor.service.AttackersService;
import com.ransommonitor.service.AttackersServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/doScrap")
public class DoScrapServlet extends HttpServlet {
    private AttackersService attackerService;

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AttackersService attackersService = new AttackersServiceImpl(new AttackersDaoImpl());
        AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
        AttacksDao attacksDao = new AttacksDaoImpl();
        VictimsDao victimsDao = new VictimsDaoImpl();
        DownloadUrlsDao downloadUrlsDao = new DownloadUrlsDaoImpl();
        ImagesDao imagesDao = new ImagesDaoImpl();
        try {
            List<Attacker> attackers = attackersService.getAttackers();
            for (Attacker attacker : attackers) {
                if(attacker.getMonitorStatus())
                {
                    List<AttackerSiteUrl> attackerSiteUrls = attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName());
                    for(AttackerSiteUrl attackerSiteUrl : attackerSiteUrls)
                    {
                        if(attackerSiteUrl.isMonitorStatus())
                        {
                            Scrapper scrapper = ScrapperFactory.getScrapper(attacker.getAttackerName());
                            List<Attack> attacks = scrapper.scrapData(attackerSiteUrl.getURL());
                            List<Attack> prev = attacksDao.getAttacksByAttackerName(attacker.getAttackerName());
                            for(Attack attack : attacks)
                            {
                                boolean found = false;
                                for(Attack attack1: prev)
                                {
                                    if(attack.equals(attack1))
                                    {
                                        if(attack.equals2(attack1)){
                                            found = true;
                                            break;
                                        }
                                        else
                                        {
                                            attacksDao.updateAttack(attack);
                                        }

                                    }
                                }
                                if(found)
                                    continue;
                                else
                                {
                                    victimsDao.addNewVictim(attack.getVictim());
                                    attacksDao.addNewAttack(attack);
                                    for(DownloadUrl x: attack.getDownloadUrls())
                                    {
                                        x.setAttackId(attack.getAttackId());
                                        downloadUrlsDao.addDownloadUrl(x);
                                    }
                                    for(Image x: attack.getImages())
                                    {
                                        x.setAttackId(attack.getAttackId());
                                        imagesDao.addImage(x);
                                    }
                                }
                            }


                        }
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
}




