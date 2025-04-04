package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;

import java.sql.SQLException;

public interface AttackersService {
    public boolean addAttacker(Attacker attacker) throws SQLException;
}
