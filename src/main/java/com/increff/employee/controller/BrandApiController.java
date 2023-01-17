package com.increff.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class BrandApiController {

	@Autowired
	private BrandDto dto;

	@ApiOperation(value = "Adds a Brand")
	@RequestMapping(path = "/api/brand", method = RequestMethod.POST)
	public void add(@RequestBody BrandForm form) throws ApiException {
		dto.addBrand(form);
	}

	@ApiOperation(value = "Deletes a brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) {
		dto.deleteBrand(id);
	}

	@ApiOperation(value = "Gets a brand by ID")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.GET)
	public BrandData get(@PathVariable int id) throws ApiException {
		return dto.getBrand(id);
	}

	@ApiOperation(value = "Gets list of all Brands")
	@RequestMapping(path = "/api/brand", method = RequestMethod.GET)
	public List<BrandData> getAll() {
		return dto.getAllBrand();
	}

	@ApiOperation(value = "Updates a Brand")
	@RequestMapping(path = "/api/brand/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody BrandForm f) throws ApiException {
		dto.updateBrand(id, f);
	}
}
