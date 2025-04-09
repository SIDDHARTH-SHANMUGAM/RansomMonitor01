package com.ransommonitor.bean;

public class Attacker {
    private int attackerId;
    private String attackerName;
    private String email;
    private String toxId;
    private String sessionId;
    private String description;
    private String firstAttackAt;
    private boolean isRAAS;
    private boolean monitorStatus;
    private String createdAt;
    private String updatedAt;

    public Attacker(int attackerId, String attackerName, String email, String toxId, String sessionId, String description, String firstAttackAt, boolean isRAAS, boolean monitorStatus, String createdAt, String updatedAt) {
        this.attackerId = attackerId;
        this.attackerName = attackerName;
        this.email = email;
        this.toxId = toxId;
        this.sessionId = sessionId;
        this.description = description;
        this.firstAttackAt = firstAttackAt;
        this.isRAAS = isRAAS;
        this.monitorStatus = monitorStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Attacker() {
        this.attackerId = 0;
        this.attackerName = "";
        this.email = "";
        this.toxId = "";
        this.sessionId = "";
        this.description = "";
        this.firstAttackAt = "";
        this.isRAAS = false;
        this.monitorStatus = true;
    }

    public int getAttackerId() {
        return attackerId;
    }

    public void setAttackerId(int attackerId) {
        this.attackerId = attackerId;
    }

    public String getAttackerName() {
        return attackerName;
    }

    public void setAttackerName(String attackerName) {
        this.attackerName = attackerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToxId() {
        return toxId;
    }

    public void setToxId(String toxId) {
        this.toxId = toxId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstAttackAt() {
        return firstAttackAt;
    }

    public void setFirstAttackAt(String firstAttackAt) {
        this.firstAttackAt = firstAttackAt;
    }

    public boolean isRAAS() {
        return isRAAS;
    }

    public void setRAAS(boolean RAAS) {
        isRAAS = RAAS;
    }

    public boolean getMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(boolean monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public boolean isMonitorStatus() {
        return monitorStatus;
    }


    @Override
    public String toString() {
        return "" +
                "\n attackerId=" + attackerId +
                "\n attackerName=" + attackerName +
                "\n email=" + email +
                "\n toxId=" + toxId +
                "\n sessionId=" + sessionId +
                "\n description=" + description +
                "\n firstAttackAt=" + firstAttackAt +
                "\n isRAAS=" + isRAAS +
                "\n monitorStatus=" + monitorStatus +
                "\n createdAt=" + createdAt +
                "\n updatedAt=" + updatedAt +
                "\n";
    }
}
