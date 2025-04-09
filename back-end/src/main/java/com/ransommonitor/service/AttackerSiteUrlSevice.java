package com.ransommonitor.service;

import com.ransommonitor.bean.AttackerSiteUrl;

import java.sql.SQLException;

public interface AttackerSiteUrlSevice {
    String addNewUrl(AttackerSiteUrl attackerSiteUrl) throws SQLException;
}
