package com.shoppurscustomer.models;

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class CartItem {

    private String barcode;
    private String prodCode;
    private String ItemName;
    private float Size;
    private String color;
    private float Price;
    private String Status;
    private int Quantity;
    private String custCode;
    private String custName;
    private String shopCode;
    private String shopName;
    private float totalAmout;
    private String createdDate;
    private String updatedDate;
    private float prodCgst;
    private float prodIgst;
    private float prodSgst;

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getCustCode() {
        return custCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public float getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(float totalAmout) {
        this.totalAmout = totalAmout;
    }

    public String getCreatedDate() {
        return createdDate;
    }


    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }


    public void setBarcode(String ItemId){
        this.barcode =ItemId;
    }
    public String getBarcode(){
        return barcode;
    }
    public void setItemName(String ItemName){
        this.ItemName=ItemName;
    }
    public String getItemName(){
        return ItemName;
    }
    public void setSize(float Size){
        this.Size=Size;
    }
    public float getSize(){
        return Size;
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
