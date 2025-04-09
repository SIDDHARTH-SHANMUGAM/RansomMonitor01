package com.ransommonitor.bean;

public class AttackerSiteUrl {
    private int urlId;
    private int attackerId;
    private String URL;
    private boolean status;
    private boolean monitorStatus;
    private boolean isScraped;
    private String lastScrap;


    public AttackerSiteUrl() {}

    public AttackerSiteUrl(int urlId, int attackerId, String URL, boolean status, boolean monitorStatus, String lastScrap, boolean isScrapped) {
        this.urlId = urlId;
        this.attackerId = attackerId;
        this.URL = URL;
        this.status = status;
        this.monitorStatus = monitorStatus;
        this.lastScrap = lastScrap;
        this.isScraped = isScrapped;
    }
    public AttackerSiteUrl(int attackerId, String URL, boolean status, boolean monitorStatus, String lastScrap, boolean isScrapped) {
        this.attackerId = attackerId;
        this.URL = URL;
        this.status = status;
        this.monitorStatus = monitorStatus;
        this.lastScrap = lastScrap;
        this.isScraped = isScrapped;
    }

    public AttackerSiteUrl( int attackerId, String URL) {
        this.urlId = 0;
        this.attackerId = attackerId;
        this.URL = URL;
        this.status = false;
        this.monitorStatus = true;
        this.lastScrap = "";
        this.isScraped = false;
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

    public boolean getActiveStatus() {
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

    public boolean isScraped() {
        return isScraped;
    }

    public void setScraped(boolean scrapped) {
        isScraped = scrapped;
    }

    public String getLastScrap() {
        return lastScrap;
    }

    @Override
    public String toString() {
        return "AttackerSiteUrl{" +
                "urlId=" + urlId +
                ", attackerId=" + attackerId +
                ", URL='" + URL + '\'' +
                ", status=" + status +
                ", monitorStatus=" + monitorStatus +
                ", isScrapped=" + isScraped +
                ", lastScrap='" + lastScrap + '\'' +
                '}';
    }
}
