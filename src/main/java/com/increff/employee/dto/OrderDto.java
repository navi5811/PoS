//package com.increff.employee.dto;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//
//import javax.transaction.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.increff.employee.model.BrandForm;
//import com.increff.employee.model.OrderData;
//import com.increff.employee.model.OrderItemData;
//import com.increff.employee.model.OrderItemForm;
//import com.increff.employee.pojo.BrandPojo;
//import com.increff.employee.pojo.InventoryPojo;
//import com.increff.employee.pojo.OrderItemPojo;
//import com.increff.employee.pojo.OrderPojo;
//import com.increff.employee.pojo.ProductPojo;
//import com.increff.employee.service.ApiException;
//import com.increff.employee.service.InventoryService;
//import com.increff.employee.service.OrderService;
//import com.increff.employee.service.ProductService;
//import com.increff.employee.util.StringUtil;
//
//@Service
//public class OrderDto {
//	
//	@Autowired
//	private OrderService orderservice;
//	
//	@Autowired
//	private ProductService productservice;
//	
//	@Autowired
//	private InventoryService inventoryservice;
//	
//	
//	
//	
//	// Creating order
//		@Transactional(rollbackOn = ApiException.class)
//		public int createOrder(List<OrderItemPojo> orderItemList) throws ApiException {
//			if(orderItemList.size() == 0){
//				throw new ApiException("Order List cannot be empty");
//			}
//			OrderPojo orderPojo = new OrderPojo();
//			orderPojo.setDate(new Date());
//			orderDao.insert(orderPojo);
//			for (OrderItemPojo p : orderItemList) {
//				ProductPojo productPojo = productservice.get(p.getProductId());
//				if (productPojo == null) {
//					throw new ApiException("Product does not exist.");
//				}
//				validate(p);
//				p.setOrderId(orderPojo.getOrderId());
//				orderItemDao.insert(p);
//				updateInventory(p);
//			}
//			return orderPojo.getOrderId();
//		}
//
//		// Fetching an Order item by id
//		@Transactional
//		public OrderItemPojo get(int id) throws ApiException {
//			OrderItemPojo p = checkIfExists(id);
//			return p;
//		}
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
//		@Transactional
//		public List<OrderItemPojo> getAll() {
//			return orderItemDao.selectAll();
//		}
//
//		// Fetching all orders
//		@Transactional
//		public List<OrderPojo> getAllOrders() {
//			return orderDao.selectAll();
//		}
//
//		// Deletion of order item
//		@Transactional
//		public void delete(int id) throws ApiException {
//			int order_id = orderItemDao.select(OrderItemPojo.class, id).getOrderId();
//			increaseInventory(orderItemDao.select(OrderItemPojo.class, id));
//			orderItemDao.delete(OrderItemPojo.class, id);
//			List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
//			if (lis.isEmpty()) {
//				orderDao.delete(OrderPojo.class, order_id);
//			}
//		}
//
//		// Deletion of order
//		@Transactional
//		public void deleteOrder(int orderId) throws ApiException {
//			List<OrderItemPojo> orderItemList = getOrderItems(orderId);
//			for (OrderItemPojo orderItemPojo : orderItemList) {
//				increaseInventory(orderItemPojo);
//				orderItemDao.delete(OrderItemPojo.class, orderItemPojo.getOrderId());
//			}
//			orderDao.delete(OrderPojo.class, orderId);
//		}
//
//		// Updating order item
//		@Transactional(rollbackOn = ApiException.class)
//		public void update(int id, OrderItemPojo p) throws ApiException {
//			validate(p);
//			OrderItemPojo ex = checkIfExists(id);
//			increaseInventory(ex);
//			ex.setAvailableQuantity(p.getOrderQuantity());
//			ex.setProductId(p.getProductId());
//			ex.setSellingPrice(p.getOrderSellingPrice());
//			orderItemDao.update(ex);
//			updateInventory(ex);
//		}
//
//		// Increasing inventory while deleting order
//		private void increaseInventory(OrderItemPojo orderItemPojo) throws ApiException {
//			InventoryPojo inventoryPojo = inventoryservice.getByProductId(orderItemPojo.getProductId());
//			int updatedQuantity = inventoryPojo.getProductQuantity() + orderItemPojo.getOrderQuantity();
//			inventoryPojo.setAvailableQuantity(updatedQuantity);
//			inventoryservice.update(inventoryPojo.getOrderId(), inventoryPojo);
//		}
//
//		// To update inventory while adding order
//		private void updateInventory(OrderItemPojo orderItemPojo) throws ApiException {
//			InventoryPojo inventoryPojo = inventoryservice.getByProductId(orderItemPojo.getProductId());
//			int updatedQuantity = inventoryPojo.getProductQuantity() - orderItemPojo.getOrderQuantity();
//			inventoryPojo.setAvailableQuantity(updatedQuantity);
//			inventoryservice.update(inventoryPojo.getOrderId(), inventoryPojo);
//		}
//
//		// Checking if a particular order item exists or not
//		@Transactional(rollbackFor = ApiException.class)
//		public OrderItemPojo checkIfExists(int id) throws ApiException {
//			OrderItemPojo p = orderItemDao.select(OrderItemPojo.class, id);
//			if (p == null) {
//				throw new ApiException("Order Item with given ID does not exist, id: " + id);
//			}
//			return p;
//		}
//
//		// Checking if a particular order exists or not
//		@Transactional(rollbackFor = ApiException.class)
//		public OrderPojo checkIfExistsOrder(int id) throws ApiException {
//			OrderPojo p = orderDao.select(OrderPojo.class, id);
//			if (p == null) {
//				throw new ApiException("Order with given ID does not exist, id: " + id);
//			}
//			return p;
//		}
//
//		// Validation of order item
//		private void validate(OrderItemPojo p) throws ApiException {
//			if (p.getOrderQuantity() < 0) {
//				throw new ApiException("Quantity must be positive or 0 if you want to cancel order");
//			} else if (inventoryservice.getByProductId(p.getProductId()).getQuantity() < p.getOrderQuantity()) {
//				throw new ApiException("Available quantity for product with barcode: " + productservice.get(p.getProductId()).getBarcode() + " is: " + inventoryservice.getByProductId(p.getProductId()).getQuantity());
//			}
//			
//			if(p.getOrderSellingPrice() > p.getMrp())
//			{
//				throw new ApiException("SP for product with barcode: " + productservice.get(p.getProductId()).getBarcode() + " can't be greater than MRP: " + p.getMrp());
//			}
//		}
//
//		public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
//			return orderDao.selectBetweenDate(startingDate, endingDate);
//		}
//	
////	@Transactional
////	public void deleteOrder(int id) {
////		orderservice.deleteOrder(id);
////	}	
////	
////	
////	
//
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
////	public void normalize(String barcode) {
////		StringUtil.toLowerCase(barcode);
////		
////	}
//	
//	//Convert OrderItemForm to OrderItemPojo
//
//
//}
