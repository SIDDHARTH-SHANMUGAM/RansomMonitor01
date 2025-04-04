package com.ransommonitor.service;

import com.ransommonitor.bean.Attacker;
import com.ransommonitor.dao.AttackersDao;

import java.sql.SQLException;

public class AttackersServiceImpl implements AttackersService {

        private AttackersDao attackerDao;

        public AttackersServiceImpl(AttackersDao attackerDao) {
            this.attackerDao = attackerDao;
        }

        public boolean addAttacker(Attacker attacker) throws SQLException {
            // Validate required fields
            if (attacker.getAttackerName() == null || attacker.getAttackerName().trim().isEmpty()) {
                return false;
            }

            if (attacker.getSessionId() == null || attacker.getSessionId().trim().isEmpty()) {
                return false;
            }

            // Validate email format if present
            if (attacker.getEmail() != null && !attacker.getEmail().trim().isEmpty()) {
                if (!isValidEmail(attacker.getEmail())) {
                    return false;
                }
            }

            // Set current timestamp if firstAttackAt is not set
            if (attacker.getFirstAttackAt() == null || attacker.getFirstAttackAt().trim().isEmpty()) {
                attacker.setFirstAttackAt(java.time.LocalDateTime.now().toString());
            }

            return Boolean.parseBoolean(attackerDao.addNewAttacker(attacker));
        }

        private boolean isValidEmail(String email) {
            // Simple email validation regex
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            return email.matches(emailRegex);
        }
}
