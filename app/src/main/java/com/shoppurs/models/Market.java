package com.shoppurs.models;

public class Market {
    private String id, name,image;
    private int localImage;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLocalImage() {
        return localImage;
    }

    public void setLocalImage(int localImage) {
        this.localImage = localImage;
    }
}
