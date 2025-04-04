package com.ransommonitor.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.config.DbConfig;

public class DownloadUrlsDaoImpl implements DownloadUrlsDao {

    PreparedStatement pstmt;
    String query;

    @Override
    public String addDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        query = "INSERT INTO DownloadUrls(attackId, downloadUrl) VALUES(?, ?) RETURNING downloadUrlId";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

        pstmt.setInt(1, downloadUrl.getAttackId());
        pstmt.setString(2, downloadUrl.getDownloadUrl());

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            downloadUrl.setDownloadUrlId(rs.getInt("downloadUrlId"));
            return "Download URL added successfully";
        }
        return "Failed to add download URL";
    }

    @Override
    public boolean updateDownloadUrl(DownloadUrl downloadUrl) throws SQLException {
        query = "UPDATE DownloadUrls SET downloadUrl = ? WHERE downloadUrlId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, downloadUrl.getDownloadUrl());
        pstmt.setInt(2, downloadUrl.getDownloadUrlId());

        int result = pstmt.executeUpdate();
        return result > 0;
    }

    @Override
    public List<DownloadUrl> getDownloadUrlsByAttack(int attackId) throws SQLException {
        List<DownloadUrl> urls = new ArrayList<>();
        query = "SELECT * FROM DownloadUrls WHERE attackId = ? ORDER BY createdAt DESC";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            DownloadUrl url = new DownloadUrl(
                    rs.getInt("downloadUrlId"),
                    rs.getInt("attackId"),
                    rs.getString("downloadUrl"),
                    rs.getString("createdAt")
            );
            urls.add(url);
        }
        return urls;
    }

    @Override
    public DownloadUrl getDownloadUrlById(int downloadUrlId) throws SQLException {
        query = "SELECT * FROM DownloadUrls WHERE downloadUrlId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, downloadUrlId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new DownloadUrl(
                    rs.getInt("downloadUrlId"),
                    rs.getInt("attackId"),
                    rs.getString("downloadUrl"),
                    rs.getString("createdAt")
            );
        }
        return null;
    }

    @Override
    public boolean deleteDownloadUrl(int downloadUrlId) throws SQLException {
        query = "DELETE FROM DownloadUrls WHERE downloadUrlId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, downloadUrlId);
        int result = pstmt.executeUpdate();
        return result > 0;
    }

    @Override
    public boolean deleteAllUrlsForAttack(int attackId) throws SQLException {
        query = "DELETE FROM DownloadUrls WHERE attackId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        int result = pstmt.executeUpdate();
        return result >= 0; // Returns true even if 0 rows deleted
    }

    public boolean urlExistsForAttack(int attackId, String url) throws SQLException {
        query = "SELECT 1 FROM DownloadUrls WHERE attackId = ? AND downloadUrl = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        pstmt.setString(2, url);
        ResultSet rs = pstmt.executeQuery();

        return rs.next();
    }
}