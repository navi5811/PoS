package com.increff.employee.service;

import com.increff.employee.dao.OrderDao;
import com.increff.employee.dao.OrderItemDao;
import com.increff.employee.dto.ProductDto;
import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	@Autowired
	private InventoryService inventoryservice;
	
	@Autowired
	private ProductService productservice;
	
	@Autowired
	private ProductDto productdto;
	

	// Creating order
	@Transactional(rollbackFor = ApiException.class)
	public int createOrder(List<OrderItemPojo> orderItemList) throws ApiException {
		if(orderItemList.size() == 0){
			throw new ApiException("Order List cannot be empty");
		}
		OrderPojo orderPojo = new OrderPojo();
		orderPojo.setDate(new Date());
		orderDao.insert(orderPojo);
		for (OrderItemPojo p : orderItemList) {
			ProductPojo productPojo= productservice.getProduct(p.getOrderProductId());
			if (productPojo == null) {
				throw new ApiException("Product does not exist.");
			}
			validate(p);
			p.setOrderId(orderPojo.getOrderId());
			orderItemDao.insert(p);
			updateInventory(p);
		}
		return orderPojo.getOrderId();
	}

	// Fetching an Order item by id
	@Transactional
	public OrderItemPojo get(int id) throws ApiException {
		OrderItemPojo p = checkIfExists(id);
		return p;
	}

	// Fetching an Order by id
	@Transactional
	public OrderPojo getOrder(int id) throws ApiException {
		OrderPojo p = checkIfExistsOrder(id);
		return p;
	}

	// Fetch all order items of a particular order
	@Transactional
	public List<OrderItemPojo> getOrderItems(int order_id) throws ApiException {
		List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
		return lis;
	}

	// Calculate Total
	public double billTotal(int orderId) {
		double total = 0;
		List<OrderItemPojo> lis = orderItemDao.selectOrder(orderId);
		for (OrderItemPojo orderItemPojo : lis) {
			total += orderItemPojo.getOrderQuantity() * orderItemPojo.getOrderSellingPrice();
		}
		return total;
	}

	// Fetching all order items
	@Transactional
	public List<OrderItemPojo> getAll() {
		return orderItemDao.selectAll();
	}

	// Fetching all orders
	@Transactional
	public List<OrderPojo> getAllOrders() {
		return orderDao.selectAll();
	}

	// Deletion of order item
	@Transactional
	public void delete(int id) throws ApiException {
		int order_id = orderItemDao.select(id).getOrderId();
		increaseInventory(orderItemDao.select(id));
		orderItemDao.delete(id);
		List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
		if (lis.isEmpty()) {
			orderDao.delete(order_id);
		}
	}

	// Deletion of order
	@Transactional
	public void deleteOrder(int orderId) throws ApiException {
		List<OrderItemPojo> orderItemList = getOrderItems(orderId);
		for (OrderItemPojo orderItemPojo : orderItemList) {
			increaseInventory(orderItemPojo);
			orderItemDao.delete(orderItemPojo.getOrderId());
		}
		orderDao.delete(orderId);
	}

	// Updating order item
	@Transactional(rollbackFor = ApiException.class)
	public void update(int id, OrderItemPojo p) throws ApiException {
		validate(p);
		OrderItemPojo ex = checkIfExists(id);
		increaseInventory(ex);
		ex.setOrderQuantity(p.getOrderQuantity());
		ex.setOrderProductId(p.getOrderProductId());
		ex.setOrderSellingPrice(p.getOrderSellingPrice());
		orderItemDao.update(ex);
		updateInventory(ex);
	}

	// Increasing inventory while deleting order
	private void increaseInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryservice.getInventory(orderItemPojo.getOrderProductId());
		int updatedQuantity = inventoryPojo.getProductQuantity() + orderItemPojo.getOrderQuantity();
		inventoryPojo.setProductQuantity(updatedQuantity);
		inventoryservice.updateInventory(inventoryPojo);
	}

	// very much doubtfull
	// To update inventory while adding order
	private void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
		InventoryPojo inventoryPojo = inventoryservice.getInventory(orderItemPojo.getOrderProductId());
		int updatedQuantity = inventoryPojo.getProductQuantity() - orderItemPojo.getOrderQuantity();
		inventoryPojo.setProductQuantity(updatedQuantity);
		inventoryservice.updateInventory(inventoryPojo);
	}

	// Checking if a particular order item exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderItemPojo checkIfExists(int id) throws ApiException {
		OrderItemPojo p = orderItemDao.select(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Checking if a particular order exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderPojo checkIfExistsOrder(int id) throws ApiException {
		OrderPojo p = orderDao.select(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}
	
	private void validate(OrderItemPojo p) throws ApiException {
		if (p.getOrderQuantity() < 0) {
			throw new ApiException("Quantity must be positive or 0 if you want to cancel order");
		} else if (inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity() < p.getOrderQuantity()) {
			throw new ApiException("Available quantity for product with barcode: " + productservice.getProduct(p.getOrderProductId()).getProductBarcode() + " is: " + inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity());
		}
		
		if(p.getOrderSellingPrice() > p.getMrp())
		{
			throw new ApiException("SP for product with barcode: " + productservice.getProduct(p.getOrderProductId()).getProductBarcode() + " can't be greater than MRP: " + p.getMrp());
		}
	}

	public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
		return orderDao.selectBetweenDate(startingDate, endingDate);
	}
	public static OrderItemPojo convertOrderItemForm(OrderItemForm orderItemForm,
			ProductPojo productPojo) {
		OrderItemPojo item = new OrderItemPojo();
		item.setOrderProductId(productPojo.getProductId());
		item.setOrderQuantity(orderItemForm.getProductQuantity());
		item.setOrderSellingPrice(orderItemForm.getProductSellingPrice());
		item.setMrp(productPojo.getProductMrp());
		return item;
	}

	// Convert Order Pojo to Order Data
	public static OrderData convertOrderPojo(OrderPojo pojo, double total) {
		OrderData d = new OrderData();
		d.setOrderId(pojo.getOrderId());
//		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//		String datetime = dateFormat.format(pojo.getDate());
		d.setDatetime(pojo.getDate());
		d.setBillAmount(total);
		return d;
	}
	public static OrderItemData convert(OrderItemPojo orderItemPojo, ProductPojo productPojo, OrderPojo orderPojo, InventoryPojo inventoryPojo) {
		OrderItemData d = new OrderItemData();
		
		d.setId(orderPojo.getOrderId());
		d.setOrderItemId(orderItemPojo.getOrderItemId());
		d.setName(productPojo.getProductName());
		d.setMrp(productPojo.getProductMrp()); 
		d.setProductBarcode(productPojo.getProductBarcode());
//		d.setAvailableQuantity(orderItemPojo.getOrderQuantity());
		d.setProductQuantity(orderItemPojo.getOrderQuantity());
		d.setProductSellingPrice(orderItemPojo.getOrderSellingPrice());
		d.setAvailableQuantity(inventoryPojo.getProductQuantity());
		return d;
	}
}