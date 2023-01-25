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
import com.increff.employee.dto.OrderDto;
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

	@Autowired
	private OrderDto orderdto;

	private static Logger logger = (Logger) LogManager.getLogger(OrderApiController.class);
	
	// getting list of order items of a particular order using arraylist
	@ApiOperation(value = "Add Order Details")
	@RequestMapping(path = "/api/order", method = RequestMethod.POST)
	public OrderData add(@RequestBody List<OrderItemForm> forms) throws ApiException, Exception {
		return orderdto.createOrder(forms);
	}

	@ApiOperation(value = "Get Order Item details record by id")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.GET)
	public OrderItemData get(@PathVariable int id) throws ApiException {
		return orderdto.getOrderItemDetails(id);
	}

	@ApiOperation(value = "Get list of all Order Items")
	@RequestMapping(path = "/api/order/item", method = RequestMethod.GET)
	public List<OrderItemData> getAll() throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		return orderdto.getAllOrderItems(orderItemPojo_list);
	}

	@ApiOperation(value = "Get list of Orders")
	@RequestMapping(path = "/api/order", method = RequestMethod.GET)
	public List<OrderData> getAllOrders() throws ApiException {
		return orderdto.getAllOrders();
	}

	@ApiOperation(value = "Get list of Order Items of a particular order")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.GET)
	public List<OrderItemData> getOrderItemsbyOrderId(@PathVariable int id) throws ApiException {
		return orderdto.getOrderItemByOrderId(id);
	}

	@ApiOperation(value = "Delete Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable int id) throws ApiException {
		orderdto.delete(id);
	}

	@ApiOperation(value = "Delete Order by id")
	@RequestMapping(path = "/api/order/{id}", method = RequestMethod.DELETE)
	public void deleteOrder(@PathVariable int id) throws ApiException {
		orderdto.deleteOrder(id);
	}

	@ApiOperation(value = "Update Order Item record")
	@RequestMapping(path = "/api/order/item/{id}", method = RequestMethod.PUT)
	public void update(@PathVariable int id, @RequestBody OrderItemForm f) throws ApiException {
		orderdto.updateOrderItem(id, f);
	}

	@ApiOperation(value = "Get Invoice")
	@RequestMapping(path = "/api/order/invoice/{orderId}", method = RequestMethod.GET)
	public String getInvoice(@PathVariable int orderId) throws Exception {
		
		return orderdto.createInvoice(orderId);
		// orderService.updateInvoice(orderId);

	}
}
