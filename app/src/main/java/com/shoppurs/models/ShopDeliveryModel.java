package com.shoppurs.models;

import java.io.Serializable;

public class ShopDeliveryModel implements Serializable {
    private double retLat;
    private double retLong;
    private String isDeliveryAvailable;
    private double minDeliveryAmount; //charge per km
    private String minDeliverytime;
    private int minDeliverydistance;
    private double chargesAfterMinDistance;
    private String shopCode;
    private double netDeliveryCharge;

    public double getNetDeliveryCharge() {
        return netDeliveryCharge;
    }

    public void setNetDeliveryCharge(double netDeliveryCharge) {
        this.netDeliveryCharge = netDeliveryCharge;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public double getRetLat() {
        return retLat;
    }

    public void setRetLat(double retLat) {
        this.retLat = retLat;
    }

    public double getRetLong() {
        return retLong;
    }

    public void setRetLong(double retLong) {
        this.retLong = retLong;
    }

    public String getIsDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setIsDeliveryAvailable(String isDeliveryAvailable) {
        this.isDeliveryAvailable = isDeliveryAvailable;
    }

    public double getMinDeliveryAmount() {
        return minDeliveryAmount;
    }

    public void setMinDeliveryAmount(double minDeliveryAmount) {
        this.minDeliveryAmount = minDeliveryAmount;
    }

    public String getMinDeliverytime() {
        return minDeliverytime;
    }

    public void setMinDeliverytime(String minDeliverytime) {
        this.minDeliverytime = minDeliverytime;
    }

    public int getMinDeliverydistance() {
        return minDeliverydistance;
    }

    public void setMinDeliverydistance(int minDeliverydistance) {
        this.minDeliverydistance = minDeliverydistance;
    }

    public double getChargesAfterMinDistance() {
        return chargesAfterMinDistance;
    }

    public void setChargesAfterMinDistance(double chargesAfterMinDistance) {
        this.chargesAfterMinDistance = chargesAfterMinDistance;
    }
}
