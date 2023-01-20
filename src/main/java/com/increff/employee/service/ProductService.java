package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.pojo.ProductPojo;

@Service
public class ProductService {

	@Autowired
	private ProductDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addProduct(ProductPojo p) throws ApiException {
		dao.insert(p);
	}

	@Transactional
	public void deleteProduct(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public ProductPojo getProduct(int id) throws ApiException {
		return findProduct(id);
	}

	@Transactional
	public List<ProductPojo> getAllProduct() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn  = ApiException.class)
	public void updateProduct(int id, ProductPojo p) throws ApiException {
		
		ProductPojo ex = findProduct(id);
		
		ex.setProductName(p.getProductName());
		ex.setProductBrandCategory(p.getProductBrandCategory());
		ex.setProductBarcode(p.getProductBarcode());
		ex.setProductMrp(p.getProductMrp());
		
		dao.update(ex);
	}

	@Transactional
	public ProductPojo findProduct(int id) throws ApiException {
		ProductPojo p = dao.select(id);
		return p;
	}
	
	@Transactional
	public ProductPojo findProduct(String barcode) throws ApiException {
		ProductPojo p = dao.select(barcode);
		return p;
	}

	//use for checking if product already exists with different barcode
	@Transactional
	public ProductPojo find(String name,int id) throws ApiException {
		ProductPojo p = dao.select(name,id);
		return p;
	}



}
