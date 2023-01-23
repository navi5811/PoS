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

	@ApiOperation(value = "Gets Inventory by Barcode")
	@RequestMapping(path = "/api/inventory/{inventoryProductBarcode}", method = RequestMethod.GET)
	public InventoryData get(@PathVariable String inventoryProductBarcode) throws ApiException {
		return inventorydto.getInventory(inventoryProductBarcode);
	}

	@ApiOperation(value = "Gets list of Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.GET)
	public List<InventoryData> getAll() throws ApiException {
		return inventorydto.getAllInventory();
	}

	@ApiOperation(value = "Updates Inventory")
	@RequestMapping(path = "/api/inventory", method = RequestMethod.PUT)
	public void update(@RequestBody InventoryForm f) throws ApiException {
		inventorydto.updateInventory(f);
	}
}
