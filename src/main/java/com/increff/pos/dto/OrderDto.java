package com.increff.pos.dto;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
@Service
public class OrderDto {

	@Autowired
	private OrderService orderservice;

	@Autowired
	private ProductService productservice;

	@Autowired
	private InventoryService inventoryservice;


// Creating order
	@Transactional(rollbackOn = ApiException.class)
	public OrderData createOrder(List<OrderItemForm> forms) throws ApiException {
		List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();
		Set<String> hash_Set = new HashSet<String>();
		for (OrderItemForm f : forms) {
			normalize(f.getProductBarcode());
			if(hash_Set.contains(f.getProductBarcode())){

				throw new ApiException("Cannot add same barcode again");
			}
			hash_Set.add(f.getProductBarcode());

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



		for (OrderItemPojo oip : orderItemList) {
			ProductPojo productPojo = productservice.findProduct(oip.getOrderProductId());
			if (productPojo == null) {
				throw new ApiException("Product does not exist.");
			}
			updateInventory(oip);
		}

		Integer orderId = orderservice.createOrder(orderItemList);
		Double total = orderservice.billTotal(orderId);
		return convertOrderPojo(orderservice.getOrder(orderId), total);
	}

	@Transactional
	public String createInvoice(Integer orderId) throws Exception {
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
			{ billData.setDatetime(new SimpleDateFormat("MM-dd-yyyy HH:mm:ss aa").format(new Date()));
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

	// Fetching an Order item by orderItemId
	@Transactional
	public OrderItemData getOrderItemDetails(Integer id) throws ApiException {
		checkIfExists(id);
		OrderItemPojo orderItemPojo = orderservice.get(id);
		Integer i = orderItemPojo.getOrderProductId();
		ProductPojo productpojo = productservice.findProduct(i);
		OrderPojo orderPojo = orderservice.getOrder(orderItemPojo.getOrderId());
		InventoryPojo inventorypojo = inventoryservice.findInventory(productpojo.getProductId());
		return convert(orderItemPojo, productpojo, orderPojo, inventorypojo);

	}

	//gets the list of all order items
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

	//gets the list of all orders
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

	//gets the list of orderitems in a particular order
	@Transactional
	public List<OrderItemData> getOrderItemByOrderId(Integer id) throws ApiException {
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

// Fetching all order items
	@Transactional
	public List<OrderItemPojo> getAll() {
		return orderservice.getAll();
	}

	//deleting an order item
	@Transactional
	public void delete(Integer id) throws ApiException {
		OrderItemData orderItemData= getOrderItemDetails(id);
		Integer orderId=orderItemData.getId();
		List<OrderItemData> orderItemDataList=getOrderItemByOrderId(orderId);
		if(orderItemDataList.size()==1)
		{
			throw new ApiException("Order Item cannot be deleted as minimun 1 order is required");
		}

		increaseInventory(orderservice.delete(id));
	}

	// Deletion of order
	@Transactional
	public void deleteOrder(Integer orderId) throws ApiException {

		List<OrderItemPojo> orderItemList = orderservice.getOrderItems(orderId);
		for (OrderItemPojo orderItemPojo : orderItemList) {
			increaseInventory(orderItemPojo);
			orderservice.deleteOrder(orderItemPojo);
		}
		orderservice.deleteOrderhelper(orderId);
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateOrderItem(Integer id, OrderItemForm f) throws ApiException {

		OrderItemPojo oip=orderservice.get(id);
		if(oip==null)
		{
			throw new ApiException("OrderItem Id does not exist");
		}
		ProductPojo p=productservice.findProduct(oip.getOrderProductId());



		ProductPojo productPojo = productservice.findProduct(f.getProductBarcode());



		if(productPojo==null)
		{
			throw new ApiException("invalid barcode");
		}

		if(!p.getProductBarcode().equals(f.getProductBarcode()))
		{
			throw new ApiException("Barcode is not Editable");
		}

		//orderItem pojo that is received through edit form
		OrderItemPojo orderItemPojo = convertOrderItemForm(f, productPojo);

		//orderItem pojo that is get from backend through id





		//quantity that is present in the order already
		 Integer quantity=oip.getOrderQuantity();



		Integer orderId=oip.getOrderId();
		OrderPojo op=orderservice.getOrder(orderId);

		if(op.isInvoiced()==true)
		{
			throw new ApiException("Order Item can't be edited");
		}
		update(id, orderItemPojo, quantity);
	}

	// Updating order item
	@Transactional(rollbackOn = ApiException.class)
	public void update(Integer id, OrderItemPojo p,Integer quantity) throws ApiException {
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
		Integer updatedQuantity = orderservice.updateInventory(inventoryPojo, orderItemPojo);
		inventoryPojo.setProductQuantity(updatedQuantity);
		inventoryservice.updateInventory(inventoryPojo);
	}

	// Checking if a particular order item exists or not
	@Transactional(rollbackOn = ApiException.class)
	public OrderItemPojo checkIfExists(Integer id) throws ApiException {
		OrderItemPojo p = orderservice.checkIfExists(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

	// Checking if a particular order exists or not
	@Transactional(rollbackOn = ApiException.class)
	public OrderPojo checkIfExistsOrder(Integer id) throws ApiException {
		OrderPojo p = orderservice.checkIfExistsOrder(id);
		if (p == null) {
			throw new ApiException("Order Item with given ID does not exist, id: " + id);
		}
		return p;
	}

//

	// Validation of order item
	private void validateUpdate(OrderItemPojo p,Integer quantity) throws ApiException {

		boolean flag=true;

		Integer moduloQuantity=0;
		if(p.getOrderQuantity()<=quantity)
		{
			flag=false;
			moduloQuantity=quantity-p.getOrderQuantity();
		}
		else{
			moduloQuantity=p.getOrderQuantity()-quantity;
		}



		if (p.getOrderQuantity() <= 0) {
			throw new ApiException("Quantity must be greater than or equal to 0");
		}else if (inventoryservice.getInventory(p.getOrderProductId()).getProductQuantity() < moduloQuantity && flag==true) {
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
		barcode.toLowerCase().trim();
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
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String datetime = dateFormat.format(pojo.getDate());
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
