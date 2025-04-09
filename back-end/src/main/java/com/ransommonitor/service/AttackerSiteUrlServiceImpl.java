package com.ransommonitor.service;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import java.sql.SQLException;

public class AttackerSiteUrlServiceImpl implements AttackerSiteUrlSevice{

    private AttackersSiteUrlsDao attackersSiteUrlsDao;

    public AttackerSiteUrlServiceImpl() {
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }
    @Override
    public String addNewUrl(AttackerSiteUrl attackerSiteUrl) throws SQLException {
        if(!attackersSiteUrlsDao.isUrlExist(attackerSiteUrl.getURL()))
        {
            return attackersSiteUrlsDao.addNewUrl(attackerSiteUrl);
        }

        return "{\"error\": \"URL " + attackerSiteUrl.getURL() + " already exists\"}";
    }
}
