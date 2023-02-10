package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemData extends OrderItemForm {

	private Integer id;
	private Integer orderItemId;
	private String name;
	private Double mrp;
	private int availableQuantity;

}
