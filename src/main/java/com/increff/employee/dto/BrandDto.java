package com.increff.employee.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import com.increff.employee.util.StringUtil;

@Service
public class BrandDto {

	@Autowired
	private BrandService brandservice;

	@Transactional(rollbackOn = ApiException.class)
	public void addBrand(BrandForm f) throws ApiException {
		BrandPojo p = convert(f);
		normalizeBrand(p);

		validation(p);

		brandservice.addBrand(p);
	}

	@Transactional
	public void deleteBrand(int id) {
		brandservice.deleteBrand(id);
	}	

	@Transactional(rollbackOn = ApiException.class)
	public BrandData getBrand(int id) throws ApiException {
		BrandPojo bp = brandservice.findBrand(id);

		BrandData bd = convert(bp);
		return bd;
	}

	@Transactional
	public List<BrandData> getAllBrand() {

		List<BrandPojo> list = brandservice.getAllBrand();
		List<BrandData> list2 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateBrand(int id, BrandForm f) throws ApiException {
		BrandPojo p = convert(f);
		
		normalizeBrand(p);
		validation(p);
		BrandPojo ex = brandservice.findBrand(id);

		ex.setBrandName(p.getBrandName());
		ex.setBrandCategory(p.getBrandCategory());

		brandservice.updateBrand(id,ex);
	}

	@Transactional
	public BrandPojo findBrand(String brandName, String brandCategory) throws ApiException {
		BrandPojo p = brandservice.findBrand(brandName, brandCategory);
		if (p != null) {
			return p;
		} else {
			throw new ApiException(
					"The given Brand: " + brandName + " Category: " + brandCategory + " Pair does not exist");
		}
	}

	@Transactional
	public BrandPojo findBrand(int id) throws ApiException {
		BrandPojo p = brandservice.findBrand(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return p;
	}

	public void normalizeBrand(BrandPojo p) {
		p.setBrandName(StringUtil.toLowerCase(p.getBrandName()));
		p.setBrandCategory(StringUtil.toLowerCase(p.getBrandCategory()));
	}

	public void validation(BrandPojo p) throws ApiException {
		if (StringUtil.isEmpty(p.getBrandName())) {
			throw new ApiException("Brand Name cannot be empty");
		}

		if (StringUtil.isEmpty(p.getBrandCategory())) {
			throw new ApiException("Brand Category cannot be empty");
		}

		// Check if Brand exists already
		if (brandservice.findBrand(p.getBrandName(), p.getBrandCategory()) != null) {
			throw new ApiException("Given Brand-Category combination already exists");
		}
	}

	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrandCategory(p.getBrandCategory());
		d.setBrandName(p.getBrandName());
		d.setBrandId(p.getBrandId());
		return d;
	}

	private static BrandPojo convert(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setBrandName(f.getBrandName());
		p.setBrandCategory(f.getBrandCategory());
		return p;
	}
}
