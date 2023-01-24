package com.increff.employee.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.employee.dao.OrderDao;
import com.increff.employee.dao.OrderItemDao;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;

@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	// Creating order
	@Transactional(rollbackFor = ApiException.class)
	public int createOrder(List<OrderItemPojo> orderItemList) throws ApiException {
		OrderPojo orderPojo = new OrderPojo();
		orderPojo.setDate(new Date());
		orderDao.insert(orderPojo);
		for (OrderItemPojo p : orderItemList) {
			p.setOrderId(orderPojo.getOrderId());
			orderItemDao.insert(p);
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
	public OrderItemPojo delete(int id) throws ApiException {
		int order_id = orderItemDao.select(id).getOrderId();
		OrderItemPojo orderItemPojo=orderItemDao.select(id);
		orderItemDao.delete(id);
		List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
		if (lis.isEmpty()) {
			orderDao.delete(order_id);
		}
		return orderItemPojo;
	}

	// Deletion of order doubtfull
	@Transactional
	public void deleteOrder(OrderItemPojo orderItemPojo) throws ApiException {
			orderItemDao.delete(orderItemPojo.getOrderId());

	}
	public void deleteOrderhelper(int orderId) throws ApiException {	
	orderDao.delete(orderId);
}
	

	// Updating order item
	@Transactional(rollbackFor = ApiException.class)
	public void update(OrderItemPojo ex) throws ApiException {
		orderItemDao.update(ex);
	}

	// Increasing inventory while deleting order
	public InventoryPojo increaseInventory(InventoryPojo inventoryPojo, OrderItemPojo orderItemPojo)
			throws ApiException {
		int updatedQuantity = inventoryPojo.getProductQuantity() + orderItemPojo.getOrderQuantity();
		inventoryPojo.setProductQuantity(updatedQuantity);
		return inventoryPojo;
	}

	// To update inventory while adding order
	public int updateInventory(InventoryPojo inventoryPojo, OrderItemPojo orderItemPojo) throws ApiException {
		int updatedQuantity = inventoryPojo.getProductQuantity() - orderItemPojo.getOrderQuantity();
		return updatedQuantity;
	}

	// Checking if a particular order item exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderItemPojo checkIfExists(int id) throws ApiException {
		OrderItemPojo p = orderItemDao.select(id);
		return p;
	}

	// Checking if a particular order exists or not
	@Transactional(rollbackFor = ApiException.class)
	public OrderPojo checkIfExistsOrder(int id) throws ApiException {
		OrderPojo p = orderDao.select(id);
		return p;
	}

	public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
		return orderDao.selectBetweenDate(startingDate, endingDate);
	}
}