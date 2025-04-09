package com.ransommonitor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ransommonitor.bean.Victim;
import com.ransommonitor.utils.DbConnect;

public class VictimsDaoImpl implements VictimsDao {

    @Override
    public String addNewVictim(Victim victim) throws SQLException {
        String query = "INSERT INTO Victims(victimName, country, description, victimURL, revenue) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING victimId";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, victim.getVictimName());
            pstmt.setString(2, victim.getCountry());
            pstmt.setString(3, victim.getDescription());
            pstmt.setString(4, victim.getVictimURL());
            pstmt.setString(5, victim.getRevenue());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    victim.setVictimId(rs.getInt("victimId"));
                    return "Added Victim";
                }
            }
        }
        return "Adding Failed";
    }

    @Override
    public boolean updateVictim(Victim victim) throws SQLException {
        String query = "UPDATE Victims SET victimName = ?, country = ?, description = ?, " +
                "victimURL = ?, revenue = ? WHERE victimId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, victim.getVictimName());
            pstmt.setString(2, victim.getCountry());
            pstmt.setString(3, victim.getDescription());
            pstmt.setString(4, victim.getVictimURL());
            pstmt.setString(5, victim.getRevenue());
            pstmt.setInt(6, victim.getVictimId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Victim> getAllVictims() throws SQLException {
        List<Victim> victims = new ArrayList<>();
        String query = "SELECT * FROM Victims ORDER BY victimName ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet res = pstmt.executeQuery()) {

            while (res.next()) {
                victims.add(new Victim(
                        res.getInt("victimId"),
                        res.getString("victimName"),
                        res.getString("country"),
                        res.getString("description"),
                        res.getString("victimURL"),
                        res.getString("revenue"),
                        res.getString("createdAt"),
                        res.getString("updatedAt")
                ));
            }
        }
        return victims;
    }

    @Override
    public Victim getVictimById(int victimId) throws SQLException {
        String query = "SELECT * FROM Victims WHERE victimId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, victimId);

            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return new Victim(
                            res.getInt("victimId"),
                            res.getString("victimName"),
                            res.getString("country"),
                            res.getString("description"),
                            res.getString("victimURL"),
                            res.getString("revenue"),
                            res.getString("createdAt"),
                            res.getString("updatedAt")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Victim getVictimByName(String victimName) throws SQLException {
        String query = "SELECT * FROM Victims WHERE victimName = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, victimName);

            try (ResultSet res = pstmt.executeQuery()) {
                if (res.next()) {
                    return new Victim(
                            res.getInt("victimId"),
                            res.getString("victimName"),
                            res.getString("country"),
                            res.getString("description"),
                            res.getString("victimURL"),
                            res.getString("revenue"),
                            res.getString("createdAt"),
                            res.getString("updatedAt")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public boolean deleteVictim(int victimId) throws SQLException {
        String query = "DELETE FROM Victims WHERE victimId = ?";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, victimId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Victim> getVictimsByCountry(String country) throws SQLException {
        List<Victim> victims = new ArrayList<>();
        String query = "SELECT * FROM Victims WHERE country = ? ORDER BY victimName ASC";

        try (Connection conn = DbConnect.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, country);

            try (ResultSet res = pstmt.executeQuery()) {
                while (res.next()) {
                    victims.add(new Victim(
                            res.getInt("victimId"),
                            res.getString("victimName"),
                            res.getString("country"),
                            res.getString("description"),
                            res.getString("victimURL"),
                            res.getString("revenue"),
                            res.getString("createdAt"),
                            res.getString("updatedAt")
                    ));
                }
            }
        }
        return victims;
    }
}