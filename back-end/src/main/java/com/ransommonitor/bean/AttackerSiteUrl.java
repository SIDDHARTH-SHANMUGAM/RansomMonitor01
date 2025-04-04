package com.ransommonitor.bean;

public class AttackerSiteUrl {
    private int urlId;
    private int attackerId;
    private String URL;
    private boolean status;
    private boolean monitorStatus;
    private String lastScrap;


    public AttackerSiteUrl() {}

    public AttackerSiteUrl(int urlId, int attackerId, String URL, boolean status, boolean monitorStatus, String lastScrap) {
        this.urlId = urlId;
        this.attackerId = attackerId;
        this.URL = URL;
        this.status = status;
        this.monitorStatus = monitorStatus;
        this.lastScrap = lastScrap;
    }
    public AttackerSiteUrl(int attackerId, String URL, boolean status, boolean monitorStatus, String lastScrap) {
        this.urlId = urlId;
        this.attackerId = attackerId;
        this.URL = URL;
        this.status = status;
        this.monitorStatus = monitorStatus;
        this.lastScrap = lastScrap;
    }

    public int getUrlId() {
        return urlId;
    }

    public void setUrlId(int urlId) {
        this.urlId = urlId;
    }

    public int getAttackerId() {
        return attackerId;
    }

    public void setAttackerId(int attackerId) {
        this.attackerId = attackerId;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isMonitorStatus() {
        return monitorStatus;
    }

    public void setMonitorStatus(boolean monitorStatus) {
        this.monitorStatus = monitorStatus;
    }

    public String isLastScrap() {
        return lastScrap;
    }

    public void setLastScrap(String lastScrap) {
        this.lastScrap = lastScrap;
    }
}
