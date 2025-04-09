package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class AttackersServiceImpl implements AttackersService {

    private static final Logger logger = Logger.getLogger(AttackersServiceImpl.class.getName());

    private AttackersDao attackerDao;
    private AttackersSiteUrlsDao attackersSiteUrlsDao;

    public AttackersServiceImpl() {
        this.attackerDao = new AttackersDaoImpl();
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }

    @Override
    public String addNewAttacker(Attacker attacker, List<String> urlList) throws SQLException {
        logger.info("Entering addAttacker method");

        // Validate required fields
        Attacker a = attackerDao.getAttackerByName(attacker.getAttackerName());
        if (a!=null) {
            logger.warning("Attacker name is Already Exist");
            return "Attacker name is Already Exist";
        }

        String res = attackerDao.addNewAttacker(attacker);
        if(res.equals("Added Attacker")) {
            for (String url : urlList) {
                AttackerSiteUrl siteUrl = new AttackerSiteUrl(attacker.getAttackerId(), url, true, true, new Date().toString(), true);

                String s =attackersSiteUrlsDao.addNewUrl(siteUrl);
                if(s.equals("Url is Not Valid")) {
                    logger.info("Url is Not Valid: " + url);
                }
                else
                {
                    logger.info("Added attacker site URL: " + url);
                }
            }

        }
        logger.info("Attacker add operation result: " + res);
        return res;
    }

    @Override
    public List<Attacker> getAttackers() throws SQLException {
        logger.info("Fetching all attackers...");
        List<Attacker> attackers = attackerDao.getAllAttackers();

        logger.info("Retrieved " + attackers.size() + " attackers.");
        return attackers;
    }

    @Override
    public boolean updateAttacker(Attacker attacker) throws SQLException{
        logger.info("Entering updateAttacker method");
        return  attackerDao.updateAttacker(attacker);
    }
}
