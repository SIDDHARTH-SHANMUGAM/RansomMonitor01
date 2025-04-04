package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.Victim;
import com.ransommonitor.utils.DbConnect;

public class AttacksDaoImpl implements AttacksDao {
    DownloadUrlsDaoImpl downloadUrlsDao = new DownloadUrlsDaoImpl();
    ImagesDaoImpl imagesDao = new ImagesDaoImpl();

    @Override
    public String addNewAttack(Attack attack) throws SQLException {
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
            pstmt.setString(6, attack.getDataSizes());
            pstmt.setString(7, attack.getDescription());
            pstmt.setString(8, attack.getCategory());
            pstmt.setString(9, attack.getRansomAmount());
            pstmt.setString(10, attack.getSaleAmount());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    attack.setAttackId(rs.getInt("attackId"));
                    return "Added Attack";
                }
            }
        }
        return "Adding Failed";
    }

    @Override
    public List<Attack> getAllAttacks() throws SQLException {
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {
            return getFromResultSet(res);
        }
    }

    @Override
    public List<Attack> getAttacksByAttackerName(String attackerName) throws SQLException {
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE at.attackerName = ? " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, attackerName);
            try (ResultSet res = pstmt.executeQuery()) {
                return getFromResultSet(res);
            }
        }
    }

    @Override
    public List<Attack> getAttacksByVictimName(String victimName) throws SQLException {
        String query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE v.victimName = ? " +
                "ORDER BY a.postedAt ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, victimName);
            try (ResultSet res = pstmt.executeQuery()) {
                return getFromResultSet(res);
            }
        }
    }

    @Override
    public boolean updateAttack(Attack attack) throws SQLException {
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

            return pstmt.executeUpdate() > 0;
        }
    }



    private List<Attack> getFromResultSet(ResultSet res) throws SQLException {
        List<Attack> attacks = new ArrayList<>();
        while (res.next()) {
            attacks.add(new Attack(
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
                            res.getDouble("revenue"),
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
                    res.getString("updatedAt"),
                    downloadUrlsDao.getDownloadUrlsByAttack(res.getInt("attackId")),
                    imagesDao.getImagesByAttack(res.getInt("attackId"))
            ));
        }
        return attacks;
    }
}
