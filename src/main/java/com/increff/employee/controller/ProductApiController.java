package com.increff.employee.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.dto.InventoryDto;
import com.increff.employee.dto.ProductDto;
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
	private ProductDto productdto;
	
	@Autowired
	private BrandDto branddto;

	@Autowired
	private InventoryDto inventorydto;

	@ApiOperation(value = "Adds a Product")
	@RequestMapping(path = "/api/product", method = RequestMethod.POST)
	public void add(@RequestBody ProductForm form) throws ApiException {
		productdto.addProduct(form);
		
	}

//	
//	@ApiOperation(value = "Deletes a Product")
//	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.DELETE)
//	public void delete(@PathVariable int id) {
//		productdto.deleteProduct(id);
//		inventorydto.deleteInventory(id);
//	}

	@ApiOperation(value = "Gets a Product by ID")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.GET)
	public ProductData get(@PathVariable int id) throws ApiException {
		return productdto.findProduct(id);

	}

	@ApiOperation(value = "Gets list of all Products")
	@RequestMapping(path = "/api/product", method = RequestMethod.GET)
	public List<ProductData> getAll() throws ApiException {

		return productdto.getAllProduct();
	}

	@ApiOperation(value = "Updates a Product")
	@RequestMapping(path = "/api/product/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody ProductForm f) throws ApiException {
		productdto.updateProduct(id, f);
	}
	


}
