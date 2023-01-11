package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.ProductDao;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.util.StringUtil;

@Service
public class ProductService {

	@Autowired
	private ProductDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addProduct(ProductPojo p) throws ApiException {
		
		validateProduct(p);
		normalizeProduct(p);
		
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
		
		normalizeProduct(p);
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
		if (p == null) {
			throw new ApiException("Product with given ID does not exit, id: " + id);
		}
		return p;
	}
	
	@Transactional
	public ProductPojo findProduct(String barcode) throws ApiException {
		ProductPojo p = dao.select(barcode);
		if (p == null) {
			throw new ApiException("Product with given barcode does not exit, Barcode: " + barcode);
		}
		return p;
	}

	protected static void normalizeProduct(ProductPojo p) {
		
		p.setProductName(StringUtil.toLowerCase(p.getProductName()));
		p.setProductBarcode(StringUtil.toLowerCase(p.getProductBarcode()));
	}
	
	protected void validateProduct(ProductPojo p) throws ApiException {

		if(StringUtil.isEmpty(p.getProductName())) {
			throw new ApiException("Product Name cannot be empty");
		}
		
		if(StringUtil.isEmpty(p.getProductBarcode())) {
			throw new ApiException("Product Barcode cannot be empty");
		}
		
		if(p.getProductMrp()==null) {
			throw new ApiException("Product Mrp cannot be null");
		}
		
		if(p.getProductMrp()<0) {
			throw new ApiException("Product Mrp cannot be less than 0");
		}
		
		if(dao.select(p.getProductBarcode())!=null) {
			throw new ApiException("Product with the same Barcode already exists");
		}
		
		// To check if Product already exists with a different barcode
		ProductPojo check = dao.select(p.getProductName(), p.getProductBrandCategory());
		
		if((check!=null) && check.getProductBarcode()==p.getProductBarcode()) {
			throw new ApiException("The given product already exists in the Database");
		}
		
		if((check!=null) && check.getProductBarcode()!=p.getProductBarcode()) {
			throw new ApiException("The given product already exists with a different Barcode");
		}
	}
}
