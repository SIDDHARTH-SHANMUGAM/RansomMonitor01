package com.ransommonitor.dao;

import java.sql.SQLException;
import java.util.List;

import com.ransommonitor.bean.AttackerSiteUrl;

public interface AttackersSiteUrlsDao {
    boolean addNewUrl(AttackerSiteUrl url) throws SQLException;
    boolean updateUrl(AttackerSiteUrl url) throws SQLException;
    boolean deleteUrl(int urlId) throws SQLException;
    boolean updateUrlMonitoringStatus(int urlId, boolean status) throws SQLException;
    boolean getUrlMonitoringStatus(int urlId) throws SQLException;
    boolean isUrlExist(String url) throws SQLException;
    boolean isUrlExist(int urlId) throws SQLException;
    boolean deleteUrlByAttackerId(int attackerId) throws SQLException;
    boolean updateUrlStatus(int urlId, boolean status) throws SQLException;
    boolean updateUrlMonitorStatus(int urlId, boolean status) throws SQLException;
    boolean updateUpdatedat(int urlId) throws SQLException;
    boolean updateIsScrapped(int urlId, boolean status) throws SQLException;
    AttackerSiteUrl getUrlById(int urlId) throws SQLException;
    List<AttackerSiteUrl> getAllUrls() throws SQLException;
    List<AttackerSiteUrl> getUrlsByAttackerId(int attackerId) throws SQLException;
    List<AttackerSiteUrl> getUrlsByAttackerName(String attackerName) throws SQLException;
    List<AttackerSiteUrl> getActiveUrls() throws SQLException;
    List<AttackerSiteUrl> getMonitoredUrls() throws SQLException;
}