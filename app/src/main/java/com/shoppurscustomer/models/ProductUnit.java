package com.shoppurscustomer.models;

import java.io.Serializable;

public class ProductUnit implements Serializable {
	
	private int id;
	private float sp,mrp;
	private String unitName;
	private String unitValue,status;

	public float getSp() {
		return sp;
	}

	public void setSp(float sp) {
		this.sp = sp;
	}

	public float getMrp() {
		return mrp;
	}

	public void setMrp(float mrp) {
		this.mrp = mrp;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getUnitValue() {
		return unitValue;
	}
	public void setUnitValue(String unitValue) {
		this.unitValue = unitValue;
	}
	
	

}
