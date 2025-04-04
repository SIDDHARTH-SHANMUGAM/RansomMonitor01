package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Image;
import com.ransommonitor.utils.DbConnect;

public class ImagesDaoImpl implements ImagesDao {

    @Override
    public String addImage(Image image) throws SQLException {
        String query = "INSERT INTO Images(attackId, image) VALUES(?, ?) RETURNING imageId, createdAt";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, image.getAttackId());
            pstmt.setString(2, image.getImage());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    image.setImageId(rs.getInt("imageId"));
                    return "Image added successfully";
                }
            }
        }
        return "Failed to add image";
    }

    @Override
    public boolean updateImage(Image image) throws SQLException {
        String query = "UPDATE Images SET image = ? WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, image.getImage());
            pstmt.setInt(2, image.getImageId());
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Image> getImagesByAttack(int attackId) throws SQLException {
        List<Image> images = new ArrayList<>();
        String query = "SELECT imageId, attackId, createdAt FROM Images WHERE attackId = ? ORDER BY createdAt DESC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    images.add(new Image(
                            rs.getInt("imageId"),
                            rs.getInt("attackId"),
                            null,
                            rs.getString("createdAt")
                    ));
                }
            }
        }
        return images;
    }

    @Override
    public Image getImageById(int imageId) throws SQLException {
        String query = "SELECT * FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Image(
                            rs.getInt("imageId"),
                            rs.getInt("attackId"),
                            rs.getString("image"),
                            rs.getString("createdAt")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public byte[] getImageDataById(int imageId) throws SQLException {
        String query = "SELECT image FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("image");
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteImage(int imageId) throws SQLException {
        String query = "DELETE FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteAllImagesForAttack(int attackId) throws SQLException {
        String query = "DELETE FROM Images WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            return pstmt.executeUpdate() >= 0;
        }
    }

    public int getImageCountForAttack(int attackId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Images WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}