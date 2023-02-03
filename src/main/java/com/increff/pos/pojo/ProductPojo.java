package com.increff.pos.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//todo @index annotation use

@Entity
public class ProductPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productId;
	private int productBrandCategory;
	private String productBarcode;
	private String productName;
	private Double productMrp;
	
	public int getProductId() {
		return productId;
	}
	
	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	public int getProductBrandCategory() {
		return productBrandCategory;
	}
	
	public void setProductBrandCategory(int productBrandCategory) {
		this.productBrandCategory = productBrandCategory;
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
