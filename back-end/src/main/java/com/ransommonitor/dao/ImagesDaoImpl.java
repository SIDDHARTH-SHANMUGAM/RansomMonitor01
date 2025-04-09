package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ransommonitor.bean.Image;
import com.ransommonitor.utils.DbConnect;

public class ImagesDaoImpl implements ImagesDao {

    private static final Logger logger = Logger.getLogger(ImagesDaoImpl.class.getName());

    @Override
    public boolean addImage(Image image) throws SQLException {
        logger.info("Adding new image for attackId: " + image.getAttackId());

        String query = "INSERT INTO Images(attackId, image) VALUES(?, ?) RETURNING imageId, createdAt";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, image.getAttackId());
            pstmt.setString(2, image.getImage());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    image.setImageId(rs.getInt("imageId"));
                    logger.info("Image added successfully with ID: " + image.getImageId());
                    return true;
                }
            }
        }

        logger.warning("Failed to add image");
        return false;
    }

    @Override
    public boolean updateImage(Image image) throws SQLException {
        logger.info("Updating image with ID: " + image.getImageId());

        String query = "UPDATE Images SET image = ? WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, image.getImage());
            pstmt.setInt(2, image.getImageId());

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Image update " + (success ? "successful" : "failed"));
            return success;
        }
    }

    @Override
    public List<Image> getImagesByAttack(int attackId) throws SQLException {
        logger.info("Fetching images for attackId: " + attackId);

        List<Image> images = new ArrayList<>();
        String query = "SELECT imageId,image, attackId, createdAt FROM Images WHERE attackId = ? ORDER BY createdAt DESC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    images.add(new Image(
                            rs.getInt("imageId"),
                            rs.getInt("attackId"),
                            rs.getString("image"),
                            rs.getString("createdAt")
                    ));
                }
            }
        }

        logger.info("Fetched " + images.size() + " images for attackId: " + attackId);
        return images;
    }

    @Override
    public Image getImageById(int imageId) throws SQLException {
        logger.info("Fetching image by ID: " + imageId);

        String query = "SELECT * FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Image found for ID: " + imageId);
                    return new Image(
                            rs.getInt("imageId"),
                            rs.getInt("attackId"),
                            rs.getString("image"),
                            rs.getString("createdAt")
                    );
                }
            }
        }

        logger.warning("No image found for ID: " + imageId);
        return null;
    }

    @Override
    public byte[] getImageDataById(int imageId) throws SQLException {
        logger.info("Retrieving image binary data for imageId: " + imageId);

        String query = "SELECT image FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    logger.info("Binary image data found for ID: " + imageId);
                    return rs.getBytes("image");
                }
            }
        }

        logger.warning("No image binary data found for ID: " + imageId);
        return null;
    }

    @Override
    public boolean deleteImage(int imageId) throws SQLException {
        logger.info("Deleting image with ID: " + imageId);

        String query = "DELETE FROM Images WHERE imageId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, imageId);
            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Image deletion " + (success ? "successful" : "failed"));
            return success;
        }
    }

    @Override
    public boolean deleteAllImagesForAttack(int attackId) throws SQLException {
        logger.info("Deleting all images for attackId: " + attackId);

        String query = "DELETE FROM Images WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            boolean success = pstmt.executeUpdate() >= 0;
            logger.info("All images deletion for attackId " + attackId + (success ? " successful" : " failed"));
            return success;
        }
    }

    public int getImageCountForAttack(int attackId) throws SQLException {
        logger.info("Counting images for attackId: " + attackId);

        String query = "SELECT COUNT(*) FROM Images WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("Found " + count + " images for attackId: " + attackId);
                    return count;
                }
            }
        }

        logger.warning("Failed to count images for attackId: " + attackId);
        return 0;
    }
}
