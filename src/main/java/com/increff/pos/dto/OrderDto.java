package com.increff.pos.dto;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

import javax.transaction.Transactional;

import com.increff.pos.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.OrderService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.PDFUtils;
import com.increff.pos.util.StringUtil;

//todo
@Component
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
			normalize(f.getProductBarcode());
			ProductPojo p = productservice.findProduct(f.getProductBarcode());
			if(p==null)
			{
				throw new ApiException("product with given barcode does not exist");
			}
			OrderItemPojo orderitempojo = convertOrderItemForm(f, p);
			
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

		Double total = orderservice.billTotal(orderId);
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
		//orderPojo.getDate())
		billData.setOrderItemData(d);
		billData.setBillAmount(orderservice.billTotal(orderId));

			if(orderPojo.isInvoiced()!=true)
			{ billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()));
			orderPojo.setInvoicedTime(new Date());
			orderPojo.setInvoiced(true);
//			orderData.setInvoiced(true);
			}
			else {
				billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(orderPojo.getInvoicedTime()));
			}
		billData.setOrderId(orderId);
		String basePath = new File("").getPath();
		String fileName = basePath + "src/main/resources/com/increff/pos/invoice/invoice" + orderId + ".pdf";
		File file = new File(fileName);
		PDFUtils.generatePDFFromJavaObject(billData, fileName);

		byte[] contents = Files.readAllBytes(file.toPath());
		return Base64.getEncoder().encodeToString(contents);
	}

	// Fetching an Order item by id
	@Transactional
	public OrderItemData getOrderItemDetails(int id) throws ApiException {
		checkIfExists(id);
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
		checkIfExistsOrder(id);
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

	// Calculate Total

//
//		// Fetching all order items
	@Transactional
	public List<OrderItemPojo> getAll() {
		return orderservice.getAll();
	}

	//deleting an order item
	@Transactional
	public void delete(int id) throws ApiException {
		increaseInventory(orderservice.delete(id));
	}

	// Deletion of order
	@Transactional
	public void deleteOrder(int orderId) throws ApiException {

		List<OrderItemPojo> orderItemList = orderservice.getOrderItems(orderId);
		for (OrderItemPojo orderItemPojo : orderItemList) {
			increaseInventory(orderItemPojo);
			orderservice.deleteOrder(orderItemPojo);
		}
		orderservice.deleteOrderhelper(orderId);
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateOrderItem(int id, OrderItemForm f) throws ApiException {
		ProductPojo productPojo = productservice.findProduct(f.getProductBarcode());

		//orderItem pojo that is received through edit form
		OrderItemPojo orderItemPojo = convertOrderItemForm(f, productPojo);

		//orderItem pojo that is get from backend through id
		OrderItemPojo oip=orderservice.get(id);
		//quantity that is present in the order already
		 Integer quantity=oip.getOrderQuantity();



		int orderId=oip.getOrderId();
		OrderPojo op=orderservice.getOrder(orderId);
		if(op.isInvoiced()==true)
		{
			throw new ApiException("Order Item can't be edited");
		}
		update(id, orderItemPojo, quantity);
	}

	// Updating order item
	@Transactional(rollbackOn = ApiException.class)
	public void update(int id, OrderItemPojo p,Integer quantity) throws ApiException {
		validateUpdate(p,quantity);
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

	// Checking if a particular order item exists or not
	@Transactional(rollbackOn = ApiException.class)
	public OrderItemPojo checkIfExists(int id) throws ApiException {
		OrderItemPojo p = orderservice.checkIfExists(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Checking if a particular order exists or not
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
	private void validateUpdate(OrderItemPojo p,Integer quantity) throws ApiException {
		boolean flag =false;
		if(p.getOrderQuantity()<=quantity)
		{
			flag=true;
		}
		Integer moduloQuantity=quantity-p.getOrderQuantity();
		if (p.getOrderQuantity() <= 0) {
			throw new ApiException("Quantity must be greater than or equal to 0");
		}else if (inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity() < moduloQuantity && flag==false) {
			throw new ApiException("Available quantity for product with barcode: "
					+ productservice.findProduct(p.getOrderProductId()).getProductBarcode() + " is: "
					+ inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity());
		}

		if(p.getOrderSellingPrice()<0)
		{
			throw new ApiException("Selling price cannot be less than 0");
		}
		if (p.getOrderSellingPrice() > p.getMrp()) {
			throw new ApiException("SP for product with barcode: "
					+ productservice.findProduct(p.getOrderProductId()).getProductBarcode()
					+ " can't be greater than MRP: " + p.getMrp());
		}
	}
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
		d.setInvoiced(pojo.isInvoiced());
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
