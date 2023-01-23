package com.increff.employee.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Objects;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.employee.model.BrandForm;
import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.service.AbstractUnitTest;
import com.increff.employee.service.ApiException;

public class TestInventoryDto extends AbstractUnitTest {

	@Autowired
	private ProductDto productdto;

	@Autowired
	private BrandDto branddto;

	@Autowired
	private InventoryDto inventorydto;

	@Test
	public void testGetAllInventory() throws ApiException {
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
		Integer quantity = 0;
		List<InventoryData> list = inventorydto.getAllInventory();

		assertEquals(list.get(0).getProductQuantity(), quantity);
		assertEquals(list.get(0).getInventoryProductBarcode(), barcode);
		assertEquals(list.get(0).getBrandName(), brandname);
		assertEquals(list.get(0).getBrandCategory(), brandcategory);
		assertEquals(list.get(0).getProductName(), productname);
	}

	@Test
	public void testGetInventory() throws ApiException {
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
		Integer quantity = 0;
		List<InventoryData> list = inventorydto.getAllInventory();
		InventoryForm form = inventorydto.getInventory(list.get(0).getInventoryProductBarcode());
		assertEquals(form.getInventoryProductBarcode(), barcode);
		assertEquals(form.getProductQuantity(), quantity);
	}
	
	@Test
	public void testUpdateInventory() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;
		Integer quantity = 0;

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
		
		quantity=5;
		
		InventoryForm p=new InventoryForm();
		p.setInventoryProductBarcode(barcode);
		p.setProductQuantity(quantity);
		
		inventorydto.updateInventory(p);
		
		List<InventoryData> list = inventorydto.getAllInventory();
		
		InventoryData form = inventorydto.getInventory(list.get(0).getInventoryProductBarcode());
		assertEquals(form.getInventoryProductBarcode(), barcode);
		assertEquals(form.getProductQuantity(), quantity);
	}
	@Test
	public void testFindInventory() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;
		Integer quantity = 0;
		
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
		
		List<ProductData> productlist=productdto.getAllProduct();
		int id = productlist.get(0).getProductId();
		InventoryForm form = inventorydto.findInventory(id);
		assertEquals(form.getInventoryProductBarcode(), barcode);
		assertEquals(form.getProductQuantity(), quantity);
	}
	@Test
	public void testValidateInventory() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;
		Integer quantity = 0;

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
		
		quantity=-5;
		boolean flag=false;
		InventoryForm p=new InventoryForm();
		p.setInventoryProductBarcode(barcode);
		p.setProductQuantity(quantity);
		try {
		inventorydto.updateInventory(p);
		}catch(ApiException e){
			flag=true;
		}
		if(Objects.equals(flag,false))fail();
		
		
		InventoryForm invf=new InventoryForm();
		invf.setInventoryProductBarcode(barcode);
		try {
			inventorydto.updateInventory(invf);
			}catch(ApiException e){
				return;
			}
		fail();
	}

}
