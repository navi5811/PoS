package com.increff.employee.dto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.employee.model.BrandForm;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.model.OrderData;
import com.increff.employee.model.OrderItemData;
import com.increff.employee.model.OrderItemForm;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.AbstractUnitTest;
import com.increff.employee.service.ApiException;

public class TestOrderDto extends AbstractUnitTest{
	
	@Autowired
	private BrandDto branddto;
	
	@Autowired
	private InventoryDto inventorydto;
	
	@Autowired
	private ProductDto productdto;
	
	@Autowired
	private OrderDto orderdto;
	
	
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
		
		InventoryForm inventoryform=new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);
		
		Date date =new Date();
		OrderPojo op= new OrderPojo();
		op.setDate(date);
		Integer quantity=5;
		Double sellingPrice=50.0;
		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);
		List<OrderItemForm> orderItemForm=new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		//order created
		
		List<OrderItemPojo> orderItemPojo_list = orderdto.getAll();
		List<OrderItemData> oid=orderdto.getAllOrderItems(orderItemPojo_list);
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
		
		InventoryForm inventoryform=new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);
		
		Date date =new Date();
		OrderPojo op= new OrderPojo();
		op.setDate(date);
		Integer quantity=5;
		Double sellingPrice=50.0;
		Double totalAmount=sellingPrice*quantity;
		
		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);
		List<OrderItemForm> orderItemForm=new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		//order created
		
		List<OrderData> orderDataList=orderdto.getAllOrders();
		assertEquals(orderDataList.get(0).getBillAmount(),totalAmount);
	}
	
	@Test
	public void testgetOrderItemsById() throws ApiException {
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
		
		InventoryForm inventoryform=new InventoryForm();
		inventoryform.setInventoryProductBarcode(barcode);
		inventoryform.setProductQuantity(50);
		inventorydto.updateInventory(inventoryform);
		
		Date date =new Date();
		OrderPojo op= new OrderPojo();
		op.setDate(date);
		Integer quantity=5;
		Double sellingPrice=50.0;
		Double totalAmount=sellingPrice*quantity;
		
		OrderItemForm form = new OrderItemForm();
		form.setProductBarcode(barcode);
		form.setProductQuantity(quantity);
		form.setProductSellingPrice(sellingPrice);
		
		List<OrderItemForm> orderItemForm=new ArrayList<OrderItemForm>();
		orderItemForm.add(form);
		orderdto.createOrder(orderItemForm);
		
		
		
		
	}
	

}
