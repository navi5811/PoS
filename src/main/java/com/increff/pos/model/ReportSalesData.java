package com.increff.pos.model;

import io.swagger.models.auth.In;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportSalesData {
	private String brand;
	private String category;
	private Integer quantity;
	private Double totalAmount;
	public ReportSalesData() {

	}
}
