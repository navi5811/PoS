package com.increff.employee.pojo;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int orderId;
	private LocalDateTime orderTime;
	
	public int getOrderId() {
		return orderId;
	}
	
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	
	public LocalDateTime getOrderTime() {
		return orderTime;
	}
	
	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}
}
