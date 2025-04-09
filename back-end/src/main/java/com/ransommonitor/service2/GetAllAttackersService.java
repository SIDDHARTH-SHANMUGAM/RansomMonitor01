package com.ransommonitor.service2;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.AttackerSiteUrl;
import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllAttackersService {

    private final AttackersDao attackersDao = new AttackersDaoImpl();
    private final AttackersSiteUrlsDao attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();

    public List<Map<String, Object>> getAllAttackers() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Attacker> attackers = attackersDao.getAllAttackers();
        for (Attacker attacker : attackers) {

            Map<String, Object> attackerMap = new HashMap<>();
            attackerMap.put("attackerId", attacker.getAttackerId());
            attackerMap.put("attackerName", attacker.getAttackerName());
            attackerMap.put("monitorStatus", attacker.getMonitorStatus());
            List<AttackerSiteUrl> urls = attackersSiteUrlsDao.getUrlsByAttackerName(attacker.getAttackerName());

            attackerMap.put("urls", urls);
            result.add(attackerMap);
        }
        return result;
    }
}
