package com.ransommonitor.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.Victim;
import com.ransommonitor.config.DbConfig;

public class AttacksDaoImpl implements AttacksDao {
    DownloadUrlsDaoImpl downloadUrlsDao = new DownloadUrlsDaoImpl();
    ImagesDaoImpl imagesDao = new ImagesDaoImpl();
    PreparedStatement pstmt;
    String query;

    @Override
    public String addNewAttack(Attack attack) throws SQLException {
        query = "INSERT INTO Attacks(attackerId, victimId, deadlines, isPublished, " +
                "isForSale, postedAt, dataSizes, description, category, ransomAmount, saleAmount) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING attackId";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

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

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            attack.setAttackId(rs.getInt("attackId"));
            return "Added Attack";
        }
        return "Adding Failed";
    }

    @Override
    public List<Attack> getAllAttacks() throws SQLException {
        query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "ORDER BY a.postedAt ASC";

        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();
        return getFromResultSet(res);
    }

    @Override
    public List<Attack> getAttacksByAttackerName(String attackerName) throws SQLException {
        query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE at.attackerName = ? " +
                "ORDER BY a.postedAt ASC";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, attackerName);
        ResultSet res = pstmt.executeQuery();

        return getFromResultSet(res);
    }

    @Override
    public List<Attack> getAttacksByVictimName(String victimName) throws SQLException {
        List<Attack> attacks = new ArrayList<>();
        query = "SELECT a.*, at.*, v.* FROM Attacks a " +
                "JOIN Attacker at ON a.attackerId = at.attackerId " +
                "JOIN Victims v ON a.victimId = v.victimId " +
                "WHERE v.victimName = ? " +
                "ORDER BY a.postedAt ASC";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, victimName);
        ResultSet res = pstmt.executeQuery();
        return getFromResultSet(res);
    }

    @Override
    public boolean updateAttack(Attack attack) throws SQLException {
        query = "UPDATE Attacks SET attackerId = ?, victimId = ?, deadlines = ?, " +
                "isPublished = ?, isForSale = ?, dataSizes = ?, description = ?, " +
                "category = ?, isNegotiated = ?, ransomAmount = ?, saleAmount = ? " +
                "WHERE attackId = ?";

        pstmt = DbConfig.getPs(query);
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

        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    @Override
    public boolean incrementVisitCount(int attackId) throws SQLException {
        query = "UPDATE Attacks SET noOfVisits = noOfVisits + 1, lastVisitedAt = CURRENT_TIMESTAMP " +
                "WHERE attackId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, attackId);
        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    public List<Attack> getFromResultSet(ResultSet res) throws SQLException {

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