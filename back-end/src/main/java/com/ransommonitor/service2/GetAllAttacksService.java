package com.ransommonitor.service2;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.dao.AttacksDao;
import com.ransommonitor.dao.AttacksDaoImpl;

import java.sql.SQLException;
import java.util.List;

public class GetAllAttacksService {
    private static AttacksDao attacksDao = new AttacksDaoImpl();
    public List<Attack> getAllAtacks() throws SQLException {
        return attacksDao.getAllAttacks();
    }
}
