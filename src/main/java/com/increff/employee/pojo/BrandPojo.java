package com.increff.employee.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BrandPojo {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int brandId;
	private String brandName;
	private String brandCategory;
	
	public int getBrandId() {
		return brandId;
	}
	public void setBrandId(int brandId) {
		this.brandId = brandId;
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
