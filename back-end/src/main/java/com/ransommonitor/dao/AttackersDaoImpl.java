package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.utils.DbConnect;

public class AttackersDaoImpl implements AttackersDao {

    @Override
    public String addNewAttacker(Attacker attacker) throws SQLException {
        String query = "INSERT INTO Attacker(attackerName, email, toxId, sessionId, description, " +
                "firstAttackAt, isRAAS, monitorStatus) VALUES(?, ?, ?, ?, ?, ?, ?, ?) RETURNING attackerId";
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

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                attacker.setAttackerId(rs.getInt("attackerId"));
                return "Added Attacker";
            }
            return "Adding Failed";
        }
    }

    @Override
    public boolean updateAttacker(Attacker attacker) throws SQLException {
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

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Attacker> getAllAttackers() throws SQLException {
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
        return attackers;
    }

    @Override
    public Attacker getAttackerById(int attackerId) throws SQLException {
        String query = "SELECT * FROM Attacker WHERE attackerId = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackerId);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
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
        return null;
    }

    @Override
    public Attacker getAttackerByName(String attackerName) throws SQLException {
        String query = "SELECT * FROM Attacker WHERE attackerName = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, attackerName);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
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
        return null;
    }

    @Override
    public boolean deleteAttacker(int attackerId) throws SQLException {
        String query = "DELETE FROM Attacker WHERE attackerId = ?";
        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, attackerId);
            return pstmt.executeUpdate() > 0;
        }
    }
}