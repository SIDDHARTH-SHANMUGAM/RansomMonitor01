package com.ransommonitor.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.Victim;
import com.ransommonitor.utils.DbConnect;

public class AttacksDaoImpl implements AttacksDao {

    private static final Logger logger = Logger.getLogger(AttacksDaoImpl.class.getName());

    DownloadUrlsDaoImpl downloadUrlsDao = new DownloadUrlsDaoImpl();
    ImagesDaoImpl imagesDao = new ImagesDaoImpl();

    @Override
    public String addNewAttack(Attack attack) throws SQLException {
        logger.info("Adding new attack for attacker ID: " + attack.getAttacker().getAttackerId()
                + ", victim ID: " + attack.getVictim().getVictimId());

        String query = "INSERT INTO Attacks(attackerId, victimId, deadlines, isPublished, " +
                "isForSale, postedAt, dataSizes, description, category, ransomAmount, saleAmount) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING attackId";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attack.getAttacker().getAttackerId());
            pstmt.setInt(2, attack.getVictim().getVictimId());
            pstmt.setString(3, attack.getDeadlines());
            pstmt.setBoolean(4, attack.isPublished());
            pstmt.setBoolean(5, attack.isForSale());
            pstmt.setString(6, attack.getPostedAt());
            pstmt.setString(7, attack.getDataSizes());
            pstmt.setString(8, attack.getDescription());
            pstmt.setString(9, attack.getCategory());
            pstmt.setString(10, attack.getRansomAmount());
            pstmt.setString(11, attack.getSaleAmount());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("attackId");
                    attack.setAttackId(id);
                    logger.info("New attack added with ID: " + id);
                    return "Added Attack";
                }
            }
        }
        logger.warning("Failed to add attack");
        return "Adding Failed";
    }

    @Override
    public List<Attack> getAllAttacks() throws SQLException {
        logger.info("Fetching all attacks");
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            List<Attack> attacks = getFromResultSet(res);
            logger.info("Total attacks fetched: " + attacks.size());
            return attacks;
        }
    }

    @Override
    public List<Attack> getAttacksByAttackerName(String attackerName) throws SQLException {
        logger.info("Fetching attacks by attacker name: " + attackerName);
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE at.attackerName = ? " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, attackerName);
            try (ResultSet res = pstmt.executeQuery()) {
                List<Attack> attacks = getFromResultSet(res);
                logger.info("Attacks found for attacker '" + attackerName + "': " + attacks.size());
                return attacks;
            }
        }
    }

    @Override
    public List<Attack> getAttacksByVictimName(String victimName) throws SQLException {
        logger.info("Fetching attacks by victim name: " + victimName);
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE v.victimName = ? " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, victimName);
            try (ResultSet res = pstmt.executeQuery()) {
                List<Attack> attacks = getFromResultSet(res);
                logger.info("Attacks found for victim '" + victimName + "': " + attacks.size());
                return attacks;
            }
        }
    }

    @Override
    public boolean updateAttack(Attack attack) throws SQLException {
        logger.info("Updating attack with ID: " + attack.getAttackId());
        String query = "UPDATE Attacks SET attackerId = ?, victimId = ?, deadlines = ?, " +
                "isPublished = ?, isForSale = ?, dataSizes = ?, description = ?, " +
                "category = ?, isNegotiated = ?, ransomAmount = ?, saleAmount = ? " +
                "WHERE attackId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attack.getAttacker().getAttackerId());
            pstmt.setInt(2, attack.getVictim().getVictimId());
            pstmt.setString(3, attack.getDeadlines());
            pstmt.setBoolean(4, attack.isPublished());
            pstmt.setBoolean(5, attack.isForSale());
            pstmt.setString(6, attack.getDataSizes());
            pstmt.setString(7, attack.getDescription());
            pstmt.setString(8, attack.getCategory());
            pstmt.setBoolean(9, attack.isNegotiated());
            pstmt.setString(10, attack.getRansomAmount());
            pstmt.setString(11, attack.getSaleAmount());
            pstmt.setInt(12, attack.getAttackId());

            boolean success = pstmt.executeUpdate() > 0;
            logger.info("Update success: " + success);
            return success;
        }
    }

    private List<Attack> getFromResultSet(ResultSet res) throws SQLException {
        List<Attack> attacks = new ArrayList<>();
        while (res.next()) {
            Attack attack = new Attack(
                    res.getInt("attackId"),
                    new Attacker(
                            res.getInt("attackerId"),
                            res.getString("attackerName"),
                            res.getString("email"),
                            res.getString("toxId"),
                            res.getString("sessionId"),
                            res.getString("description"),
                            res.getString("firstAttackAt"),
                            res.getBoolean("isRAAS"),
                            res.getBoolean("monitorStatus"),
                            res.getString("createdAt"),
                            res.getString("updatedAt")
                    ),
                    new Victim(
                            res.getInt("victimId"),
                            res.getString("victimName"),
                            res.getString("country"),
                            res.getString("description"),
                            res.getString("victimURL"),
                            res.getString("revenue"),
                            res.getString("createdAt"),
                            res.getString("updatedAt")
                    ),
                    res.getString("deadlines"),
                    res.getBoolean("isPublished"),
                    res.getBoolean("isForSale"),
                    res.getString("postedAt"),
                    res.getInt("noOfVisits"),
                    res.getString("dataSizes"),
                    res.getString("description"),
                    res.getString("lastVisitedAt"),
                    res.getString("category"),
                    res.getBoolean("isNegotiated"),
                    res.getString("ransomAmount"),
                    res.getString("saleAmount"),
                    res.getString("createdAt"),
                    res.getString("updatedAt"),
                    downloadUrlsDao.getDownloadUrlsByAttack(res.getInt("attackId")),
                    imagesDao.getImagesByAttack(res.getInt("attackId"))
            );
            attacks.add(attack);
        }
        return attacks;
    }
}
