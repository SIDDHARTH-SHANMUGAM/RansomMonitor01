package com.ransommonitor.dao;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.utils.DbConnect;

public class AttackersSiteUrlsDaoImpl implements AttackersSiteUrlsDao {

    private static final Logger logger = Logger.getLogger(AttackersSiteUrlsDaoImpl.class.getName());

    @Override
    public String addNewUrl(AttackerSiteUrl url) throws SQLException {
        logger.info("Adding new URL for attacker ID: " + url.getAttackerId());
        if(isValidUrl(url.getURL()))
        {
            return "Url is Not Valid";
        }
        String query = "INSERT INTO AttackerSiteURL(attackerId, url, status, monitorStatus, isScraped) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING urlId";

        try (Connection conn = DbConnect.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, url.getAttackerId());
            pstmt.setString(2, url.getURL());
            pstmt.setBoolean(3, url.getActiveStatus() );
            pstmt.setBoolean(4, url.isMonitorStatus());
            pstmt.setBoolean(5, url.isScraped());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    url.setUrlId(rs.getInt("urlId"));
                    logger.info("URL added with ID: " + url.getUrlId());
                    return "Added URL";
                }
            }
        }
        logger.warning("Failed to add URL");
        return "Adding Failed";
    }

    private static final Pattern ONION_V3_PATTERN =
            Pattern.compile("^[a-z2-7]{56}\\.onion$");

    // Regex pattern for legacy v2 onion addresses (16 chars)
    private static final Pattern ONION_V2_PATTERN =
            Pattern.compile("^[a-z2-7]{16}\\.onion$");

    public static boolean isValidUrl(String url) {
        try {
            URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost().toLowerCase();

            // Special validation for .onion addresses
            if (host.endsWith(".onion")) {
                String onionName = host.substring(0, host.length() - 6);
                return ONION_V3_PATTERN.matcher(onionName).matches() ||
                        ONION_V2_PATTERN.matcher(onionName).matches();
            }

            // Standard URL validation
            parsedUrl.toURI();
            return true;

        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    @Override
    public boolean updateUrl(AttackerSiteUrl url) throws SQLException {
        logger.info("Updating URL with ID: " + url.getUrlId());
        System.out.println("here "+url);
        String query = "UPDATE AttackerSiteURL SET attackerId = ?, url = ?, status = ?, isScraped = ?," +
                "monitorStatus = ? WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, url.getAttackerId());
            pstmt.setString(2, url.getURL());
            pstmt.setBoolean(3, url.getActiveStatus());
            pstmt.setBoolean(4, url.isScraped());
            pstmt.setBoolean(5, url.isMonitorStatus());
            pstmt.setInt(6, url.getUrlId());

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Update status: " + success);
            return success;
        }
    }

    @Override
    public List<AttackerSiteUrl> getAllUrls() throws SQLException {
        logger.info("Fetching all URLs");
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        logger.info("Total URLs fetched: " + urls.size());
        return urls;
    }

    @Override
    public AttackerSiteUrl getUrlById(int urlId) throws SQLException {
        logger.info("Fetching URL with ID: " + urlId);
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
        logger.warning("No URL found for ID: " + urlId);
        return null;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerId(int attackerId) throws SQLException {
        logger.info("Fetching URLs for attacker ID: " + attackerId);
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
        logger.info("Total URLs found: " + urls.size());
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getUrlsByAttackerName(String attackerName) throws SQLException {
        logger.info("Fetching URLs by attacker name: " + attackerName);
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
        logger.info("Total URLs found: " + urls.size());
        return urls;
    }

    @Override
    public boolean deleteUrl(int urlId) throws SQLException {
        logger.info("Deleting URL with ID: " + urlId);
        String query = "DELETE FROM AttackerSiteURL WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, urlId);
            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Deletion success: " + success);
            return success;
        }
    }

    @Override
    public List<AttackerSiteUrl> getActiveUrls() throws SQLException {
        logger.info("Fetching all active URLs");
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL WHERE status = 'active' ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        logger.info("Total active URLs: " + urls.size());
        return urls;
    }

    @Override
    public List<AttackerSiteUrl> getMonitoredUrls() throws SQLException {
        logger.info("Fetching all monitored URLs");
        List<AttackerSiteUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM AttackerSiteURL WHERE monitorStatus = 'monitored' ORDER BY url ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                urls.add(mapResultSetToAttackerSiteUrl(res));
            }
        }
        logger.info("Total monitored URLs: " + urls.size());
        return urls;
    }

    private AttackerSiteUrl mapResultSetToAttackerSiteUrl(ResultSet res) throws SQLException {
        return new AttackerSiteUrl(
                res.getInt("urlId"),
                res.getInt("attackerId"),
                res.getString("url"),
                res.getBoolean("status"),
                res.getBoolean("monitorStatus"),
                res.getString("updatedat"),
                res.getBoolean("isScraped")
        );
    }

    @Override
    public boolean updateUrlMonitoringStatus(int urlId, boolean status) throws SQLException {
        logger.info("Updating monitoring status of URL ID " + urlId + " to " + (status ));
        String query = "UPDATE AttackerSiteURL SET monitorStatus = ? WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, status );
            pstmt.setInt(2, urlId);

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Monitoring status update success: " + success);
            return success;
        }
    }

    @Override
    public boolean getUrlMonitoringStatus(int urlId) throws SQLException {
        logger.info("Fetching monitoring status for URL ID: " + urlId);
        String query = "SELECT monitorStatus FROM AttackerSiteURL WHERE urlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, urlId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean monitored = "monitored".equals(rs.getString("monitorStatus"));
                    logger.info("Monitoring status is: " + monitored);
                    return monitored;
                }
            }
        }
        logger.warning("URL ID not found: " + urlId);
        return false;
    }

    @Override
    public boolean updateAttackerMonitoringStatus(int attackerId, boolean status) throws SQLException {
        logger.info("Updating attacker (ID: " + attackerId + ") monitoring status to " + status);
        String query = "UPDATE Attacker SET monitorStatus = ? WHERE attackerId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, status);
            pstmt.setInt(2, attackerId);

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Attacker monitoring status update success: " + success);
            return success;
        }
    }

    @Override
    public boolean isUrlExist(String url) throws SQLException {
        logger.info("Checking if URL " + url + " exists");
        String query = "SELECT * FROM AttackerSiteURL WHERE url = ?";

        try (Connection conn = DbConnect.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, url);
            ResultSet rs= pstmt.executeQuery();
            return rs.next();
        }


    }
}
