package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;

import java.sql.SQLException;
import java.util.List;

public interface AttackersService {
    String addNewAttacker(Attacker attacker, List<String> urlList) throws SQLException;
    List<Attacker> getAttackers() throws SQLException;

    boolean updateAttacker(Attacker attacker) throws SQLException;
}
