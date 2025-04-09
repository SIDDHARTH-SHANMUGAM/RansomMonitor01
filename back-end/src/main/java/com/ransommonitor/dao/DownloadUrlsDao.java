package com.ransommonitor.dao;

import java.sql.SQLException;
import java.util.List;
import com.ransommonitor.bean.DownloadUrl;

public interface DownloadUrlsDao {
    boolean addDownloadUrl(DownloadUrl downloadUrl) throws SQLException;
    boolean updateDownloadUrl(DownloadUrl downloadUrl) throws SQLException;
    boolean deleteDownloadUrl(int downloadUrlId) throws SQLException;
    boolean deleteAllUrlsForAttack(int attackId) throws SQLException;
    List<DownloadUrl> getDownloadUrlsByAttack(int attackId) throws SQLException;
    DownloadUrl getDownloadUrlById(int downloadUrlId) throws SQLException;
}