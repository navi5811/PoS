package com.increff.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.dto.InventoryDto;
import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class InventoryApiController {

	@Autowired
	private InventoryDto inventorydto;

//	@ApiOperation(value = "Adds Inventory")
//	@RequestMapping(path = "/api/inventory", method = RequestMethod.POST)
//	public void add(@RequestBody InventoryForm form) throws ApiException {
//		inventoryService.addInventory(form);
//	}

	@ApiOperation(value = "Gets Inventory by ID")
	@RequestMapping(path = "/api/inventory/{productId}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable int productId) throws ApiException {
		return inventorydto.getInventory(productId);
	}

//	@ApiOperation(value = "Gets Inventory by barcode")
//	@RequestMapping(path = "/api/inventory/{inventoryProductBarcode}", method = RequestMethod.GET)
//	public InventoryData get(@PathVariable String inventoryProductBarcode) throws ApiException {
//
//		ProductPojo p = productService.findProduct(inventoryProductBarcode);
//		int productId = p.getProductId();
//
//		InventoryPojo pi = inventoryService.getInventory(productId);
//		return convert(pi);
//	}

	@ApiOperation(value = "Gets list of Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
	public List<InventoryData> getAll() throws ApiException {
		return inventorydto.getAllInventory();
	}

//	@ApiOperation(value = "Updates Inventory")
//	@RequestMapping(path = "/api/inventory/{id}", method = RequestMethod.PUT)
//	public void update(@PathVariable int id, @RequestBody InventoryForm f) throws ApiException {
//		InventoryPojo p = convert(f);
//		inventoryService.updateInventory(p);
//	}

	@ApiOperation(value = "Updates Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.PUT)
	public void update(@RequestBody InventoryForm f) throws ApiException {
		inventorydto.updateInventory(f);
	}

}
