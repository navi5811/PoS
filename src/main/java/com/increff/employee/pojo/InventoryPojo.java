package com.increff.employee.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class InventoryPojo {

	@Id
	private int productId;
	private Integer productQuantity;
	
//	public InventoryPojo(int productId, Integer productQuantity) {
//		super();
//		this.productId = productId;
//		this.productQuantity = productQuantity;
//	}
//	
//	public InventoryPojo() {
//		
//	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	public int getProductId() {
		return productId;
	}
	
	public void setProductId(int productId) {
		this.productId = productId;
	}
}
