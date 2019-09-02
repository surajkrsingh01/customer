package com.shoppurs.models;

/**
 * Created by suraj kumar singh on 05-04-2019.
 */

public class MyOrderDetail {
    private String shopCode, shopName, shopMobile, shopAddress, status;
    private MyProduct myProduct;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

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

    public MyProduct getMyProduct() {
        return myProduct;
    }

    public void setMyProduct(MyProduct myProduct) {
        this.myProduct = myProduct;
    }
}
