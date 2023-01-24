package com.increff.employee.dto;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.model.BillData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.model.InventoryData;
import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.OrderService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.PDFUtils;
import com.increff.employee.util.StringUtil;
//todo
@Service
public class OrderDto {

	@Autowired
	private OrderService orderservice;

	@Autowired
	private ProductService productservice;

	@Autowired
	private InventoryService inventoryservice;

	@Autowired
	private InventoryDto inventorydto;
//	
//	
//	
// Creating order
	@Transactional(rollbackOn = ApiException.class)
	public OrderData createOrder(List<OrderItemForm> forms) throws ApiException {
		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();
		for (OrderItemForm f : forms) {
			ProductPojo p = productservice.findProduct(f.getProductBarcode());
			OrderItemPojo orderitempojo=convertOrderItemForm(f, p);
			validate(orderitempojo);
			orderItemList.add(orderitempojo);
		}
		if (orderItemList.size() == 0) {
			throw new ApiException("Order List cannot be empty");
		}
		
		int orderId = orderservice.createOrder(orderItemList);
		
		for (OrderItemPojo oip : orderItemList) {
			ProductPojo productPojo = productservice.findProduct(oip.getOrderProductId());
			if (productPojo == null) {
				throw new ApiException("Product does not exist.");
			}
			updateInventory(oip);
		}
		
		double total = orderservice.billTotal(orderId);
		return convertOrderPojo(orderservice.getOrder(orderId), total);
	}
	
	@Transactional
	public String createInvoice(int orderId) throws Exception {
		BillData billData = new BillData();
		OrderPojo orderPojo = orderservice.getOrder(orderId);
		List<OrderItemPojo> orderItemPojo_list = orderservice.getOrderItems(orderId);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productservice.findProduct(orderItemPojo.getOrderProductId());
			InventoryPojo inventoryPojo = inventoryservice.getInventory(productPojo.getProductId());
			d.add(convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		billData.setOrderItemData(d);
		billData.setBillAmount(orderservice.billTotal(orderId));
		billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(orderPojo.getDate()));
		billData.setOrderId(orderId);
		PDFUtils.generatePDFFromJavaObject(billData);
		File file = new File("invoice.pdf");
		byte[] contents = Files.readAllBytes(file.toPath());
		return Base64.getEncoder().encodeToString(contents);
	}

	// Fetching an Order item by id
	@Transactional
	public OrderItemData getOrderItemDetails(int id) throws ApiException {

		OrderItemPojo orderItemPojo = orderservice.get(id);

		int i = orderItemPojo.getOrderProductId();

		ProductPojo productpojo = productservice.findProduct(i);

		OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());

		InventoryPojo inventorypojo = inventoryservice.findInventory(productpojo.getProductId());

		return convert(orderItemPojo, productpojo, orderPojo, inventorypojo);

	}

	@Transactional
	public List<OrderItemData> getAllOrderItems(List<OrderItemPojo> od) throws ApiException {

		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : od) {
			ProductPojo productPojo = productservice.findProduct(orderItemPojo.getOrderProductId());
			OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
			InventoryPojo inventoryPojo = inventoryservice.findInventory(productPojo.getProductId());
			d.add(convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

	@Transactional
	public List<OrderData> getAllOrders() throws ApiException {
		List<OrderPojo> orders_list = orderservice.getAllOrders();
		List<OrderData> list1 = new ArrayList<OrderData>();
		for (OrderPojo p : orders_list) {
			double total = orderservice.billTotal(p.getOrderId());
			list1.add(convertOrderPojo(p, total));
		}
		return list1;
	}

	@Transactional
	public List<OrderItemData> getOrderItemByOrderId(int id) throws ApiException {
		List<OrderItemPojo> orderItemPojo_list = orderservice.getOrderItems(id);
		List<OrderItemData> d = new ArrayList<OrderItemData>();
		for (OrderItemPojo orderItemPojo : orderItemPojo_list) {
			ProductPojo productPojo = productservice.findProduct(orderItemPojo.getOrderProductId());
			OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
			InventoryPojo inventoryPojo = inventoryservice.findInventory(productPojo.getProductId());
			d.add(convert(orderItemPojo, productPojo, orderPojo, inventoryPojo));
		}
		return d;
	}

//	@Transactional
//	public OrderItemData get(int id) throws ApiException {
//		OrderItemPojo p = checkIfExists(id);
//		OrderItemData id = convert(p);
//		return id;
//	}

//
//		// Fetching an Order by id
//		@Transactional
//		public OrderPojo getOrder(int id) throws ApiException {
//			OrderPojo p = checkIfExistsOrder(id);
//			return p;
//		}
//
//		// Fetch all order items of a particular order
//		@Transactional
//		public List<OrderItemPojo> getOrderItems(int order_id) throws ApiException {
//			List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
//			return lis;
//		}
//
//		// Calculate Total
//		public double billTotal(int orderId) {
//			double total = 0;
//			List<OrderItemPojo> lis = orderItemDao.selectOrder(orderId);
//			for (OrderItemPojo orderItemPojo : lis) {
//				total += orderItemPojo.getOrderQuantity() * orderItemPojo.getOrderSellingPrice();
//			}
//			return total;
//		}
//
//		// Fetching all order items
	@Transactional
	public List<OrderItemPojo> getAll() {
		return orderservice.getAll();
	}

	@Transactional
	public void delete(int id) throws ApiException {
		increaseInventory(orderservice.delete(id));
	}
//		// Deletion of order
	@Transactional
	public void deleteOrder(int orderId) throws ApiException {

		List<OrderItemPojo> orderItemList = orderservice.getOrderItems(orderId);
		for (OrderItemPojo orderItemPojo : orderItemList) {
			increaseInventory(orderItemPojo);
			orderservice.deleteOrder(orderItemPojo);
		}
		orderservice.deleteOrderhelper(orderId);
	}

//
	@Transactional(rollbackOn = ApiException.class)
	public void updateOrderItem(int id, OrderItemForm f) throws ApiException {
		ProductPojo productPojo = productservice.findProduct(f.getProductBarcode());
		OrderItemPojo orderItemPojo = convertOrderItemForm(f, productPojo);
		update(id, orderItemPojo);
	}

	// Updating order item
	@Transactional(rollbackOn = ApiException.class)
	public void update(int id, OrderItemPojo p) throws ApiException {
		validate(p);
		OrderItemPojo ex = checkIfExists(id);
		increaseInventory(ex);
		ex.setOrderQuantity(p.getOrderQuantity());
		ex.setOrderProductId(p.getOrderProductId());
		ex.setOrderSellingPrice(p.getOrderSellingPrice());
		orderservice.update(ex);
		updateInventory(ex);
	}

	// Increasing inventory while deleting order
	public void increaseInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryservice.getInventory(orderItemPojo.getOrderProductId());
		InventoryPojo ip = orderservice.increaseInventory(inventoryPojo, orderItemPojo);
		inventoryservice.updateInventory(ip);
	}

	// To update inventory while adding order
	public void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryservice.getInventory(orderItemPojo.getOrderProductId());
		int updatedQuantity = orderservice.updateInventory(inventoryPojo, orderItemPojo);
		inventoryPojo.setProductQuantity(updatedQuantity);
		inventoryservice.updateInventory(inventoryPojo);
	}

//
//		// Checking if a particular order item exists or not
	@Transactional(rollbackOn = ApiException.class)
	public OrderItemPojo checkIfExists(int id) throws ApiException {
		OrderItemPojo p = orderservice.checkIfExists(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

//
//		// Checking if a particular order exists or not
	@Transactional(rollbackOn = ApiException.class)
	public OrderPojo checkIfExistsOrder(int id) throws ApiException {
		OrderPojo p = orderservice.checkIfExistsOrder(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

//
	// Validation of order item
	private void validate(OrderItemPojo p) throws ApiException {
		if (p.getOrderQuantity() < 0) {
			throw new ApiException("Quantity must be positive or 0 if you want to cancel order");
		} else if (inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity() < p.getOrderQuantity()) {
			throw new ApiException("Available quantity for product with barcode: "
					+ productservice.findProduct(p.getOrderProductId()).getProductBarcode() + " is: "
					+ inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity());
		}

		if (p.getOrderSellingPrice() > p.getMrp()) {
			throw new ApiException("SP for product with barcode: "
					+ productservice.findProduct(p.getOrderProductId()).getProductBarcode()
					+ " can't be greater than MRP: " + p.getMrp());
		}
	}

	public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
		return orderservice.getAllBetween(startingDate, endingDate);
	}

	public void normalize(String barcode) {
		StringUtil.toLowerCase(barcode);
	}

	public static OrderItemPojo convertOrderItemForm(OrderItemForm orderItemForm, ProductPojo productPojo) {
		OrderItemPojo item = new OrderItemPojo();
		item.setOrderProductId(productPojo.getProductId());
		item.setOrderQuantity(orderItemForm.getProductQuantity());
		item.setOrderSellingPrice(orderItemForm.getProductSellingPrice());
		item.setMrp(productPojo.getProductMrp());
		return item;
	}

	// Convert Order Pojo to Order Data
	public static OrderData convertOrderPojo(OrderPojo pojo, Double total) {
		OrderData d = new OrderData();
		d.setOrderId(pojo.getOrderId());
//			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//			String datetime = dateFormat.format(pojo.getDate());
		d.setDatetime(pojo.getDate());
		d.setBillAmount(total);
		return d;
	}

	public static OrderItemData convert(OrderItemPojo orderItemPojo, ProductPojo productPojo, OrderPojo orderPojo,
			InventoryPojo inventoryPojo) {
		OrderItemData d = new OrderItemData();
		d.setId(orderPojo.getOrderId());
		d.setOrderItemId(orderItemPojo.getOrderItemId());
		d.setName(productPojo.getProductName());
		d.setMrp(productPojo.getProductMrp());
		d.setProductBarcode(productPojo.getProductBarcode());
		d.setProductQuantity(orderItemPojo.getOrderQuantity());
		d.setProductSellingPrice(orderItemPojo.getOrderSellingPrice());
		d.setAvailableQuantity(inventoryPojo.getProductQuantity());
		return d;
	}
}
