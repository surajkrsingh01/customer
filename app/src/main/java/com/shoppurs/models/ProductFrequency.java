package com.shoppurs.models;

public class ProductFrequency {

    private String name, value, noOfDays,qyantity, startDate, endDate ;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public String getNoOfDays() {
        return noOfDays;
    }

    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }

    public String getQyantity() {
        return qyantity;
    }

    public void setQyantity(String qyantity) {
        this.qyantity = qyantity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
