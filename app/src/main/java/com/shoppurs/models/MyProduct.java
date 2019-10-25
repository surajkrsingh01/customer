package com.shoppurs.models;

import java.io.Serializable;
import java.util.List;

public class MyProduct implements Serializable {
    private String id, shopCode,catId, subCatId,name,code, unit, isBarcodeAvailable, comboProductIds, barCode,prodHsnCode, prodMfgDate, prodExpiryDate, prodMfgBy, offerId, offerType,
            prodWarranty, desc,image,subCatName, prodImage1, prodImage2, prodImage3, size, color, custCode,
            invNo, transId, srId;
    private int offerItemCounter, freeProductPosition, localImage, quantity, qoh, returnStatus;
    private float discount, mrp, sellingPrice, prodCgst, prodIgst, prodSgst, totalAmount ;
    private ProductDiscountOffer productDiscountOffer;
    private ProductPriceOffer productPriceOffer;
    private ProductComboOffer productComboOffer;
    private List<ProductUnit> productUnitList;
    private List<ProductSize> productSizeList;
    private List<Barcode> barcodeList;
    private Object productOffer;
    private ProductFrequency frequency;

    public String getSrId() {
        return srId;
    }

    public void setSrId(String srId) {
        this.srId = srId;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(int returnStatus) {
        this.returnStatus = returnStatus;
    }

    public ProductFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(ProductFrequency frequency) {
        this.frequency = frequency;
    }

    public Object getProductOffer() {
        return productOffer;
    }

    public void setProductOffer(Object productOffer) {
        this.productOffer = productOffer;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public int getQoh() {
        return qoh;
    }

    public void setQoh(int qoh) {
        this.qoh = qoh;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getIsBarcodeAvailable() {
        return isBarcodeAvailable;
    }

    public void setIsBarcodeAvailable(String isBarcodeAvailable) {
        this.isBarcodeAvailable = isBarcodeAvailable;
    }

    public String getComboProductIds() {
        return comboProductIds;
    }

    public void setComboProductIds(String comboProductIds) {
        this.comboProductIds = comboProductIds;
    }

    public ProductComboOffer getProductComboOffer() {
        return productComboOffer;
    }

    public void setProductComboOffer(ProductComboOffer productComboOffer) {
        this.productComboOffer = productComboOffer;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public List<ProductUnit> getProductUnitList() {
        return productUnitList;
    }

    public void setProductUnitList(List<ProductUnit> productUnitList) {
        this.productUnitList = productUnitList;
    }

    public List<ProductSize> getProductSizeList() {
        return productSizeList;
    }

    public void setProductSizeList(List<ProductSize> productSizeList) {
        this.productSizeList = productSizeList;
    }

    public ProductDiscountOffer getProductDiscountOffer() {
        return productDiscountOffer;
    }

    public void setProductDiscountOffer(ProductDiscountOffer productDiscountOffer) {
        this.productDiscountOffer = productDiscountOffer;
    }

    public ProductPriceOffer getProductPriceOffer() {
        return productPriceOffer;
    }

    public void setProductPriceOffer(ProductPriceOffer productPriceOffer) {
        this.productPriceOffer = productPriceOffer;
    }

    public List<Barcode> getBarcodeList() {
        return barcodeList;
    }

    public void setBarcodeList(List<Barcode> barcodeList) {
        this.barcodeList = barcodeList;
    }


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

    public float getMrp() {
        return mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
    }

    public float getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(float sellingPrice) {
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
