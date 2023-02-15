package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryForm {

	private String inventoryProductBarcode;
	private Integer productQuantity;

	@Override
	public String toString() {
		return "InventoryForm{" +
				"inventoryProductBarcode='" + inventoryProductBarcode + '\'' +
				", productQuantity=" + productQuantity +
				'}';
	}
}
