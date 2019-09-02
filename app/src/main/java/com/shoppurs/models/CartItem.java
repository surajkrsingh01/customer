package com.shoppurs.models;

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class CartItem {

    private String prodId, prodCode, color, barcode, prodName,  couponId, couponName, couponPercentage, couponMaxAmount, offerId,
    offerType, deleveryAddress, deleiveryCountry, state, city, pincode, deliveryCharges, longitude, latitude,
    comboProductIds, productSubcatId, productcatId, productName, productCode, productBarCode, productDescription, produtHsnCode, unit, isBarcodeAvailable, image1, image2, image3;
    private int offerItemCounter, freeProductPosition, Quantity;
    private float Price, productMrp, productSp, Size, totalAmout;
    private String custCode, custName, shopCode, shopName;
    private String createdDate;
    private String updatedDate;
    private float prodCgst, prodIgst, prodSgst;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getCouponPercentage() {
        return couponPercentage;
    }

    public void setCouponPercentage(String couponPercentage) {
        this.couponPercentage = couponPercentage;
    }

    public String getCouponMaxAmount() {
        return couponMaxAmount;
    }

    public void setCouponMaxAmount(String couponMaxAmount) {
        this.couponMaxAmount = couponMaxAmount;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public String getDeleveryAddress() {
        return deleveryAddress;
    }

    public void setDeleveryAddress(String deleveryAddress) {
        this.deleveryAddress = deleveryAddress;
    }

    public String getDeleiveryCountry() {
        return deleiveryCountry;
    }

    public void setDeleiveryCountry(String deleiveryCountry) {
        this.deleiveryCountry = deleiveryCountry;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getComboProductIds() {
        return comboProductIds;
    }

    public void setComboProductIds(String comboProductIds) {
        this.comboProductIds = comboProductIds;
    }

    public String getProductSubcatId() {
        return productSubcatId;
    }

    public void setProductSubcatId(String productSubcatId) {
        this.productSubcatId = productSubcatId;
    }

    public String getProductcatId() {
        return productcatId;
    }

    public void setProductcatId(String productcatId) {
        this.productcatId = productcatId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductBarCode() {
        return productBarCode;
    }

    public void setProductBarCode(String productBarCode) {
        this.productBarCode = productBarCode;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProdutHsnCode() {
        return produtHsnCode;
    }

    public void setProdutHsnCode(String produtHsnCode) {
        this.produtHsnCode = produtHsnCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIsBarcodeAvailable() {
        return isBarcodeAvailable;
    }

    public void setIsBarcodeAvailable(String isBarcodeAvailable) {
        this.isBarcodeAvailable = isBarcodeAvailable;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public int getOfferItemCounter() {
        return offerItemCounter;
    }

    public void setOfferItemCounter(int offerItemCounter) {
        this.offerItemCounter = offerItemCounter;
    }

    public int getFreeProductPosition() {
        return freeProductPosition;
    }

    public void setFreeProductPosition(int freeProductPosition) {
        this.freeProductPosition = freeProductPosition;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public float getProductMrp() {
        return productMrp;
    }

    public void setProductMrp(float productMrp) {
        this.productMrp = productMrp;
    }

    public float getProductSp() {
        return productSp;
    }

    public void setProductSp(float productSp) {
        this.productSp = productSp;
    }

    public float getSize() {
        return Size;
    }

    public void setSize(float size) {
        Size = size;
    }

    public float getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(float totalAmout) {
        this.totalAmout = totalAmout;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public float getProdCgst() {
        return prodCgst;
    }

    public void setProdCgst(float prodCgst) {
        this.prodCgst = prodCgst;
    }

    public float getProdIgst() {
        return prodIgst;
    }

    public void setProdIgst(float prodIgst) {
        this.prodIgst = prodIgst;
    }

    public float getProdSgst() {
        return prodSgst;
    }

    public void setProdSgst(float prodSgst) {
        this.prodSgst = prodSgst;
    }
}
