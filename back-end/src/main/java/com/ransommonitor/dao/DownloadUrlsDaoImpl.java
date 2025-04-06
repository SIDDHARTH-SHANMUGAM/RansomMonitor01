package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.utils.DbConnect;

public class DownloadUrlsDaoImpl implements DownloadUrlsDao {

    private static final Logger logger = Logger.getLogger(DownloadUrlsDaoImpl.class.getName());

    @Override
    public String addDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        logger.info("Adding new download URL for attackId: " + downloadUrl.getAttackId());

        String query = "INSERT INTO DownloadUrls(attackId, downloadUrl) VALUES(?, ?) RETURNING downloadUrlId";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, downloadUrl.getAttackId());
            pstmt.setString(2, downloadUrl.getDownloadUrl());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    downloadUrl.setDownloadUrlId(rs.getInt("downloadUrlId"));
                    logger.info("Download URL added successfully with ID: " + downloadUrl.getDownloadUrlId());
                    return "Download URL added successfully";
                }
            }
        }
        logger.warning("Failed to add download URL");
        return "Failed to add download URL";
    }

    @Override
    public boolean updateDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        logger.info("Updating download URL with ID: " + downloadUrl.getDownloadUrlId());

        String query = "UPDATE DownloadUrls SET downloadUrl = ? WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, downloadUrl.getDownloadUrl());
            pstmt.setInt(2, downloadUrl.getDownloadUrlId());

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Download URL update " + (success ? "successful" : "failed"));
            return success;
        }
    }

    @Override
    public List<DownloadUrl> getDownloadUrlsByAttack(int attackId) throws SQLException {
        logger.info("Retrieving download URLs for attackId: " + attackId);

        List<DownloadUrl> urls = new ArrayList<>();
        String query = "SELECT * FROM DownloadUrls WHERE attackId = ? ORDER BY createdAt DESC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    urls.add(new DownloadUrl(
                            rs.getInt("downloadUrlId"),
                            rs.getInt("attackId"),
                            rs.getString("downloadUrl"),
                            rs.getString("createdAt")
                    ));
                }
            }
        }

        logger.info("Retrieved " + urls.size() + " download URLs for attackId: " + attackId);
        return urls;
    }

    @Override
    public DownloadUrl getDownloadUrlById(int downloadUrlId) throws SQLException {
        logger.info("Fetching download URL by ID: " + downloadUrlId);

        String query = "SELECT * FROM DownloadUrls WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, downloadUrlId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Download URL found for ID: " + downloadUrlId);
                    return new DownloadUrl(
                            rs.getInt("downloadUrlId"),
                            rs.getInt("attackId"),
                            rs.getString("downloadUrl"),
                            rs.getString("createdAt")
                    );
                }
            }
        }

        logger.warning("No download URL found for ID: " + downloadUrlId);
        return null;
    }

    @Override
    public boolean deleteDownloadUrl(int downloadUrlId) throws SQLException {
        logger.info("Deleting download URL with ID: " + downloadUrlId);

        String query = "DELETE FROM DownloadUrls WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, downloadUrlId);
            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Download URL deletion " + (success ? "successful" : "failed"));
            return success;
        }
    }

    @Override
    public boolean deleteAllUrlsForAttack(int attackId) throws SQLException {
        logger.info("Deleting all download URLs for attackId: " + attackId);

        String query = "DELETE FROM DownloadUrls WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackId);
            boolean success = pstmt.executeUpdate() >= 0;
            logger.info("All download URLs deletion for attackId " + attackId + (success ? " successful" : " failed"));
            return success;
        }
    }

    public boolean urlExistsForAttack(int attackId, String url) throws SQLException {
        logger.info("Checking if URL exists for attackId: " + attackId + " and URL: " + url);

        String query = "SELECT 1 FROM DownloadUrls WHERE attackId = ? AND downloadUrl = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackId);
            pstmt.setString(2, url);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean exists = rs.next();
                logger.info("URL exists: " + exists);
                return exists;
            }
        }
    }
}
