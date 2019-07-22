package com.shoppurscustomer.models;

import java.io.Serializable;

public class Barcode implements Serializable {

    private String barcode;

    public Barcode() {

    }

    public Barcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
}
