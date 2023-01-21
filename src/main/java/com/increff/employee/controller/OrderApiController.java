package com.increff.employee.controller;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.increff.employee.dto.InventoryDto;
import com.increff.employee.dto.ProductDto;
import com.increff.employee.model.BillData;
import com.increff.employee.model.InventoryData;
import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.OrderService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.PDFUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
public class OrderApiController {

	@Autowired
	private OrderService orderservice;

	@Autowired
	private ProductDto productdto;

	@Autowired
	private InventoryDto inventorydto;
	
	@Autowired
	private ProductService productservice;


	private static org.apache.logging.log4j.Logger logger =LogManager.getLogger(OrderApiController.class);
	// getting list of order items of a particular order using arraylist
	@ApiOperation(value = "Add Order Details")
	@RequestMapping(path = "/api/order", method = RequestMethod.POST)
	public OrderData add(@RequestBody List<OrderItemForm> forms) throws ApiException, Exception {

		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();
		logger.error("just entring create order with number of orders",forms.size());
		for (OrderItemForm f : forms) {
			ProductData productdata =productdto.findProduct(f.getProductBarcode());
			ProductPojo p=productdto.convertdp(productdata);
//			logger.error("productdata is fecthed from find product",p.getProductId());
			orderItemList.add(orderservice.convertOrderItemForm(f, p));
		}
//		logger.error("just entring create order with number of orders",orderItemList.size());
		int orderId = orderservice.createOrder(orderItemList);
		double total = orderservice.billTotal(orderId);
		return orderservice.convertOrderPojo(orderservice.getOrder(orderId), total);

	}

	@ApiOperation(value = "Get Order Item details record by id")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.GET)
	public OrderItemData get(@PathVariable int id) throws ApiException {
		OrderItemPojo orderItemPojo = orderservice.get(id);
		int i=orderItemPojo.getOrderProductId();
		ProductData productdata = productdto.findProduct(i);
		OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
		InventoryData inventorydata = inventorydto.findInventory(productdata.getProductId());
		InventoryPojo inventorypojo=inventorydto.convert(inventorydata);
		ProductPojo productpojo=productdto.convertdp(productdata);
		return orderservice.convert(orderItemPojo, productpojo, orderPojo, inventorypojo);
	}

	@ApiOperation(value = "Get list of all Order Items")
	@RequestMapping(path = "/api/order/item", method = RequestMethod.GET)
	public List<OrderItemData> getAll() throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderservice.getAll();
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductData productdata = productdto.findProduct(orderItemPojo.getOrderProductId());
			ProductPojo productPojo=productdto.convertdp(productdata);
			OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
			InventoryData inventorydata = inventorydto.findInventory(productPojo.getProductId());
			InventoryPojo inventoryPojo=inventorydto.convert(inventorydata);
			d.add(orderservice.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

	@ApiOperation(value = "Get list of Orders")
	@RequestMapping(path = "/api/order", method = RequestMethod.GET)
	public List<OrderData> getAllOrders() {
		List<OrderPojo> orders_list = orderservice.getAllOrders();
		List<OrderData> list1 = new ArrayList<OrderData>();
		for (OrderPojo p : orders_list) {
			double total = orderservice.billTotal(p.getOrderId());
			list1.add(orderservice.convertOrderPojo(p, total));
		}
		return list1;
	}

	@ApiOperation(value = "Get list of Order Items of a particular order")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
	public List<OrderItemData> getOrderItemsbyOrderId(@PathVariable int id) throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderservice.getOrderItems(id);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductData productdata = productdto.findProduct(orderItemPojo.getOrderProductId());
			ProductPojo productPojo=productdto.convertdp(productdata);
			OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
			InventoryData inventorydata = inventorydto.findInventory(productPojo.getProductId());
			InventoryPojo inventoryPojo=inventorydto.convert(inventorydata);
			d.add(orderservice.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

	@ApiOperation(value = "Delete Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) throws ApiException {
		orderservice.deleteOrder(id);
	}

	@ApiOperation(value = "Delete Order by id")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
	public void deleteOrder(@PathVariable int id) throws ApiException {
		orderservice.deleteOrder(id);
	}

	@ApiOperation(value = "Update Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {

		ProductPojo productPojo = productservice.findProduct(f.getProductBarcode());
//		ProductPojo productPojo=productdto.convertdp(productdata);
		OrderItemPojo orderItemPojo = orderservice.convertOrderItemForm(f, productPojo);
		orderservice.update(id, orderItemPojo);
	}

	@ApiOperation(value = "Get Invoice")
	@RequestMapping(path = "/api/order/invoice/{orderId}", method = RequestMethod.GET)
	public String getInvoice(@PathVariable int orderId) throws Exception {
		BillData billData = new BillData();
		OrderPojo orderPojo = orderservice.getOrder(orderId);
		List<OrderItemPojo> orderItemPojo_list = orderservice.getOrderItems(orderId);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductData productdata = productdto.findProduct(orderItemPojo.getOrderProductId());
			ProductPojo productPojo=productdto.convertdp(productdata);
			InventoryPojo inventoryPojo = inventorydto
					.convert(inventorydto.getInventory(productPojo.getProductBarcode()));
			d.add(orderservice.convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		billData.setOrderItemData(d);
		billData.setBillAmount(orderservice.billTotal(orderId));
		billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(orderPojo.getDate()));
		billData.setOrderId(orderId);
		// orderService.updateInvoice(orderId);
		PDFUtils.generatePDFFromJavaObject(billData);
		File file = new File("invoice.pdf");
		byte[] contents = Files.readAllBytes(file.toPath());
		return Base64.getEncoder().encodeToString(contents);

	}
}
