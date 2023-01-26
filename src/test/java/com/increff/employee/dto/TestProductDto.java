package com.increff.employee.dto;

import com.increff.employee.service.ApiException;
import com.increff.employee.dao.BrandDao;
import com.increff.employee.dao.ProductDao;
import com.increff.employee.model.BrandData;
import com.increff.employee.model.BrandForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.model.ProductForm;
import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.AbstractUnitTest;
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

	@Autowired
	private ProductDao productdao;

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
	}

	@Test
	public void testdeleteproduct() throws ApiException {

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
		int id = list.get(0).getProductId();

		productdto.deleteProduct(id);

		try {
			productdto.findProduct(id);
		} catch (ApiException e) {
			return;
		}
		fail();
	}

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

}
