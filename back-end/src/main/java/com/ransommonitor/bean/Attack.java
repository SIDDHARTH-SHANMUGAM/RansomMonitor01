package com.ransommonitor.bean;

import java.util.ArrayList;
import java.util.List;

public class Attack {
    private int attackId;
    private Attacker attacker;
    private Victim victim;
    private String deadlines;
    private boolean isPublished;
    private boolean isForSale;
    private String postedAt;
    private int noOfVisits;
    private String dataSizes;
    private String description;
    private String lastVisitedAt;
    private String category;
    private boolean isNegotiated;
    private String ransomAmount;
    private String saleAmount;
    private String updatedAt;

    private List<DownloadUrl> downloadUrls;
    private List<Image> images;


    public Attack(int attackId, Attacker attacker, Victim victim, String deadlines,
                  boolean isPublished, boolean isForSale, String postedAt, int noOfVisits, String dataSizes,
                  String description, String lastVisitedAt, String category, boolean isNegotiated,
                  String ransomAmount, String saleAmount, String updatedAt, List<DownloadUrl> downloadUrls,
                  List<Image> images) {
        this.attackId = attackId;
        this.attacker = attacker;
        this.victim = victim;
        this.deadlines = deadlines;
        this.isPublished = isPublished;
        this.isForSale = isForSale;
        this.postedAt = postedAt;
        this.noOfVisits = noOfVisits;
        this.dataSizes = dataSizes;
        this.description = description;
        this.lastVisitedAt = lastVisitedAt;
        this.category = category;
        this.isNegotiated = isNegotiated;
        this.ransomAmount = ransomAmount;
        this.saleAmount = saleAmount;
        this.updatedAt = updatedAt;
        this.downloadUrls = downloadUrls;
        this.images = images;
    }

    public Attack(int attackId, String deadlines, String attackDescription,
                  boolean isPublished, boolean isForSale, String postedAt, int noOfVisits, String dataSizes,
                  String description, String lastVisitedAt, String category, boolean isNegotiated,
                  String ransomAmount, String saleAmount, String updatedAt) {
        this.attackId = attackId;
        this.deadlines = deadlines;
        this.isPublished = isPublished;
        this.isForSale = isForSale;
        this.postedAt = postedAt;
        this.noOfVisits = noOfVisits;
        this.dataSizes = dataSizes;
        this.description = description;
        this.lastVisitedAt = lastVisitedAt;
        this.category = category;
        this.isNegotiated = isNegotiated;
        this.ransomAmount = ransomAmount;
        this.saleAmount = saleAmount;
        this.updatedAt = updatedAt;
    }

    public Attack() {
        victim = new Victim();
        attacker = new Attacker();
        downloadUrls = new ArrayList<>();
        images = new ArrayList<>();
    }

    public int getAttackId() {
        return attackId;
    }

    public void setAttackId(int attackId) {
        this.attackId = attackId;
    }

    public Attacker getAttacker() {
        return attacker;
    }

    public void setAttacker(Attacker attacker) {
        this.attacker = attacker;
    }

    public Victim getVictim() {
        return victim;
    }

    public void setVictim(Victim victim) {
        this.victim = victim;
    }

    public String getDeadlines() {
        return deadlines;
    }

    public void setDeadlines(String deadlines) {
        this.deadlines = deadlines;
    }


    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public boolean isForSale() {
        return isForSale;
    }

    public void setForSale(boolean forSale) {
        isForSale = forSale;
    }

    public String getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(String postedAt) {
        this.postedAt = postedAt;
    }

    public int getNoOfVisits() {
        return noOfVisits;
    }

    public void setNoOfVisits(int noOfVisits) {
        this.noOfVisits = noOfVisits;
    }

    public String getDataSizes() {
        return dataSizes;
    }

    public void setDataSizes(String dataSizes) {
        this.dataSizes = dataSizes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastVisitedAt() {
        return lastVisitedAt;
    }

    public void setLastVisitedAt(String lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isNegotiated() {
        return isNegotiated;
    }

    public void setNegotiated(boolean negotiated) {
        isNegotiated = negotiated;
    }

    public String getRansomAmount() {
        return ransomAmount;
    }

    public void setRansomAmount(String ransomAmount) {
        this.ransomAmount = ransomAmount;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<DownloadUrl> getDownloadUrls() {
        return downloadUrls;
    }

    public void setDownloadUrls(List<DownloadUrl> downloadUrls) {
        this.downloadUrls = downloadUrls;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "#############################################################" +
                "\n attackId=" + attackId +
                victim +
                "\n deadlines=" + deadlines +
                "\n isPublished=" + isPublished +
                "\n isForSale=" + isForSale +
                "\n postedAt=" + postedAt +
                "\n noOfVisits=" + noOfVisits +
                "\n dataSizes=" + dataSizes +
                "\n description=" + description +
                "\n lastVisitedAt=" + lastVisitedAt +
                "\n category=" + category +
                "\n isNegotiated=" + isNegotiated +
                "\n ransomAmount=" + ransomAmount +
                "\n saleAmount=" + saleAmount +
                "\n updatedAt=" + updatedAt +
                "\n downloadUrls=" + downloadUrls +
                "\n images=" + images +
                "\n";
    }
}
