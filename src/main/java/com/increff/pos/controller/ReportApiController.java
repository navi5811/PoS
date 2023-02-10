package com.increff.pos.controller;

import java.text.ParseException;
import java.util.List;

import com.increff.pos.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.increff.pos.dto.ReportDto;
import com.increff.pos.service.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class ReportApiController {
	private static Logger logger = LogManager.getLogger(ReportDto.class);
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
		@RequestMapping(path = "/api/report/sales", method = RequestMethod.POST)
		public List<ReportSalesData> getSales(@RequestBody ReportSalesForm form) throws ApiException, ParseException {
			return reportdto.getSalesReport(form);
		}
	}


