package com.increff.pos.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderItemPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int orderItemId;
	private int orderId;
	private int orderProductId;
	private int orderQuantity;
	private Double orderSellingPrice;
	private Double mrp;
	
	public Double getMrp() {
		return mrp;
	}

	public void setMrp(Double mrp) {
		this.mrp = mrp;
	}

	public int getOrderItemId() {
		return orderItemId;
	}
	
	public void setOrderItemId(int orderItemId) {
		this.orderItemId = orderItemId;
	}
	
	public int getOrderId() {
		return orderId;
	}
	
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public int getOrderProductId() {
		return orderProductId;
	}
	
	public void setOrderProductId(int orderProductId) {
		this.orderProductId = orderProductId;
	}
	
	public int getOrderQuantity() {
		return orderQuantity;
	}
	
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	
	public Double getOrderSellingPrice() {
		return orderSellingPrice;
	}
	
	public void setOrderSellingPrice(Double orderSellingPrice) {
		this.orderSellingPrice = orderSellingPrice;
	}
}
