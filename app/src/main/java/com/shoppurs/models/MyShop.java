package com.shoppurs.models;

import java.io.Serializable;

/**
 * Created by suraj kumar singh on 09-03-2019.
 */

public class MyShop implements Serializable {
    private String id, merchantId,accessCode, name,code,desc,subCatName,address,mobile, state, city,
            shopimage, dbname, dbusername, dbpassword, khataNumber, khataOpenDate;
    private int image, type;
    private double latitude, longitude;
    private String isDeliveryAvailable;
    private double minDeliveryAmount;
    private String minDeliverytime;
    private int minDeliverydistance;
    private double chargesAfterMinDistance;

    public String getKhataOpenDate() {
        return khataOpenDate;
    }

    public void setKhataOpenDate(String khataOpenDate) {
        this.khataOpenDate = khataOpenDate;
    }

    public String getKhataNumber() {
        return khataNumber;
    }

    public void setKhataNumber(String khataNumber) {
        this.khataNumber = khataNumber;
    }

    public String getIsDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setIsDeliveryAvailable(String isDeliveryAvailable) {
        this.isDeliveryAvailable = isDeliveryAvailable;
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

    public String isDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setDeliveryAvailable(String deliveryAvailable) {
        isDeliveryAvailable = deliveryAvailable;
    }

    public double getMinDeliveryAmount() {
        return minDeliveryAmount;
    }

    public void setMinDeliveryAmount(double minDeliveryAmount) {
        this.minDeliveryAmount = minDeliveryAmount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDbusername() {
        return dbusername;
    }

    public void setDbusername(String dbusername) {
        this.dbusername = dbusername;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }

    public String getShopimage() {
        return shopimage;
    }

    public void setShopimage(String shopimage) {
        this.shopimage = shopimage;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
