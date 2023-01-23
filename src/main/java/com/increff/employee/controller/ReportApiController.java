package com.increff.employee.controller;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.dto.BrandDto;
import com.increff.employee.dto.ReportDto;
import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ReportInventoryData;
import com.increff.employee.model.ReportSalesData;
import com.increff.employee.model.ReportSalesForm;
import com.increff.employee.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ReportApiController {
	
		@Autowired
		private ReportDto reportdto;

		@ApiOperation(value = "Gets list of all Brands")
		@RequestMapping(path = "/api/report/brand", method = RequestMethod.GET)
		public List<BrandData> getAll() throws ApiException {
			return reportdto.getAllBrand();
		}
		
		@ApiOperation(value = "Gets list of all inventory")
		@RequestMapping(path = "/api/report/inventory", method = RequestMethod.GET)
		public List<ReportInventoryData> get() throws ApiException {
			return reportdto.getInventoryReport();
		}
		
		
		@ApiOperation(value = "Gets Sales Report")
		@RequestMapping(path = "/api/report/sales", method = RequestMethod.GET)
		public ReportSalesData getSales(@RequestParam Date startDate,@RequestParam Date endDate,@RequestParam String brand,@RequestParam String category) throws ApiException, ParseException {
			return reportdto.getSalesReport(startDate,endDate,brand,category);
		}
	}


