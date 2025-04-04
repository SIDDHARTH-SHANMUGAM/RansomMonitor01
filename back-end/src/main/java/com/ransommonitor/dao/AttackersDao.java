package com.ransommonitor.dao;


import java.sql.SQLException;
import java.util.List;
import com.ransommonitor.bean.Attacker;

public interface AttackersDao {
    String addNewAttacker(Attacker attacker) throws SQLException;
    boolean updateAttacker(Attacker attacker) throws SQLException;
    List<Attacker> getAllAttackers() throws SQLException;
    Attacker getAttackerById(int attackerId) throws SQLException;
    Attacker getAttackerByName(String attackerName) throws SQLException;
    boolean deleteAttacker(int attackerId) throws SQLException;
}