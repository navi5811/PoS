package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.controller.ProductApiController;
import com.increff.employee.dao.BrandDao;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.util.StringUtil;

@Service
public class BrandService {
	
	private static Logger logger = LogManager.getLogger(BrandService.class);

	@Autowired
	private BrandDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addBrand(BrandPojo p) throws ApiException {
		normalizeBrand(p);

		if (StringUtil.isEmpty(p.getBrandName())) {
			throw new ApiException("Brand Name cannot be empty");
		}

		if (StringUtil.isEmpty(p.getBrandCategory())) {
			throw new ApiException("Brand Category cannot be empty");
		}

		// Check if Brand exists already
		if (dao.select(p.getBrandName(), p.getBrandCategory()) != null) {
			throw new ApiException("Given Brand-Category combination already exists");
		}

		dao.insert(p);
	}

	@Transactional
	public void deleteBrand(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo getBrand(int id) throws ApiException {
		return findBrand(id);
	}

	@Transactional
	public List<BrandPojo> getAllBrand() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateBrand(int id, BrandPojo p) throws ApiException {
		normalizeBrand(p);
		BrandPojo ex = findBrand(id);

		ex.setBrandName(p.getBrandName());
		ex.setBrandCategory(p.getBrandCategory());

		dao.update(ex);
	}
	
	@Transactional
	public BrandPojo findBrand(String brandName,String brandCategory) throws ApiException {
		logger.error("Control reached Brand fn " + brandName + " " + brandCategory);
		BrandPojo p = dao.select(brandName, brandCategory);
		
		if(p != null) {
			return p;
		}
		else {
			throw new ApiException("The given Brand: " + brandName + " Category: " + brandCategory + " Pair does not exist");
		}
	}

	@Transactional
	public BrandPojo findBrand(int id) throws ApiException {
		BrandPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return p;
	}

	protected static void normalizeBrand(BrandPojo p) {
		p.setBrandName(StringUtil.toLowerCase(p.getBrandName()));
		p.setBrandCategory(StringUtil.toLowerCase(p.getBrandCategory()));
	}
}
