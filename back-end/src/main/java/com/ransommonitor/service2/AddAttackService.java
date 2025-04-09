package com.ransommonitor.service2;

import com.ransommonitor.bean.*;
import com.ransommonitor.dao.*;
import com.ransommonitor.scrapper.Scraper;
import com.ransommonitor.utils.ScraperFactory;

import java.sql.SQLException;
import java.util.List;

public class AddAttackService {

    AttackersDao attackersDao;
    AttackersSiteUrlsDao attackersSiteUrlsDao;
    AttacksDao attacksDao;
    VictimsDao victimsDao;
    DownloadUrlsDao downloadUrlsDao;
    ImagesDao imagesDao;
    SyncStatusService syncStatusService;


    public AddAttackService() {
        attackersDao = new AttackersDaoImpl();
        attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
        attacksDao = new AttacksDaoImpl();
        victimsDao = new VictimsDaoImpl();
        downloadUrlsDao = new DownloadUrlsDaoImpl();
        imagesDao = new ImagesDaoImpl();
        syncStatusService = new SyncStatusService();
    }

    public String addNewAttacks() throws SQLException, ClassNotFoundException {
        String res = syncStatusService.syncStatus();
        if(!res.equals("Sync Success")) {
            return res;
        }
        List<Attacker> attackerList = attackersDao.getAllAttackers();
        for (Attacker attacker : attackerList) {
            if(attacker.getMonitorStatus())
            {
                List<AttackerSiteUrl> attackerSiteUrls = attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName());
                for (AttackerSiteUrl attackerSiteUrl : attackerSiteUrls) {
                    if(attackerSiteUrl.getActiveStatus())
                    {
                        Scraper scraper = ScraperFactory.getScrapper(attacker.getAttackerName());
                        List<Attack> attacks = scraper.scrapeData(attackerSiteUrl.getURL());
                        if(attacks==null || attacks.isEmpty()) {
                            attackersSiteUrlsDao.updateIsScrapped(attackerSiteUrl.getUrlId(), false);
                            continue;
                        }
                        attackersSiteUrlsDao.updateIsScrapped(attackerSiteUrl.getUrlId(), true);
                        attackersSiteUrlsDao.updateUpdatedat(attackerSiteUrl.getUrlId());
                        List<Attack> attacks1 = attacksDao.getAttacksByAttackerName(attacker.getAttackerName());
                        boolean found = true;
                        for(Attack attack: attacks) {
                            for(Attack attack1: attacks1) {
                                if(attack.equals(attack1)) {
                                    if(attack.equals2(attack1)) {
                                        found = false;
                                        break;
                                    }
                                    else {
                                        attack.setAttacker(attacker);
                                        attacksDao.updateAttack(attack);
                                    }
                                }
                            }
                            if(found) {
                                if(victimsDao.addNewVictim(attack.getVictim()))
                                {
                                    if(attacksDao.addNewAttack(attack)) {
                                        if(attack.getDownloadUrls()!=null)
                                        {
                                            for(DownloadUrl downloadUrl: attack.getDownloadUrls()) {
                                                downloadUrlsDao.updateDownloadUrl(downloadUrl);
                                            }
                                        }
                                        if(attack.getImages()!=null)
                                        {
                                            for (Image image : attack.getImages()) {
                                                imagesDao.updateImage(image);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        return "Scraped successfully";
    }

}
