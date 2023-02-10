package com.increff.pos.dto;


import com.increff.pos.dto.BrandDto;
import com.increff.pos.dto.ProductDto;
import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.AbstractUnitTest;
import com.increff.pos.service.ApiException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestProductDto extends AbstractUnitTest {

	@Autowired
	private ProductDto productdto;

	@Autowired
	private BrandDto branddto;

	@Test
	public void testaddproduct() throws ApiException {

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


		List<ProductData> list = productdto.getAllProduct();
		assertEquals(list.get(0).getProductBarcode(), barcode);
		assertEquals(list.get(0).getProductBrandName(), brandname);
		assertEquals(list.get(0).getProductBrandCategoryName(), brandcategory);
		assertEquals(list.get(0).getProductName(), productname);
		assertEquals(list.get(0).getProductMrp(), mrp);

		f.setProductBrandName("");
		try{
			productdto.addProduct(f);
		}catch (ApiException e)
		{
			return;
		}
		fail();
	}

//	@Test
//	public void testdeleteproduct() throws ApiException {
//
//		String barcode = "barcode";
//		String brandname = "name";
//		String brandcategory = "category";
//		String productname = "pname";
//		Double mrp = 800.0;
//
//		BrandForm brandform = new BrandForm();
//		brandform.setBrandName(brandname);
//		brandform.setBrandCategory(brandcategory);
//
//		branddto.addBrand(brandform);
//		ProductForm f = new ProductForm();
//		f.setProductBarcode(barcode);
//		f.setProductBrandName(brandname);
//		f.setProductBrandCategoryName(brandcategory);
//		f.setProductName(productname);
//		f.setProductMrp(mrp);
//		productdto.addProduct(f);
//
//		List<ProductData> list = productdto.getAllProduct();
//		int id = list.get(0).getProductId();
//
//		productdto.deleteProduct(id);
//
//		try {
//			productdto.findProduct(id);
//		} catch (ApiException e) {
//			return;
//		}
//		fail();
//	}

	@Test
	public void testFindProductById() throws ApiException {
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
		List<ProductData> list = productdto.getAllProduct();

		// Verifying the getById operation
		ProductForm form = productdto.findProduct(list.get(0).getProductId());
		assertEquals(form.getProductBarcode(), barcode);
		assertEquals(form.getProductBrandCategoryName(), brandcategory);
		assertEquals(form.getProductName(), productname);
		assertEquals(form.getProductBrandName(), brandname);
		assertEquals(form.getProductMrp(), mrp);

		// now if we want to check exception in find product
		// choosing random product id
		String productbarcode = "abs";
		try {
			productdto.findProduct(productbarcode);
		} catch (ApiException e) {
			return;
		}
		fail();

	}

	@Test
	public void testFindProductByBarcode() throws ApiException {
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
		List<ProductData> list = productdto.getAllProduct();

		// Verifying the getById operation
		ProductForm form = productdto.findProduct(list.get(0).getProductBarcode());
		assertEquals(form.getProductBarcode(), barcode);
		assertEquals(form.getProductBrandCategoryName(), brandcategory);
		assertEquals(form.getProductBrandName(), brandname);
		assertEquals(form.getProductName(), productname);
		assertEquals(form.getProductMrp(), mrp);
	}

	@Test
	public void testUpdate() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";
		Double mrp = 800.0;

		// adding brand
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

		List<ProductData> list = productdto.getAllProduct();
		int id = list.get(0).getProductId();

		// Verifying the Update function
		barcode = "barcodechanged";
		brandname = "namechanged";
		brandcategory = "categorychanged";
		productname = "pnamechanged";

		mrp = 900.0;

		BrandForm newbrandform = new BrandForm();
		newbrandform.setBrandName(brandname);
		newbrandform.setBrandCategory(brandcategory);
		branddto.addBrand(newbrandform);

		ProductForm p = new ProductForm();
		p.setProductBarcode(barcode);
		p.setProductBrandName(brandname);
		p.setProductBrandCategoryName(brandcategory);
		p.setProductName(productname);
		p.setProductMrp(mrp);

		productdto.updateProduct(id, p);
		List<ProductData> nlist = productdto.getAllProduct();
		assertEquals(nlist.get(0).getProductBarcode(), barcode);
		assertEquals(nlist.get(0).getProductBrandCategoryName(), brandcategory);
		assertEquals(nlist.get(0).getProductBrandName(), brandname);
		assertEquals(nlist.get(0).getProductName(), productname);
		assertEquals(nlist.get(0).getProductMrp(), mrp);
	}

	@Test
	public void testValidateProduct() throws ApiException {

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

		//checking if barcode is empty or not
		f.setProductBarcode("");

		boolean check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}

		//checked if product name is empty or not
		f.setProductName("");
		check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}
		//check if category is empty or not
		f.setProductBrandCategoryName("");
		check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}

	}

	@Test
	public void testAnotherValidation() throws ApiException {
		String barcode = "barcode";
		String brandname = "name";
		String brandcategory = "category";
		String productname = "pname";

		BrandForm brandform = new BrandForm();
		brandform.setBrandName(brandname);
		brandform.setBrandCategory(brandcategory);

		branddto.addBrand(brandform);
		ProductForm f = new ProductForm();
		f.setProductBarcode(barcode);
		f.setProductBrandName(brandname);
		f.setProductBrandCategoryName(brandcategory);
		f.setProductName(productname);


		//null mrp
		boolean check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}

		check=false;

		//negative mrp
		Double mrp= -50.0D;
		f.setProductMrp(mrp);
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}


		mrp=50.0;
		f.setProductMrp(mrp);
		productdto.addProduct(f);

		//product already exist
		check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}


		//product already exist with different barcode
		barcode="changedbarcode";
		f.setProductBarcode(barcode);

		check=false;
		try {
			productdto.addProduct(f);
		}catch (ApiException e){
			check=true;
		}
		if(check==false) {fail();}
//
//
//		brandname="newname";
//		brandcategory="newcategory";
//		barcode = "barcode";
//		f.setProductBrandName(brandname);
//		f.setProductBrandCategoryName(brandcategory);
//		f.setProductBarcode(barcode);
//
//		check=false;
////		try {
////			productdto.addProduct(f);
////		}catch (ApiException e){
////			check=true;
////		}
////		if(check==false) {fail();}

	}







}
