package com.increff.employee.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ProductApiController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandService brandService;

	@Autowired
	private InventoryService inventoryService;

	@ApiOperation(value = "Adds a Product")
	@RequestMapping(path = "/api/product", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm form) throws ApiException {
		ProductPojo p = convert(form);
		productService.addProduct(p);
		
		InventoryPojo inv = new InventoryPojo(p.getProductId(), 0);
		inventoryService.addInventory(inv);
	}

	
	@ApiOperation(value = "Deletes a Product")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
	// /api/1
	public void delete(@PathVariable int id) {
		productService.deleteProduct(id);
		inventoryService.deleteInventory(id);
	}

	@ApiOperation(value = "Gets a Product by ID")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable int id) throws ApiException {
		ProductPojo p = productService.getProduct(id);
		return convert(p);
	}

	@ApiOperation(value = "Gets list of all Products")
	@RequestMapping(path = "/api/product", method = RequestMethod.GET)
	public List<ProductData> getAll() throws ApiException {
		List<ProductPojo> list = productService.getAllProduct();
		List<ProductData> list2 = new ArrayList<ProductData>();
		for (ProductPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@ApiOperation(value = "Updates a Product")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
		ProductPojo p = convert(f);
		productService.updateProduct(id, p);
	}
	
	private ProductData convert(ProductPojo p) throws ApiException {
		ProductData d = new ProductData();
		
		d.setProductBarcode(p.getProductBarcode());
		d.setProductName(p.getProductName());
		d.setProductId(p.getProductId());
		d.setProductMrp(p.getProductMrp());
		
		int productBrandId = p.getProductBrandCategory();
		BrandPojo brandPojo = brandService.findBrand(productBrandId);
		
		d.setProductBrandCategoryName(brandPojo.getBrandName());
		d.setProductBrandName(brandPojo.getBrandCategory());
		
		return d;
	}
	
	private ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();
		
		if(StringUtil.isEmpty(f.getProductBrandName())){
			throw new ApiException("Product must belong to a brand, Brand name field cannot be empty");
		}
		
		if(StringUtil.isEmpty(f.getProductBrandCategoryName())){
			throw new ApiException("Product must belong to a , Category name field cannot be empty");
		}
		
		String toFindBrandName = StringUtil.toLowerCase(f.getProductBrandName());
		String toFindBrandCategoryName = StringUtil.toLowerCase(f.getProductBrandCategoryName());
		
		BrandPojo foundBrand = brandService.findBrand(toFindBrandName, toFindBrandCategoryName);
		
		int foundBrandId = foundBrand.getBrandId();
		
		p.setProductBrandCategory(foundBrandId);
		p.setProductName(f.getProductName());
		p.setProductMrp(f.getProductMrp());
		p.setProductBarcode(f.getProductBarcode());
		
		return p;
	}

}
