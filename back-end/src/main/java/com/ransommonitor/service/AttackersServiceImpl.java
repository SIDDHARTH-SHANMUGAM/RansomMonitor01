package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.dao.AttackersDao;

import java.sql.SQLException;
import java.util.List;

public class AttackersServiceImpl implements AttackersService {

        private AttackersDao attackerDao;

        public AttackersServiceImpl(AttackersDao attackerDao) {
            this.attackerDao = attackerDao;
        }

        public boolean addAttacker(Attacker attacker) throws SQLException {
            // Validate required fields
            System.out.println("inside addAttacker");
            if (attacker.getAttackerName() == null || attacker.getAttackerName().trim().isEmpty()) {
                return false;
            }
            return Boolean.parseBoolean(attackerDao.addNewAttacker(attacker));

        }


        public List<Attacker> getAttackers() throws SQLException {
            return attackerDao.getAllAttackers();
        }
}
