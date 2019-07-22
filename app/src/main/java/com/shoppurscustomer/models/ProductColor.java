package com.shoppurscustomer.models;

import java.io.Serializable;

public class ProductColor implements Serializable {
	
	private int id,sizeId;
	private String size,colorName,colorValue,status;
	
	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSizeId() {
		return sizeId;
	}
	public void setSizeId(int sizeId) {
		this.sizeId = sizeId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getColorName() {
		return colorName;
	}
	public void setColorName(String colorName) {
		this.colorName = colorName;
	}
	public String getColorValue() {
		return colorValue;
	}
	public void setColorValue(String colorValue) {
		this.colorValue = colorValue;
	}
	
	

}
