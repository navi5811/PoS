package com.increff.employee.model;

public class InventoryData {

	private String inventoryProductBarcode;
	private int productId;
	
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	private Integer productQuantity;
	
	public String getInventoryProductBarcode() {
		return inventoryProductBarcode;
	}
	public void setInventoryProductBarcode(String inventoryProductBarcode) {
		this.inventoryProductBarcode = inventoryProductBarcode;
	}
	public Integer getProductQuantity() {
		return productQuantity;
	}
	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}
}
