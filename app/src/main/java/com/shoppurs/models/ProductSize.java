package com.shoppurs.models;

import java.io.Serializable;
import java.util.List;

public class ProductSize implements Serializable {

	private int id;
	private String size,status;
	
	private List<ProductColor> productColorList;
	
	

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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<ProductColor> getProductColorList() {
		return productColorList;
	}

	public void setProductColorList(List<ProductColor> productColorList) {
		this.productColorList = productColorList;
	}
	
	
}
