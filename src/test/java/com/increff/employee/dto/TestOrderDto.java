package com.increff.employee.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.employee.controller.OrderApiController;
import com.increff.employee.model.BrandForm;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.service.AbstractUnitTest;
import com.increff.employee.service.ApiException;

public class TestOrderDto extends AbstractUnitTest {

	@Autowired
	private BrandDto branddto;

	@Autowired
	private InventoryDto inventorydto;

	@Autowired
	private ProductDto productdto;

	@Autowired
	private OrderDto orderdto;

	private static Logger logger = (Logger) LogManager.getLogger(TestOrderDto.class);

	@Test
	public void testgetAllorderItems() throws ApiException {

		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;
		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);
		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// order created

		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		List<OrderItemData> oid = orderdto.getAllOrderItems(orderItemPojo_list);
		assertEquals(oid.get(0).getProductBarcode(), barcode);
		assertEquals(oid.get(0).getProductQuantity(), quantity);
		assertEquals(oid.get(0).getProductSellingPrice(), sellingPrice);

	}

	@Test
	public void testgetAllorders() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;
		Double totalAmount = sellingPrice * quantity;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);
		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// order created

		List<OrderData> orderDataList = orderdto.getAllOrders();
		assertEquals(orderDataList.get(0).getBillAmount(), totalAmount);
	}

	@Test
	public void testgetOrderItemsByOrderId() throws ApiException {

		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);

		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);

		List<OrderData> orderDataList = orderdto.getAllOrders();
		int orderId = orderDataList.get(0).getOrderId();
		List<OrderItemData> orderItemsList = orderdto.getOrderItemByOrderId(orderId);

		assertEquals(orderItemsList.get(0).getProductBarcode(), barcode);
		assertEquals(orderItemsList.get(0).getMrp(), mrp);
		assertEquals(orderItemsList.get(0).getProductQuantity(), quantity);
		assertEquals(orderItemsList.get(0).getProductSellingPrice(), sellingPrice);
		assertEquals(orderItemsList.get(0).getName(), productname);

	}

	// Fetching an Order item by orderItem id
	@Test
	public void testgetOrderItemById() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);

		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// orderCreated

		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		List<OrderItemData> oid = orderdto.getAllOrderItems(orderItemPojo_list);
		int oderItemId = oid.get(0).getOrderItemId();
		OrderItemData data = orderdto.getOrderItemDetails(oderItemId);

		assertEquals(data.getProductBarcode(), barcode);
		assertEquals(data.getMrp(), mrp);
		assertEquals(data.getProductQuantity(), quantity);
		assertEquals(data.getProductSellingPrice(), sellingPrice);
		assertEquals(data.getName(), productname);
	}

	@Test
	public void testDeleteOrder() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);

		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// orderCreated

		List<OrderData> orderDataList = orderdto.getAllOrders();

		// checking that order exists in the orderlist
		if (orderDataList.size() == 0) {
			return;
		}

		int orderId = orderDataList.get(0).getOrderId();

		orderdto.deleteOrder(orderId);
		List<OrderData> neworderDataList = orderdto.getAllOrders();

		// checking that after deleting order list is empty
		if (neworderDataList.size() == 0)
			return;
		fail();

	}

	@Test
	public void testUpdate() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);

		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// orderCreated

		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		List<OrderItemData> oid = orderdto.getAllOrderItems(orderItemPojo_list);
		int orderItemId = oid.get(0).getOrderItemId();
		barcode = "barcode";
		quantity = 6;
		sellingPrice = 55.0;

		OrderItemForm newForm = new OrderItemForm();
		newForm.setProductBarcode(barcode);
		newForm.setProductQuantity(quantity);
		newForm.setProductSellingPrice(sellingPrice);
		logger.error("orderItem id is ", orderItemId);

		orderdto.updateOrderItem(orderItemId, newForm);

		OrderItemData orderItemData = orderdto.getOrderItemDetails(orderItemId);

		assertEquals(orderItemData.getProductBarcode(), barcode);
		assertEquals(orderItemData.getProductQuantity(), quantity);
		assertEquals(orderItemData.getProductSellingPrice(), sellingPrice);
	}

	@Test
	public void testDeleteOrderItem() throws ApiException {

		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		InventoryForm inventoryform = new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);

		Date date = new Date();
		OrderPojo op = new OrderPojo();
		op.setDate(date);
		Integer quantity = 5;
		Double sellingPrice = 50.0;

		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);

		List<OrderItemForm> orderItemForm = new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		// orderCreated
		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		List<OrderItemData> oid = orderdto.getAllOrderItems(orderItemPojo_list);
		int orderItemId = oid.get(0).getOrderItemId();
		orderdto.delete(orderItemId);

		try {
			orderdto.getOrderItemDetails(orderItemId);
		} catch (ApiException e) {
			return;
		}
		fail();

	}

}
