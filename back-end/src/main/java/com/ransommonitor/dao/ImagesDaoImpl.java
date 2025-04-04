package com.ransommonitor.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Image;
import com.ransommonitor.config.DbConfig;

public class ImagesDaoImpl implements ImagesDao {

    PreparedStatement pstmt;
    String query;

    @Override
    public String addImage(Image image) throws SQLException {
        query = "INSERT INTO Images(attackId, image) VALUES(?, ?) RETURNING imageId, createdAt";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

        pstmt.setInt(1, image.getAttackId());
        pstmt.setString(2, image.getImage());

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            image.setImageId(rs.getInt("imageId"));
            return "Image added successfully";
        }
        return "Failed to add image";
    }

    @Override
    public boolean updateImage(Image image) throws SQLException {
        query = "UPDATE Images SET image = ? WHERE imageId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, image.getImage());
        pstmt.setInt(2, image.getImageId());

        int result = pstmt.executeUpdate();
        return result > 0;
    }

    @Override
    public List<Image> getImagesByAttack(int attackId) throws SQLException {
        List<Image> images = new ArrayList<>();
        query = "SELECT imageId, attackId, createdAt FROM Images WHERE attackId = ? ORDER BY createdAt DESC";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Image image = new Image(
                    rs.getInt("imageId"),
                    rs.getInt("attackId"),
                    null,
                    rs.getString("createdAt")
            );
            images.add(image);
        }
        return images;
    }

    @Override
    public Image getImageById(int imageId) throws SQLException {
        query = "SELECT * FROM Images WHERE imageId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, imageId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Image(
                    rs.getInt("imageId"),
                    rs.getInt("attackId"),
                    rs.getString("image"),
                    rs.getString("createdAt")
            );
        }
        return null;
    }

    @Override
    public byte[] getImageDataById(int imageId) throws SQLException {
        query = "SELECT image FROM Images WHERE imageId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, imageId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getBytes("image");
        }
        return null;
    }

    @Override
    public boolean deleteImage(int imageId) throws SQLException {
        query = "DELETE FROM Images WHERE imageId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, imageId);
        int result = pstmt.executeUpdate();
        return result > 0;
    }

    @Override
    public boolean deleteAllImagesForAttack(int attackId) throws SQLException {
        query = "DELETE FROM Images WHERE attackId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        int result = pstmt.executeUpdate();
        return result >= 0; // Returns true even if 0 rows deleted
    }

    public int getImageCountForAttack(int attackId) throws SQLException {
        query = "SELECT COUNT(*) FROM Images WHERE attackId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}