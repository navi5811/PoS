package com.increff.employee.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.StringUtil;

@Service
public class ProductDto {
	
	@Autowired
	private ProductService productservice;
	@Autowired
	private BrandService brandservice;
	@Autowired
	private InventoryService inventoryservice;
	
//	private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(ProductDto.class);

	@Transactional(rollbackOn = ApiException.class)
	public void addProduct(ProductForm f) throws ApiException {

		ProductPojo p = convert(f);	
		normalizeProduct(p);
		validateProduct(p);
		productservice.addProduct(p);
//		logger.error("reached inv");
		InventoryPojo inv = new InventoryPojo(p.getProductId(), 0);
		
//		logger.error("the value of product id is ",p.getProductId());
		inventoryservice.addInventory(inv);
		
	}

	@Transactional
	public void deleteProduct(int id) {
		productservice.deleteProduct(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public ProductData getProduct(int id) throws ApiException {

		ProductPojo pp = productservice.findProduct(id);
		ProductData pd = convert(pp);
		return pd;
	}

	@Transactional
	public List<ProductData> getAllProduct() throws ApiException {

		List<ProductPojo> list = productservice.getAllProduct();
		List<ProductData> list2 = new ArrayList<ProductData>();
		for (ProductPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateProduct(int id, ProductForm f) throws ApiException {

		ProductPojo p = convert(f);
		normalizeProduct(p);
		ProductPojo ex = productservice.findProduct(id);

		ex.setProductName(p.getProductName());
		ex.setProductBrandCategory(p.getProductBrandCategory());
		ex.setProductBarcode(p.getProductBarcode());
		ex.setProductMrp(p.getProductMrp());

		productservice.updateProduct(id, ex);
	}

	@Transactional
	public ProductPojo findProduct(int id) throws ApiException {
		ProductPojo p = productservice.findProduct(id);
		if (p == null) {
			throw new ApiException("Product with given ID does not exit, id: " + id);
		}
		return p;
	}

	@Transactional
	public ProductPojo findProduct(String barcode) throws ApiException {
		ProductPojo p = productservice.findProduct(barcode);
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

		if (StringUtil.isEmpty(p.getProductName())) {
			throw new ApiException("Product Name cannot be empty");
		}

		if (StringUtil.isEmpty(p.getProductBarcode())) {
			throw new ApiException("Product Barcode cannot be empty");
		}

		if (p.getProductMrp() == null) {
			throw new ApiException("Product Mrp cannot be null");
		}

		if (p.getProductMrp() < 0) {
			throw new ApiException("Product Mrp cannot be less than 0");
		}

		if (productservice.findProduct(p.getProductBarcode()) != null) {
			throw new ApiException("Product with the same Barcode already exists");
		}

		// To check if Product already exists with a different barcode
//			BrandPojo check = brandservice.findBrand(p.getProductName(), p.getProductBrandCategory());
//
//		if ((check != null) && check.getProductBarcode() == p.getProductBarcode()) {
//			throw new ApiException("The given product already exists in the Database");
//		}
//
//		if ((check != null) && check.getProductBarcode() != p.getProductBarcode()) {
//			throw new ApiException("The given product already exists with a different Barcode");
//		}
	}

	private ProductData convert(ProductPojo p) throws ApiException {
		ProductData d = new ProductData();

		d.setProductBarcode(p.getProductBarcode());
		d.setProductName(p.getProductName());
		d.setProductId(p.getProductId());
		d.setProductMrp(p.getProductMrp());

		int productBrandId = p.getProductBrandCategory();
		BrandPojo brandPojo = brandservice.findBrand(productBrandId);
		
		d.setProductBrandCategoryName(brandPojo.getBrandCategory());
		d.setProductBrandName(brandPojo.getBrandName());

		return d;
	}

	private ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();

		if (StringUtil.isEmpty(f.getProductBrandName())) {
			throw new ApiException("Product must belong to a brand, Brand name field cannot be empty");
		}

		if (StringUtil.isEmpty(f.getProductBrandCategoryName())) {
			throw new ApiException("Product must belong to a , Category name field cannot be empty");
		}

		String toFindBrandName = StringUtil.toLowerCase(f.getProductBrandName());
		String toFindBrandCategoryName = StringUtil.toLowerCase(f.getProductBrandCategoryName());

		BrandPojo foundBrand = brandservice.findBrand(toFindBrandName, toFindBrandCategoryName);

		int foundBrandId = foundBrand.getBrandId();

		p.setProductBrandCategory(foundBrandId);
		p.setProductName(f.getProductName());
		p.setProductMrp(f.getProductMrp());
		p.setProductBarcode(f.getProductBarcode());

		return p;
	}
}
