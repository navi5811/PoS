package com.increff.pos.dto;

import java.text.ParseException;
import java.util.*;


import javax.transaction.Transactional;

import com.increff.pos.model.ReportSalesForm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.ReportInventoryData;
import com.increff.pos.model.ReportSalesData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
//todo rename employee
@Component
public class ReportDto {

	@Autowired
	private BrandService brandservice;

	@Autowired
	private ProductService productservice;

	@Autowired
	private InventoryService inventoryservice;
	@Autowired
	private OrderService orderservice;

	private static Logger logger = LogManager.getLogger(ReportDto.class);

	@Transactional
	public List<BrandData> getAllBrand() throws ApiException {

		List<BrandPojo> list = brandservice.getAllBrand();
		List<BrandData> list2 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@Transactional
	public List<ReportInventoryData> getInventoryReport() throws ApiException {

		List<BrandPojo> list = brandservice.getAllBrand();
		List<ReportInventoryData> rid = new ArrayList<ReportInventoryData>();
		for (BrandPojo bp : list) {
			Integer number = 0;
			int brandId = bp.getBrandId();
			ReportInventoryData rd = new ReportInventoryData();
			List<ProductPojo> pp = productservice.findAllProduct(brandId);

			for (ProductPojo productpojo : pp) {
				InventoryPojo ip = inventoryservice.getInventory(productpojo.getProductId());
				number += ip.getProductQuantity();
			}

			rd.setBrandCategory(bp.getBrandCategory());
			rd.setBrandName(bp.getBrandName());
			rd.setReportInventoryQuantity(number);
			rid.add(rd);
		}
		return rid;
	}

	@Transactional
	public List<ReportSalesData> getSalesReport(ReportSalesForm form) throws ApiException, ParseException {

		String startDate=form.getStartDate();
		String endDate=form.getEndDate();
		String brand=form.getBrand();
		String category=form.getCategory();
		SimpleDateFormat formatter2=new SimpleDateFormat("yyyy-MM-dd");


		logger.error("start date is " + startDate);
		Date enDate;
		//this is for comparison that if start date is entered as all, then we will consider it as null
		List<ReportSalesData> rsd = new ArrayList<ReportSalesData>();


		if (Objects.equals(startDate, "")) {
			startDate = "1753-01-01";
		}

		if (Objects.equals(endDate, "")) {
			enDate = new Date();
		} else {
			enDate = formatter2.parse(endDate);
		}
		Date stDate = formatter2.parse(startDate);
		logger.error("start date is " + stDate);
		logger.error("end date is " + enDate);
//		enDate.setHours(23);
//		enDate.setMinutes(59);
//		enDate.setSeconds(59);

		logger.error("end date again is " + enDate);

		logger.error("value of brand is  " + brand);

		logger.error("value of category is  " + category);

		if(stDate.compareTo(enDate)>0)
		{
			throw new ApiException("start Date must be less than end date");
		}


		List<OrderPojo> orderList = orderservice.getAllBetween(stDate, enDate);

		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();

		for (OrderPojo order : orderList) {
			List<OrderItemPojo> orderItemListTemp = orderservice.getOrderItems(order.getOrderId());
			orderItemList.addAll(orderItemListTemp);
		}

		HashMap<Integer, Integer> quantityForBrandId = new HashMap<Integer, Integer>();
		HashMap<Integer, Double> revenueForBrandId = new HashMap<Integer, Double>();


		for (OrderItemPojo oip : orderItemList) {
			// Find Brand Category
			int id = oip.getOrderProductId();
			ProductPojo productPojo = productservice.findProduct(id);
			int brandId = productPojo.getProductBrandCategory();
			BrandPojo pojo = brandservice.getBrand(brandId);
			String orderItemBrand = pojo.getBrandName();
			String orderItemCategory = pojo.getBrandCategory();

			if (
					(brand.equals("") || Objects.equals(brand, orderItemBrand)) &&
							(category.equals("") || Objects.equals(category, orderItemCategory))
			) {
				int orderedQuantity = oip.getOrderQuantity();
				Double orderRevenue = oip.getOrderQuantity() * oip.getOrderSellingPrice();
				quantityForBrandId.put(brandId, (quantityForBrandId.getOrDefault(brandId, 0) + orderedQuantity));
				revenueForBrandId.put(brandId, (revenueForBrandId.getOrDefault(brandId, 0D) + orderRevenue));
			}
		}


	// Store Sales Report Data
	List<ReportSalesData> salesReportData = new ArrayList<ReportSalesData>();
		for(int brandCategoryId :quantityForBrandId.keySet())
	{
		BrandPojo pojo = brandservice.getBrand(brandCategoryId);
		ReportSalesData data = new ReportSalesData();
		data.setBrand(pojo.getBrandName());
		data.setCategory(pojo.getBrandCategory());
		data.setQuantity(quantityForBrandId.get(brandCategoryId));
		data.setTotalAmount(revenueForBrandId.get(brandCategoryId));
		salesReportData.add(data);
	}

return salesReportData;
}
	//conversion function
	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrandCategory(p.getBrandCategory());
		d.setBrandName(p.getBrandName());
		return d;
	}
	
}
