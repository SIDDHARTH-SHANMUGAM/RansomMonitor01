package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.utils.DbConnect;

public class DownloadUrlsDaoImpl implements DownloadUrlsDao {

    @Override
    public String addDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        String query = "INSERT INTO DownloadUrls(attackId, downloadUrl) VALUES(?, ?) RETURNING downloadUrlId";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, downloadUrl.getAttackId());
            pstmt.setString(2, downloadUrl.getDownloadUrl());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    downloadUrl.setDownloadUrlId(rs.getInt("downloadUrlId"));
                    return "Download URL added successfully";
                }
            }
        }
        return "Failed to add download URL";
    }

    @Override
    public boolean updateDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        String query = "UPDATE DownloadUrls SET downloadUrl = ? WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, downloadUrl.getDownloadUrl());
            pstmt.setInt(2, downloadUrl.getDownloadUrlId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<DownloadUrl> getDownloadUrlsByAttack(int attackId) throws SQLException {
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
        return urls;
    }

    @Override
    public DownloadUrl getDownloadUrlById(int downloadUrlId) throws SQLException {
        String query = "SELECT * FROM DownloadUrls WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, downloadUrlId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new DownloadUrl(
                            rs.getInt("downloadUrlId"),
                            rs.getInt("attackId"),
                            rs.getString("downloadUrl"),
                            rs.getString("createdAt")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteDownloadUrl(int downloadUrlId) throws SQLException {
        String query = "DELETE FROM DownloadUrls WHERE downloadUrlId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, downloadUrlId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteAllUrlsForAttack(int attackId) throws SQLException {
        String query = "DELETE FROM DownloadUrls WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            return pstmt.executeUpdate() >= 0; // Returns true even if 0 rows deleted
        }
    }

    public boolean urlExistsForAttack(int attackId, String url) throws SQLException {
        String query = "SELECT 1 FROM DownloadUrls WHERE attackId = ? AND downloadUrl = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            pstmt.setString(2, url);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}