package com.ransommonitor.dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Victim;
import com.ransommonitor.config.DbConfig;

public class VictimsDaoImpl implements VictimsDao {

    PreparedStatement pstmt;
    String query;

    @Override
    public String addNewVictim(Victim victim) throws SQLException {
        query = "INSERT INTO Victims(victimName, country, description, victimURL, revenue) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING victimId";

        pstmt = DbConfig.getPs(query);
        if (pstmt == null)
            return "pstmt failed";

        pstmt.setString(1, victim.getVictimName());
        pstmt.setString(2, victim.getCountry());
        pstmt.setString(3, victim.getDescription());
        pstmt.setString(4, victim.getVictimURL());
        pstmt.setDouble(5, victim.getRevenue());

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            victim.setVictimId(rs.getInt("victimId"));
            return "Added Victim";
        }
        return "Adding Failed";
    }

    @Override
    public boolean updateVictim(Victim victim) throws SQLException {
        query = "UPDATE Victims SET victimName = ?, country = ?, description = ?, " +
                "victimURL = ?, revenue = ? WHERE victimId = ?";

        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, victim.getVictimName());
        pstmt.setString(2, victim.getCountry());
        pstmt.setString(3, victim.getDescription());
        pstmt.setString(4, victim.getVictimURL());
        pstmt.setDouble(5, victim.getRevenue());
        pstmt.setInt(6, victim.getVictimId());

        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    @Override
    public List<Victim> getAllVictims() throws SQLException {
        List<Victim> victims = new ArrayList<>();
        query = "SELECT * FROM Victims ORDER BY victimName ASC";
        pstmt = DbConfig.getPs(query);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            Victim victim = new Victim(
                    res.getInt("victimId"),
                    res.getString("victimName"),
                    res.getString("country"),
                    res.getString("description"),
                    res.getString("victimURL"),
                    res.getDouble("revenue"),
                    res.getString("createdAt"),
                    res.getString("updatedAt")
            );
            victims.add(victim);
        }
        return victims;
    }

    @Override
    public Victim getVictimById(int victimId) throws SQLException {
        query = "SELECT * FROM Victims WHERE victimId = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, victimId);
        ResultSet res = pstmt.executeQuery();

        if (res.next()) {
            return new Victim(
                    res.getInt("victimId"),
                    res.getString("victimName"),
                    res.getString("country"),
                    res.getString("description"),
                    res.getString("victimURL"),
                    res.getDouble("revenue"),
                    res.getString("createdAt"),
                    res.getString("updatedAt")
            );
        }
        return null;
    }

    @Override
    public Victim getVictimByName(String victimName) throws SQLException {
        query = "SELECT * FROM Victims WHERE victimName = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, victimName);
        ResultSet res = pstmt.executeQuery();

        if (res.next()) {
            return new Victim(
                    res.getInt("victimId"),
                    res.getString("victimName"),
                    res.getString("country"),
                    res.getString("description"),
                    res.getString("victimURL"),
                    res.getDouble("revenue"),
                    res.getString("createdAt"),
                    res.getString("updatedAt")
            );
        }
        return null;
    }

    @Override
    public boolean deleteVictim(int victimId) throws SQLException {
        query = "DELETE FROM Victims WHERE victimId = ?";
        pstmt = DbConfig.getPs(query);
        pstmt.setInt(1, victimId);
        int rs = pstmt.executeUpdate();
        return rs > 0;
    }

    // Additional useful methods
    public List<Victim> getVictimsByCountry(String country) throws SQLException {
        List<Victim> victims = new ArrayList<>();
        query = "SELECT * FROM Victims WHERE country = ? ORDER BY victimName ASC";
        pstmt = DbConfig.getPs(query);
        pstmt.setString(1, country);
        ResultSet res = pstmt.executeQuery();

        while (res.next()) {
            Victim victim = new Victim(
                    res.getInt("victimId"),
                    res.getString("victimName"),
                    res.getString("country"),
                    res.getString("description"),
                    res.getString("victimURL"),
                    res.getDouble("revenue"),
                    res.getString("createdAt"),
                    res.getString("updatedAt")
            );
            victims.add(victim);
        }
        return victims;
    }
}