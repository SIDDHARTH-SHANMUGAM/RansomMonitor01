package com.ransommonitor.service;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;

import java.sql.SQLException;
import java.util.List;

public interface AttacksService {
    void addNewAttacks(List<Attack> attacks, Attacker attacker) throws SQLException;

    List<Attack> getAllAtacks() throws SQLException;
}
