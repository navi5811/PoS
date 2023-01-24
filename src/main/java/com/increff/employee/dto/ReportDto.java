package com.increff.employee.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.model.BrandData;
import com.increff.employee.model.ReportInventoryData;
import com.increff.employee.model.ReportSalesData;
import com.increff.employee.model.ReportSalesForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.BrandService;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.OrderService;
import com.increff.employee.service.ProductService;
//todo rename employee
@Service
public class ReportDto {

	@Autowired
	private BrandService brandservice;
	
	@Autowired
	private ProductService productservice;
	
	@Autowired
	private InventoryService inventoryservice;
	@Autowired
	private OrderService orderservice;
	
	@Transactional
	public List<BrandData> getAllBrand() throws ApiException{

		List<BrandPojo> list = brandservice.getAllBrand();
		List<BrandData> list2 = new ArrayList<BrandData>();
		for (BrandPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}
	
	@Transactional
	public List<ReportInventoryData> getInventoryReport() throws ApiException{

		List<BrandPojo> list = brandservice.getAllBrand();
		List<ReportInventoryData> rid =new ArrayList<ReportInventoryData>();
		for(BrandPojo bp : list) {
			int number =0;
			int brandId=bp.getBrandId();
			ReportInventoryData rd=new ReportInventoryData();
			List<ProductPojo> pp =productservice.findAllProduct(brandId);
			
			for(ProductPojo productpojo : pp) {
			InventoryPojo ip=inventoryservice.getInventory(productpojo.getProductId());
			number+=ip.getProductQuantity();
			}
			
			rd.setBrandCategory(bp.getBrandCategory());
			rd.setBrandName(bp.getBrandName());
			rd.setReportInventoryQuantity(number);
			rid.add(rd);
		}
		return rid;
	}
	
	@Transactional
	public ReportSalesData getSalesReport(Date startDate,Date endDate,String brand,String category) throws ApiException, ParseException{

		ReportSalesData rsd=new ReportSalesData();
		
		List<OrderPojo> orderlist=orderservice.getAllOrders();
		List<Integer> productIdList=new ArrayList<Integer>();
		List<OrderItemPojo> orderItemList =new ArrayList<OrderItemPojo>();
//		HashMap<Integer,pair<Integer,Integer> map = new HashMap<>();
//		SimpleDateFormat formatter6=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");  
//
//		Date stDate=formatter6.parse(startDate);
//		Date enDate=formatter6.parse(endDate);
		for(OrderPojo op : orderlist) {
				Date dt=op.getDate();

				if(dt.getTime() >=startDate.getTime() && dt.getTime()<= endDate.getTime())
				{
					List<OrderItemPojo> orderItemtempList =new ArrayList<OrderItemPojo>();
					int id=op.getOrderId();
					//got the list of orderitempojo with order id that are falling under the given dates
					orderItemtempList =orderservice.getOrderItems(id);	
					for(OrderItemPojo ip:orderItemtempList) {
						orderItemList.add(ip);
					}
				}
		}
		
//		
//		BrandPojo bp=brandservice.findBrand(brand, category);
//		
//		List<ProductPojo> productPojoList=productservice.findAllProduct(bp.getBrandId());
		
		int totalquantity=0;
		Double revenue =0.0;
		for(OrderItemPojo oip : orderItemList)
		{
			int id=oip.getOrderProductId();
			ProductPojo p=productservice.findProduct(id);
			int brandId=p.getProductBrandCategory();
			BrandPojo brandpojo=brandservice.getBrand(brandId);
			if(brandpojo.getBrandName()==brand && brandpojo.getBrandCategory()==category)
			{
				rsd.setBrand(brand);
				rsd.setCategory(category);
				totalquantity+=oip.getOrderQuantity();
				revenue+=oip.getOrderQuantity()*oip.getOrderSellingPrice();
			}
		}
		
		rsd.setQuantity(totalquantity);
		rsd.setTotalamount(revenue);
		
	    return rsd;
	}
	
	
	
	
	
	//conversion function
	private static BrandData convert(BrandPojo p) {
		BrandData d = new BrandData();
		d.setBrandCategory(p.getBrandCategory());
		d.setBrandName(p.getBrandName());
		d.setBrandId(p.getBrandId());
		return d;
	}
	
}
