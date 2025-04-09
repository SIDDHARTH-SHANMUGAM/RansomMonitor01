package com.ransommonitor.service;

import com.ransommonitor.bean.Attack;
import com.ransommonitor.bean.Attacker;
import com.ransommonitor.bean.DownloadUrl;
import com.ransommonitor.bean.Image;
import com.ransommonitor.dao.*;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AttacksServiceImpl implements AttacksService {

    private static final Logger logger = Logger.getLogger(AttacksServiceImpl.class.getName());

    AttacksDao attacksDao;
    VictimsDao victimsDao;
    DownloadUrlsDao downloadUrlsDao;
    ImagesDao imagesDao;

    public AttacksServiceImpl() {
        logger.info("Initializing DAOs in AttacksServiceImpl constructor");
        attacksDao = new AttacksDaoImpl();
        victimsDao = new VictimsDaoImpl();
        downloadUrlsDao = new DownloadUrlsDaoImpl();
        imagesDao = new ImagesDaoImpl();
    }

    @Override
    public void addNewAttacks(List<Attack> attacks, Attacker attacker) throws SQLException {
        logger.info("Entering addNewAttacks method");

        if (attacks != null && !attacks.isEmpty()) {
            logger.info("Found " + attacks.size() + " attacks to process for attacker: " + attacker.getAttackerName());

            List<Attack> prev = attacksDao.getAttacksByAttackerName(attacker.getAttackerName());
            logger.info("Fetched " + prev.size() + " previous attacks for comparison");

            for (Attack attack : attacks) {
                boolean found = false;
                attack.setAttacker(attacker);
                for (Attack attack1 : prev) {
                    if (attack.equals(attack1)) {
                        found = true;
                        if (attack.equals2(attack1)) {
                            logger.info("Attack already exists and is unchanged. Skipping.");
                            break;
                        } else {
                            logger.info("Attack exists but differs. Updating...");
                            attacksDao.updateAttack(attack);
                        }
                    }
                }

                if (!found) {
                    logger.info("New attack found. Inserting into database...");
                    boolean b = false;
                    if (victimsDao.getVictimByName(attack.getVictim().getVictimName()) == null) {
                        logger.info("Victim not found. Adding new victim: " + attack.getVictim().getVictimName());
                        if(victimsDao.addNewVictim(attack.getVictim()).equals("Added Victim"))
                        {
                            b = true;
                        }
                    }
                    if(b)
                    {
                        attacksDao.addNewAttack(attack);
                        logger.info("Attack added successfully.");

                        if (attack.getDownloadUrls() != null) {
                            for (DownloadUrl x : attack.getDownloadUrls()) {
                                x.setAttackId(attack.getAttackId());
                                downloadUrlsDao.addDownloadUrl(x);
                            }
                            logger.info("Added " + attack.getDownloadUrls().size() + " download URLs.");
                        }

                        if (attack.getImages() != null) {
                            for (Image x : attack.getImages()) {
                                x.setAttackId(attack.getAttackId());
                                imagesDao.addImage(x);
                            }
                            logger.info("Added " + attack.getImages().size() + " images.");
                        }
                    }
                }
            }
        } else {
            logger.warning("No attacks provided to add.");
        }

        logger.info("Exiting addNewAttacks method");
    }

    @Override
    public List<Attack> getAllAtacks() throws SQLException {
        logger.info("Entering getAllAtacks method");
        return attacksDao.getAllAttacks();
    }
}
