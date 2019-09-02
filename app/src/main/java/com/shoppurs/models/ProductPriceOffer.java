package com.shoppurs.models;

import java.io.Serializable;
import java.util.List;

public class ProductPriceOffer implements Serializable {

	private int id,prodId;
	private String offerName,status,startDate,endDate,userName,dbName,dbPassword,dbUserName;

	public List<ProductPriceDetails> getProductPriceDetails() {
		return productPriceDetails;
	}

	public void setProductPriceDetails(List<ProductPriceDetails> productPriceDetails) {
		this.productPriceDetails = productPriceDetails;
	}

	private List<ProductPriceDetails> productPriceDetails;
	
	

	
	
	public int getProdId() {
		return prodId;
	}
	public void setProdId(int prodId) {
		this.prodId = prodId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOfferName() {
		return offerName;
	}
	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	
	
	
}
