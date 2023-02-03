package com.increff.pos.model;

public class ProductForm {

	private String productBrandName;
	private String productBrandCategoryName;
	private String productBarcode;
	private String productName;
	private Double productMrp;
	
	public String getProductBrandName() {
		return productBrandName;
	}
	
	public void setProductBrandName(String productBrandName) {
		this.productBrandName = productBrandName;
	}
	
	public String getProductBrandCategoryName() {
		return productBrandCategoryName;
	}
	
	public void setProductBrandCategoryName(String s) {
		this.productBrandCategoryName = s;
	}
	
	public String getProductBarcode() {
		return productBarcode;
	}
	
	public void setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
	}
	
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public Double getProductMrp() {
		return productMrp;
	}
	
	public void setProductMrp(Double productMrp) {
		this.productMrp = productMrp;
	}
}
