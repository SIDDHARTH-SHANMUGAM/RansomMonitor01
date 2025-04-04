package com.ransommonitor.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.config.DbConfig;

public class AttackersDaoImpl implements AttackersDao {

    PreparedStatement pstmt;
    String query;

    @Override
    public String addNewAttacker(Attacker attacker) throws SQLException {
        query = "INSERT INTO Attacker(attackerName, email, toxId, sessionId, description, " +
                "firstAttackAt, isRAAS, monitorStatus) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?) RETURNING attackerId";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

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

    @Override
    public boolean updateAttacker(Attacker attacker) throws SQLException {
        query = "UPDATE Attacker SET attackerName = ?, email = ?, toxId = ?, sessionId = ?, " +
                "description = ?, firstAttackAt = ?, isRAAS = ?, monitorStatus = ? " +
                "WHERE attackerId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, attacker.getAttackerName());
        pstmt.setString(2, attacker.getEmail());
        pstmt.setString(3, attacker.getToxId());
        pstmt.setString(4, attacker.getSessionId());
        pstmt.setString(5, attacker.getDescription());
        pstmt.setString(6, attacker.getFirstAttackAt());
        pstmt.setBoolean(7, attacker.isRAAS());
        pstmt.setBoolean(8, attacker.getMonitorStatus());
        pstmt.setInt(9, attacker.getAttackerId());

        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    @Override
    public List<Attacker> getAllAttackers() throws SQLException {
        List<Attacker> attackers = new ArrayList<>();
        query = "SELECT * FROM Attacker ORDER BY attackerName ASC";
        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            Attacker attacker = new Attacker(
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
            attackers.add(attacker);
        }
        return attackers;
    }

    @Override
    public Attacker getAttackerById(int attackerId) throws SQLException {
        query = "SELECT * FROM Attacker WHERE attackerId = ?";
        pstmt = DbConfig.getPs(query);
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
        return null;
    }

    @Override
    public Attacker getAttackerByName(String attackerName) throws SQLException {
        query = "SELECT * FROM Attacker WHERE attackerName = ?";
        pstmt = DbConfig.getPs(query);
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
        return null;
    }

    @Override
    public boolean deleteAttacker(int attackerId) throws SQLException {
        query = "DELETE FROM Attacker WHERE attackerId = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackerId);
        int rs = pstmt.executeUpdate();
        return rs > 0;
    }
}