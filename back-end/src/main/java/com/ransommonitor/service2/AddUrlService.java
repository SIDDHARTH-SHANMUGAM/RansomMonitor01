package com.ransommonitor.service2;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import java.sql.SQLException;

public class AddUrlService {
    private final AttackersDao attackerDao;
    private final AttackersSiteUrlsDao attackersSiteUrlsDao;

    public AddUrlService() {
        this.attackerDao = new AttackersDaoImpl();
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }

    public String addNewUrl(AttackerSiteUrl attackerSiteUrl) throws SQLException {
        // Validate is Attacker Already Exist
        if (!attackerDao.isAttackerAvailableById(attackerSiteUrl.getAttackerId())) {
            return "Attacker is Not Available";
        }
        StringBuilder res = new StringBuilder();

        if(AddAttackerService.isValidURL(attackerSiteUrl.getURL())) {
            if(attackersSiteUrlsDao.isUrlExist(attackerSiteUrl.getURL())) {
                    if(attackersSiteUrlsDao.addNewUrl(attackerSiteUrl)) {
                    res.append("Url Added");
                }
                else {
                    res.append("\n").append(attackerSiteUrl.getURL()).append("is Not Added");
                }
            } else {
                res.append("\n").append(attackerSiteUrl.getURL()).append(" is Already Exist");
            }
        } else {
            res.append("\n").append(attackerSiteUrl.getURL()).append(" is Not a valid URL");
        }
        return res.toString();
    }
}
