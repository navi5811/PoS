package com.increff.employee.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class InventoryApiController {

	@Autowired
	private InventoryService inventoryService;
	
	@Autowired
	private ProductService productService;

	@ApiOperation(value = "Adds Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
	public void add(@RequestBody InventoryForm form) throws ApiException {
		InventoryPojo p = convert(form);
		inventoryService.addInventory(p);
	}
	
	
	@ApiOperation(value = "Deletes Inventory")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.DELETE)
	// /api/1
	public void delete(@PathVariable int id) {
		inventoryService.deleteInventory(id);
	}

	@ApiOperation(value = "Gets Inventory by ID")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable int id) throws ApiException {
		InventoryPojo p = inventoryService.getInventorty(id);
		return convert(p);
	}

	@ApiOperation(value = "Gets list of Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
	public List<InventoryData> getAll() throws ApiException {
		List<InventoryPojo> list = inventoryService.getAllInventory();
		List<InventoryData> list2 = new ArrayList<InventoryData>();
		for (InventoryPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@ApiOperation(value = "Updates Inventory")
	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
		InventoryPojo p = convert(f);
		inventoryService.updateInventory(p);
	}
	

	private InventoryData convert(InventoryPojo p) throws ApiException {
		InventoryData d = new InventoryData();
		
		ProductPojo prod = productService.findProduct(p.getProductId());
		
		d.setInventoryProductBarcode(prod.getProductBarcode());
		d.setProductQuantity(p.getProductQuantity());
		return d;
	}

	private InventoryPojo convert(InventoryForm f) throws ApiException {
		InventoryPojo p = new InventoryPojo();
		
		if(StringUtil.isEmpty(f.getInventoryProductBarcode())) {
			throw new ApiException("Please enter a valid barcode, Barcode field cannot be empty");
		}
		
		ProductPojo prod = productService.findProduct(f.getInventoryProductBarcode());
		
		p.setProductId(prod.getProductId());
		p.setProductQuantity(f.getProductQuantity());
		return p;
	}

}
