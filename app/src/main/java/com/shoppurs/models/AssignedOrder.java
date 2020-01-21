package com.shoppurs.models;

import java.io.Serializable;

public class AssignedOrder  implements Serializable {

    private  String shopName, shopMobile, shopAddress, shopLat, shopLong;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopMobile() {
        return shopMobile;
    }

    public void setShopMobile(String shopMobile) {
        this.shopMobile = shopMobile;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopLat() {
        return shopLat;
    }

    public void setShopLat(String shopLat) {
        this.shopLat = shopLat;
    }

    public String getShopLong() {
        return shopLong;
    }

    public void setShopLong(String shopLong) {
        this.shopLong = shopLong;
    }
}
