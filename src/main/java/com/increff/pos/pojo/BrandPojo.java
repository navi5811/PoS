package com.increff.pos.pojo;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class BrandPojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int brandId;
	private String brandName;
	private String brandCategory;

}
