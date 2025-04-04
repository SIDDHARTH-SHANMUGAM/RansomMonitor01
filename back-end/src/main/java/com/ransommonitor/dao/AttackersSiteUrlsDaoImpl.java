package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.utils.DbConnect;

public class AttackersSiteUrlsDaoImpl implements AttackersSiteUrlsDao {

    @Override
    public String addNewUrl(AttackerSiteUrl url) throws SQLException {
        String query = "INSERT INTO AttackerSiteURL(attackerId, url, status, monitorStatus, lastScrap) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING urlId";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, url.getAttackerId());
            pstmt.setString(2, url.getURL());
            pstmt.setString(3, url.isStatus() ? "active" : "inactive");
            pstmt.setString(4, url.isMonitorStatus() ? "monitored" : "unmonitored");
            pstmt.setTimestamp(5, null);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    url.setUrlId(rs.getInt("urlId"));
                    return "Added URL";
                }
            }
        }
        return "Adding Failed";
    }

    @Override
    public boolean updateUrl(AttackerSiteUrl url) throws SQLException {
        String query = "UPDATE AttackerSiteURL SET attackerId = ?, url = ?, status = ?, " +
                "monitorStatus = ?, lastScrap = ? WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, url.getAttackerId());
            pstmt.setString(2, url.getURL());
            pstmt.setString(3, url.isStatus() ? "active" : "inactive");
            pstmt.setString(4, url.isMonitorStatus() ? "monitored" : "unmonitored");
            pstmt.setTimestamp(5, null);
            pstmt.setInt(6, url.getUrlId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<AttackerSiteUrl> getAllUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        return urls;
    }

    @Override
    public AttackerSiteUrl getUrlById(int urlId) throws SQLException {
        String query = "SELECT * FROM AttackerSiteURL WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, urlId);
            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return mapResultSetToAttackerSiteUrl(res);
                }
            }
        }
        return null;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerId(int attackerId) throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL WHERE attackerId = ? ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackerId);
            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    urls.add(mapResultSetToAttackerSiteUrl(res));
                }
            }
        }
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerName(String attackerName) throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT u.* FROM AttackerSiteURL u " +
                "JOIN Attacker a ON u.attackerId = a.attackerId " +
                "WHERE a.attackerName = ? ORDER BY u.url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, attackerName);
            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    urls.add(mapResultSetToAttackerSiteUrl(res));
                }
            }
        }
        return urls;
    }

    @Override
    public boolean deleteUrl(int urlId) throws SQLException {
        String query = "DELETE FROM AttackerSiteURL WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, urlId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<AttackerSiteUrl> getActiveUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL WHERE status = 'active' ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getMonitoredUrls() throws SQLException {
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL WHERE monitorStatus = 'monitored' ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        return urls;
    }

    private AttackerSiteUrl mapResultSetToAttackerSiteUrl(ResultSet res) throws SQLException {
        return new AttackerSiteUrl(
                res.getInt("urlId"),
                res.getInt("attackerId"),
                res.getString("url"),
                "active".equals(res.getString("status")),
                "monitored".equals(res.getString("monitorStatus")),
                res.getString("lastScrap")
        );
    }
}