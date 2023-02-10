package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemForm {
	private String productBarcode;
	private Integer productQuantity;
	private Double productSellingPrice;

}
