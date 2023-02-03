package com.increff.pos.model;

public class ReportInventoryData {

	public Integer reportInventoryQuantity;
	public String brandName;
	public String brandCategory;

	public Integer getReportInventoryQuantity() {
		return reportInventoryQuantity;
	}

	public void setReportInventoryQuantity(Integer reportInventoryQuantity) {
		this.reportInventoryQuantity = reportInventoryQuantity;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandCategory() {
		return brandCategory;
	}

	public void setBrandCategory(String brandCategory) {
		this.brandCategory = brandCategory;
	}

}
