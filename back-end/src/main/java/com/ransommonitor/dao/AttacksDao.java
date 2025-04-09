package com.ransommonitor.dao;


import java.sql.SQLException;
import java.util.List;

import com.ransommonitor.bean.Attack;

public interface AttacksDao {
    boolean addNewAttack(Attack attack) throws SQLException;
    boolean updateAttack(Attack attack) throws SQLException;
    List<Attack> getAllAttacks() throws SQLException;
    List<Attack> getAttacksByAttackerName(String attackerName) throws SQLException;
    List<Attack> getAttacksByVictimName(String victimName) throws SQLException;
}