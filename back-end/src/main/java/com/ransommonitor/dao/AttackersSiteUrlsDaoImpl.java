package com.ransommonitor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.config.DbConfig;

public class AttackersSiteUrlsDaoImpl implements AttackersSiteUrlsDao {

    PreparedStatement pstmt;
    String query;

    @Override
    public String addNewUrl(AttackerSiteUrl url) throws SQLException {
        query = "INSERT INTO AttackerSiteURL(attackerId, url, status, monitorStatus, lastScrap) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING urlId";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

        pstmt.setInt(1, url.getAttackerId());
        pstmt.setString(2, url.getURL());
        pstmt.setString(3, url.isStatus() ? "active" : "inactive");
        pstmt.setString(4, url.isMonitorStatus() ? "monitored" : "unmonitored");
        pstmt.setTimestamp(5, null); // You might want to set actual timestamp if available

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            url.setUrlId(rs.getInt("urlId"));
            return "Added URL";
        }
        return "Adding Failed";
    }

    @Override
    public boolean updateUrl(AttackerSiteUrl url) throws SQLException {
        query = "UPDATE AttackerSiteURL SET attackerId = ?, url = ?, status = ?, " +
                "monitorStatus = ?, lastScrap = ? WHERE urlId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, url.getAttackerId());
        pstmt.setString(2, url.getURL());
        pstmt.setString(3, url.isStatus() ? "active" : "inactive");
        pstmt.setString(4, url.isMonitorStatus() ? "monitored" : "unmonitored");
        pstmt.setTimestamp(5, null); // Set actual timestamp if available
        pstmt.setInt(6, url.getUrlId());

        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    @Override
    public List<AttackerSiteUrl> getAllUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        query = "SELECT * FROM AttackerSiteURL ORDER BY url ASC";
        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            AttackerSiteUrl url = new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    res.getString("status").equals("active"),
                    res.getString("monitorStatus").equals("monitored"),
                    res.getTimestamp("lastScrap") != null
            );
            urls.add(url);
        }
        return urls;
    }

    @Override
    public AttackerSiteUrl getUrlById(int urlId) throws SQLException {
        query = "SELECT * FROM AttackerSiteURL WHERE urlId = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, urlId);
        ResultSet res = pstmt.executeQuery();

        if (res.next()) {
            return new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    res.getString("status").equals("active"),
                    res.getString("monitorStatus").equals("monitored"),
                    res.getTimestamp("lastScrap") != null
            );
        }
        return null;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerId(int attackerId) throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        query = "SELECT * FROM AttackerSiteURL WHERE attackerId = ? ORDER BY url ASC";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackerId);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            AttackerSiteUrl url = new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    res.getString("status").equals("active"),
                    res.getString("monitorStatus").equals("monitored"),
                    res.getTimestamp("lastScrap") != null
            );
            urls.add(url);
        }
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerName(String attackerName) throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        query = "SELECT u.* FROM AttackerSiteURL u " +
                "JOIN Attacker a ON u.attackerId = a.attackerId " +
                "WHERE a.attackerName = ? ORDER BY u.url ASC";
        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, attackerName);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            AttackerSiteUrl url = new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    res.getString("status").equals("active"),
                    res.getString("monitorStatus").equals("monitored"),
                    res.getTimestamp("lastScrap") != null
            );
            urls.add(url);
        }
        return urls;
    }

    @Override
    public boolean deleteUrl(int urlId) throws SQLException {
        query = "DELETE FROM AttackerSiteURL WHERE urlId = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, urlId);
        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    @Override
    public List<AttackerSiteUrl> getActiveUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        query = "SELECT * FROM AttackerSiteURL WHERE status = 'active' ORDER BY url ASC";
        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            AttackerSiteUrl url = new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    true, // status is active
                    res.getString("monitorStatus").equals("monitored"),
                    res.getTimestamp("lastScrap") != null
            );
            urls.add(url);
        }
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getMonitoredUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        query = "SELECT * FROM AttackerSiteURL WHERE monitorStatus = 'monitored' ORDER BY url ASC";
        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            AttackerSiteUrl url = new AttackerSiteUrl(
                    res.getInt("urlId"),
                    res.getInt("attackerId"),
                    res.getString("url"),
                    res.getString("status").equals("active"),
                    true, // monitorStatus is monitored
                    res.getTimestamp("lastScrap") != null
            );
            urls.add(url);
        }
        return urls;
    }
}