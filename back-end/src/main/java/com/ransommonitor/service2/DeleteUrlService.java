package com.ransommonitor.service2;

import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import java.sql.SQLException;


public class DeleteUrlService {

    private final AttackersSiteUrlsDao attackersSiteUrlsDao;

    public DeleteUrlService() {
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }

    public String deleteUrl(int urlId) throws SQLException {

        if(!attackersSiteUrlsDao.isUrlExist(urlId)) {
            return "Url is not available";
        }
        if(attackersSiteUrlsDao.deleteUrl(urlId)) {
            return "Url deleted";
        }
        return "Attacker is not Deleted";
    }
}
