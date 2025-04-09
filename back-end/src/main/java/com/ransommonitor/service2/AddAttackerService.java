package com.ransommonitor.service2;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

public class AddAttackerService {

    private final AttackersDao attackerDao;
    private final AttackersSiteUrlsDao attackersSiteUrlsDao;

    public AddAttackerService() {
        this.attackerDao = new AttackersDaoImpl();
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }

    public static boolean isValidURL(String url) {
        try {
            URI uri = new URI(url);
            return uri.isAbsolute();
        } catch (URISyntaxException e) {
            return false;
        }
    }


    public String addNewAttacker(Attacker attacker, List<String> urlList) throws SQLException {
        // Validate is Attacker Already Exist
        if (attackerDao.isAttackerAvailableByName(attacker.getAttackerName())) {
            return "Attacker name is Already Exist";
        }
        if(attackerDao.addNewAttacker(attacker)) {
            StringBuilder res= new StringBuilder();
            for (String url : urlList) {
                AttackerSiteUrl siteUrl = new AttackerSiteUrl(attacker.getAttackerId(), url);
                if(isValidURL(url)) {
                    if(attackersSiteUrlsDao.isUrlExist(url)) {
                        if(!attackersSiteUrlsDao.addNewUrl(siteUrl)) {
                            res.append("\n").append(url).append(" is Not Added in Db");
                        }
                    } else {
                        res.append("\n").append(url).append(" is Already Exist");
                    }
                } else {
                    res.append("\n").append(url).append(" is Not a valid URL");
                }
            }
            if(!res.toString().isEmpty()) {
                return res.toString();
            }
        }
        return "Attacker Added";
    }
}
