package com.shoppurscustomer.models;

import java.io.Serializable;

public class MyProduct implements Serializable {
    private String id,name,code,barCode,prodHsnCode, prodMfgDate, prodExpiryDate, prodMfgBy, offerId,
            prodCgst, prodIgst, prodSgst, prodWarranty, desc,mrp,sellingPrice,image,subCatName,
            prodImage1, prodImage2, prodImage3;
    private int localImage, quantity;
    private float discount;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getProdHsnCode() {
        return prodHsnCode;
    }

    public void setProdHsnCode(String prodHsnCode) {
        this.prodHsnCode = prodHsnCode;
    }

    public String getProdMfgDate() {
        return prodMfgDate;
    }

    public void setProdMfgDate(String prodMfgDate) {
        this.prodMfgDate = prodMfgDate;
    }

    public String getProdExpiryDate() {
        return prodExpiryDate;
    }

    public void setProdExpiryDate(String prodExpiryDate) {
        this.prodExpiryDate = prodExpiryDate;
    }

    public String getProdMfgBy() {
        return prodMfgBy;
    }

    public void setProdMfgBy(String prodMfgBy) {
        this.prodMfgBy = prodMfgBy;
    }

    public String getProdImage1() {
        return prodImage1;
    }

    public void setProdImage1(String prodImage1) {
        this.prodImage1 = prodImage1;
    }

    public String getProdImage2() {
        return prodImage2;
    }

    public void setProdImage2(String prodImage2) {
        this.prodImage2 = prodImage2;
    }

    public String getProdImage3() {
        return prodImage3;
    }

    public void setProdImage3(String prodImage3) {
        this.prodImage3 = prodImage3;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getProdCgst() {
        return prodCgst;
    }

    public void setProdCgst(String prodCgst) {
        this.prodCgst = prodCgst;
    }

    public String getProdIgst() {
        return prodIgst;
    }

    public void setProdIgst(String prodIgst) {
        this.prodIgst = prodIgst;
    }

    public String getProdSgst() {
        return prodSgst;
    }

    public void setProdSgst(String prodSgst) {
        this.prodSgst = prodSgst;
    }

    public String getProdWarranty() {
        return prodWarranty;
    }

    public void setProdWarranty(String prodWarranty) {
        this.prodWarranty = prodWarranty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }
}
