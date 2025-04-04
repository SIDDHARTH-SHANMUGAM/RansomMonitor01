package com.ransommonitor.bean;

public class Victim {
    private int victimId;
    private String victimName;
    private String country;
    private String description;
    private String victimURL;
    private double revenue;
    private String createdAt;
    private String updatedAt;

    public Victim(int victimId, String victimName, String country, String description, String victimURL, double revenue, String createdAt, String updatedAt) {
        this.victimId = victimId;
        this.victimName = victimName;
        this.country = country;
        this.description = description;
        this.victimURL = victimURL;
        this.revenue = revenue;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Victim() {
    }

    public int getVictimId() {
        return victimId;
    }

    public void setVictimId(int victimId) {
        this.victimId = victimId;
    }

    public String getVictimName() {
        return victimName;
    }

    public void setVictimName(String victimName) {
        this.victimName = victimName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVictimURL() {
        return victimURL;
    }

    public void setVictimURL(String victimURL) {
        this.victimURL = victimURL;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
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

    @Override
    public String toString() {
        return
                "\n victimId=" + victimId +
                "\n victimName=" + victimName +
                "\n country=" + country +
                "\n description=" + description +
                "\n victimURL=" + victimURL +
                "\n revenue=" + revenue +
                "\n createdAt=" + createdAt +
                "\n updatedAt=" + updatedAt +
                "\n";
    }
}
