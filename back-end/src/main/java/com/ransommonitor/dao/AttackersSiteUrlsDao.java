package com.ransommonitor.dao;

import java.sql.SQLException;
import java.util.List;

import com.ransommonitor.bean.AttackerSiteUrl;

public interface AttackersSiteUrlsDao {
    String addNewUrl(AttackerSiteUrl url) throws SQLException;
    boolean updateUrl(AttackerSiteUrl url) throws SQLException;
    List<AttackerSiteUrl> getAllUrls() throws SQLException;
    AttackerSiteUrl getUrlById(int urlId) throws SQLException;
    List<AttackerSiteUrl> getUrlsByAttackerId(int attackerId) throws SQLException;
    List<AttackerSiteUrl> getUrlsByAttackerName(String attackerName) throws SQLException;
    boolean deleteUrl(int urlId) throws SQLException;
    List<AttackerSiteUrl> getActiveUrls() throws SQLException;
    List<AttackerSiteUrl> getMonitoredUrls() throws SQLException;
    boolean updateUrlMonitoringStatus(int urlId, boolean status) throws SQLException;
    boolean getUrlMonitoringStatus(int urlId) throws SQLException;

    boolean updateAttackerMonitoringStatus(int attackerId, boolean status) throws SQLException;

}