package com.ransommonitor.bean;

public class Image {
    private int imageId;
    private int attackId;
    private String image;
    private String createdAt;

    public Image(int imageId, int attackId, String image, String createdAt) {
        this.imageId = imageId;
        this.attackId = attackId;
        this.image = image;
        this.createdAt = createdAt;
    }

    public Image() {
    }

    public Image(String img) {
        this.image = img;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getAttackId() {
        return attackId;
    }

    public void setAttackId(int attackId) {
        this.attackId = attackId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return image;
    }
}
