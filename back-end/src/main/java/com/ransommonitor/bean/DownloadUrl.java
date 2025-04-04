package com.ransommonitor.bean;

public class DownloadUrl {
    private int downloadUrlId;
    private int attackId;
    private String downloadUrl;
    private String createdAt;

    public DownloadUrl(int downloadUrlId, int attackId, String downloadUrl, String createdAt) {
        this.downloadUrlId = downloadUrlId;
        this.attackId = attackId;
        this.downloadUrl = downloadUrl;
        this.createdAt = createdAt;
    }

    public DownloadUrl() {
    }

    public DownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }


    public int getDownloadUrlId() {
        return downloadUrlId;
    }

    public void setDownloadUrlId(int downloadUrlId) {
        this.downloadUrlId = downloadUrlId;
    }

    public int getAttackId() {
        return attackId;
    }

    public void setAttackId(int attackId) {
        this.attackId = attackId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
