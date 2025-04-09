package com.ransommonitor.service2;

import com.ransommonitor.dao.AttackersDao;
import com.ransommonitor.dao.AttackersDaoImpl;
import com.ransommonitor.dao.AttackersSiteUrlsDao;
import com.ransommonitor.dao.AttackersSiteUrlsDaoImpl;
import java.sql.SQLException;

public class DeleteAttackerService {

    private AttackersDao attackerDao;
    private AttackersSiteUrlsDao attackersSiteUrlsDao;

    public DeleteAttackerService() {
        this.attackerDao = new AttackersDaoImpl();
        this.attackersSiteUrlsDao = new AttackersSiteUrlsDaoImpl();
    }

    public String deleteAttacker(int attackerId) throws SQLException {

        if(!attackerDao.isAttackerAvailableById(attackerId))
        {
            return "Attacker is not available";
        }
        attackersSiteUrlsDao.deleteUrlByAttackerId(attackerId);
        if(attackerDao.deleteAttacker(attackerId)) {
            return "Deleted Attacker";
        }
        return "Attacker is not Deleted";
    }
}
