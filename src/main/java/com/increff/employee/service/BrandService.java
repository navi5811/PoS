package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.BrandDao;
import com.increff.employee.pojo.BrandPojo;
//todo we can add @transactional on the top of class
@Service
public class BrandService {

	@Autowired
	private BrandDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addBrand(BrandPojo p) throws ApiException {
		dao.insert(p);
	}

	@Transactional
	public void deleteBrand(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public BrandPojo getBrand(int id) throws ApiException {
		BrandPojo p = dao.select(id);
		return p;
	}

	@Transactional
	public List<BrandPojo> getAllBrand() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateBrand(int id, BrandPojo p) throws ApiException {
		BrandPojo ex = getBrand(id);
		ex.setBrandName(p.getBrandName());
		ex.setBrandCategory(p.getBrandCategory());
		dao.update(ex);
	}

	@Transactional
	public BrandPojo findBrand(String brandName, String brandCategory) throws ApiException {
		BrandPojo p = dao.select(brandName, brandCategory);
		return p;
	}
}
