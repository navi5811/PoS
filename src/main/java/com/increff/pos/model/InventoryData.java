package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryData extends InventoryForm {

	private Integer productId;
	private String brandName;
	private String brandCategory;
	private String productName;
	private Double mrp;
}
