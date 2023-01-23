package com.increff.employee.model;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class ReportSalesForm {
//
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date startDate;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date endDate;

	private String brandName;

	private String category;
//	public Date getStartDate() {
//		return startDate;
//	}
//
//	public void setStartDate(Date startDate) {
//		this.startDate = startDate;
//	}
//
//	public Date getEndDate() {
//		return endDate;
//	}
//
//	public void setEndDate(Date endDate) {
//		this.endDate = endDate;
//	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	

}
