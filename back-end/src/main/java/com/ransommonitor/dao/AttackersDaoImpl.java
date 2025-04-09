package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.utils.DbConnect;

public class AttackersDaoImpl implements AttackersDao {

    private static final Logger logger = Logger.getLogger(AttackersDaoImpl.class.getName());

    @Override
    public boolean addNewAttacker(Attacker attacker) throws SQLException {
        logger.info("Called addNewAttacker()");

        String query = "INSERT INTO Attacker(attackerName, email, toxId, sessionId, description, " +
                "firstAttackAt, isRAAS, monitorStatus) VALUES(?, ?, ?, ?, ?, ?, ?, ?) RETURNING attackerId";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            logger.info("Preparing statement for adding attacker: " + attacker.getAttackerName());

            pstmt.setString(1, attacker.getAttackerName());
            pstmt.setString(2, attacker.getEmail());
            pstmt.setString(3, attacker.getToxId());
            pstmt.setString(4, attacker.getSessionId());
            pstmt.setString(5, attacker.getDescription());
            pstmt.setString(6, attacker.getFirstAttackAt());
            pstmt.setBoolean(7, attacker.isRAAS());
            pstmt.setBoolean(8, attacker.getMonitorStatus());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                attacker.setAttackerId(rs.getInt("attackerId"));
                return true;
            }
            logger.warning("Failed to add attacker.");
            return false;
        }
    }

    @Override
    public boolean updateAttacker(Attacker attacker) throws SQLException {
        logger.info("Called updateAttacker() for attacker ID: " + attacker.getAttackerId());

        String query = "UPDATE Attacker SET attackerName = ?, email = ?, toxId = ?, sessionId = ?, " +
                "description = ?, firstAttackAt = ?, isRAAS = ?, monitorStatus = ? " +
                "WHERE attackerId = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, attacker.getAttackerName());
            pstmt.setString(2, attacker.getEmail());
            pstmt.setString(3, attacker.getToxId());
            pstmt.setString(4, attacker.getSessionId());
            pstmt.setString(5, attacker.getDescription());
            pstmt.setString(6, attacker.getFirstAttackAt());
            pstmt.setBoolean(7, attacker.isRAAS());
            pstmt.setBoolean(8, attacker.getMonitorStatus());
            pstmt.setInt(9, attacker.getAttackerId());

            boolean updated = pstmt.executeUpdate() > 0;
            logger.info(updated ? "Attacker updated successfully." : "Attacker update failed.");
            return updated;
        }
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
    public List<Attacker> getAllAttackers() throws SQLException {
        logger.info("Called getAllAttackers()");
        List<Attacker> attackers = new ArrayList<>();
        String query = "SELECT * FROM Attacker ORDER BY attackerName ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                attackers.add(new Attacker(
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
                ));
            }
        }

        logger.info("Retrieved " + attackers.size() + " attackers.");
        return attackers;
    }

    @Override
    public Attacker getAttackerByName(String attackerName) throws SQLException {
        logger.info("Called getAttackerByName() for name: " + attackerName);

        String query = "SELECT * FROM Attacker WHERE attackerName = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, attackerName);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                logger.info("Attacker found with name: " + attackerName);
                return new Attacker(
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
                );
            }
        }

        logger.warning("No attacker found with name: " + attackerName);
        return null;
    }


    @Override
    public boolean deleteAttacker(int attackerId) throws SQLException {
        logger.info("Called deleteAttacker() for ID: " + attackerId);

        String query = "DELETE FROM Attacker WHERE attackerId = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackerId);
            boolean deleted = pstmt.executeUpdate() > 0;
            logger.info(deleted ? "Attacker deleted successfully." : "Attacker deletion failed.");
            return deleted;
        }
    }

    @Override
    public boolean isAttackerAvailableByName(String attackerName) throws SQLException {
        String query = "SELECT * FROM Attacker WHERE attackerName = ?";
        try (Connection conn = DbConnect.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, attackerName);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAttackerAvailableById(int attackerId) throws SQLException {
        String query = "SELECT * FROM Attacker WHERE attackerId = ?";
        try (Connection conn = DbConnect.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, attackerId);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                return true;
            }
        }
        return false;
    }
}
