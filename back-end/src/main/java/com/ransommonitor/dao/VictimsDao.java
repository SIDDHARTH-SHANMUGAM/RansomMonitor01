package com.ransommonitor.dao;


import java.sql.SQLException;
import java.util.List;
import com.ransommonitor.bean.Victim;

public interface VictimsDao {
    String addNewVictim(Victim victim) throws SQLException;
    boolean updateVictim(Victim victim) throws SQLException;
    List<Victim> getAllVictims() throws SQLException;
    Victim getVictimById(int victimId) throws SQLException;
    Victim getVictimByName(String victimName) throws SQLException;
    boolean deleteVictim(int victimId) throws SQLException;
}
