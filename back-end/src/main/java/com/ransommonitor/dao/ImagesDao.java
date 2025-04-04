package com.ransommonitor.dao;

import java.sql.SQLException;
import java.util.List;
import com.ransommonitor.bean.Image;

public interface ImagesDao {
    String addImage(Image image) throws SQLException;
    boolean updateImage(Image image) throws SQLException;
    List<Image> getImagesByAttack(int attackId) throws SQLException;
    Image getImageById(int imageId) throws SQLException;
    boolean deleteImage(int imageId) throws SQLException;
    boolean deleteAllImagesForAttack(int attackId) throws SQLException;
    byte[] getImageDataById(int imageId) throws SQLException;
}