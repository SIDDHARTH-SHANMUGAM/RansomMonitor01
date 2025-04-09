package com.ransommonitor.dao;


import java.sql.SQLException;
import java.util.List;
import com.ransommonitor.bean.Attacker;

public interface AttackersDao {
    boolean addNewAttacker(Attacker attacker) throws SQLException;
    boolean updateAttacker(Attacker attacker) throws SQLException;
    boolean updateAttackerMonitoringStatus(int attackerId, boolean status) throws SQLException;
    boolean deleteAttacker(int attackerId) throws SQLException;
    boolean isAttackerAvailableByName(String attackerName) throws SQLException;
    boolean isAttackerAvailableById(int attackerId) throws SQLException;
    Attacker getAttackerByName(String attackerName) throws SQLException;
    List<Attacker> getAllAttackers() throws SQLException;
}