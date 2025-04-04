package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;

import java.sql.SQLException;
import java.util.List;

public interface AttackersService {
    public boolean addAttacker(Attacker attacker) throws SQLException;
    public List<Attacker> getAttackers() throws SQLException;
}
