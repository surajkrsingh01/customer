package com.shoppurs.models;

public class KhataTransaction {

    private int paymentId;
    private String coNo,paymentTransactionType,paymentTransactionId,paymentPaymentMethod,
            paymentPaymentBrand,paymentPaymentMode,paymentPaymentDate,paymentPaymentInvoiceNo,createdDate;
    private float paymentAmount;

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getCoNo() {
        return coNo;
    }

    public void setCoNo(String coNo) {
        this.coNo = coNo;
    }

    public String getPaymentTransactionType() {
        return paymentTransactionType;
    }

    public void setPaymentTransactionType(String paymentTransactionType) {
        this.paymentTransactionType = paymentTransactionType;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getPaymentPaymentMethod() {
        return paymentPaymentMethod;
    }

    public void setPaymentPaymentMethod(String paymentPaymentMethod) {
        this.paymentPaymentMethod = paymentPaymentMethod;
    }

    public String getPaymentPaymentBrand() {
        return paymentPaymentBrand;
    }

    public void setPaymentPaymentBrand(String paymentPaymentBrand) {
        this.paymentPaymentBrand = paymentPaymentBrand;
    }

    public String getPaymentPaymentMode() {
        return paymentPaymentMode;
    }

    public void setPaymentPaymentMode(String paymentPaymentMode) {
        this.paymentPaymentMode = paymentPaymentMode;
    }

    public String getPaymentPaymentDate() {
        return paymentPaymentDate;
    }

    public void setPaymentPaymentDate(String paymentPaymentDate) {
        this.paymentPaymentDate = paymentPaymentDate;
    }

    public String getPaymentPaymentInvoiceNo() {
        return paymentPaymentInvoiceNo;
    }

    public void setPaymentPaymentInvoiceNo(String paymentPaymentInvoiceNo) {
        this.paymentPaymentInvoiceNo = paymentPaymentInvoiceNo;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}
