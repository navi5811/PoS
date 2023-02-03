package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.model.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.StringUtil;

//todo dto to service call is done via flow service
//todo @transactional removal from dto layer compulsary to apply on dao layer
//todo
@Service
public class BrandDto {

	@Autowired
	private BrandService brandservice;
	@Autowired
	private InfoData info;

	// adding a brand
	@Transactional(rollbackOn = ApiException.class)
	public void addBrand(BrandForm f) throws ApiException {
		BrandPojo p = convert(f);
		normalizeBrand(p);
		validation(p);
		brandservice.addBrand(p);
	}

	// delete a brand only for backend purpose
	@Transactional
	public void deleteBrand(int id) {
		brandservice.deleteBrand(id);
	}

	// gets brand by id
	@Transactional(rollbackOn = ApiException.class)
	public BrandData getBrand(int id) throws ApiException {
		BrandPojo bp = brandservice.getBrand(id);

		if (bp == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		BrandData bd = convert(bp);
		return bd;
	}

	// get list of all brands
	@Transactional
	public List<BrandData> getAllBrand() {

		List<BrandPojo> list = brandservice.getAllBrand();
		List<BrandData> list2 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	// update a brand using id
	@Transactional(rollbackOn = ApiException.class)
	public void updateBrand(int id, BrandForm f) throws ApiException {
		BrandPojo p = convert(f);

		normalizeBrand(p);
		validation(p);
		BrandPojo ex = brandservice.getBrand(id);

		ex.setBrandName(p.getBrandName());
		ex.setBrandCategory(p.getBrandCategory());

		brandservice.updateBrand(id, ex);
	}

	// finding a brand using brandname and category
	@Transactional
	public BrandData findBrand(String brandName, String brandCategory) throws ApiException {
		BrandPojo p = brandservice.findBrand(brandName, brandCategory);
		if (p != null) {
			return convert(p);
		} else {
			throw new ApiException(
					"The given Brand: " + brandName + " Category: " + brandCategory + " Pair does not exist");
		}
	}

	// noramlization of brand
	public void normalizeBrand(BrandPojo p) {
		p.setBrandName(StringUtil.toLowerCase(p.getBrandName()));
		p.setBrandCategory(StringUtil.toLowerCase(p.getBrandCategory()));
	}

	// validation of brand
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

	// conversion from pojo to data
	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrandCategory(p.getBrandCategory());
		d.setBrandName(p.getBrandName());
		d.setBrandId(p.getBrandId());
		return d;
	}

	// conversion from form to pojo
	private static BrandPojo convert(BrandForm f) {
		BrandPojo p = new BrandPojo();
		p.setBrandName(f.getBrandName());
		p.setBrandCategory(f.getBrandCategory());
		return p;
	}
}
