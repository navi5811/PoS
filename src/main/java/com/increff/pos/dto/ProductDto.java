package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.increff.pos.model.ProductData;
import com.increff.pos.model.ProductForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;

@Component
public class ProductDto {
	
	@Autowired
	private ProductService productservice;
	@Autowired
	private BrandService brandservice;
	@Autowired
	private InventoryService inventoryservice;

	//adds a product
	@Transactional(rollbackOn = ApiException.class)
	public void addProduct(ProductForm f) throws ApiException {
		normalizeProduct(f);
		ProductPojo p = convert(f);
		validateProduct(p);
		productservice.addProduct(p);
		InventoryPojo inv = new InventoryPojo(p.getProductId(), 0);
		inventoryservice.addInventory(inv);
	}

	//gets the list of all the prducts in the database
	@Transactional
	public List<ProductData> getAllProduct() throws ApiException {

		List<ProductPojo> list = productservice.getAllProduct();
		List<ProductData> list2 = new ArrayList<ProductData>();
		for (ProductPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	//updates a product or say edit a product
	@Transactional(rollbackOn = ApiException.class)
	public void updateProduct(Integer id, ProductForm f) throws ApiException {

		normalizeProduct(f);
		ProductPojo p = convert(f);
		validateEditProduct(p);
		ProductPojo ex = productservice.findProduct(id);
		if(!ex.getProductBarcode().equals(f.getProductBarcode()))
		{
			throw new ApiException("Barcode is not editable ");
		}
		if(!ex.getProductName().equals(f.getProductName()))
		{
			throw new ApiException("Product Name is not editable");
		}
		ex.setProductName(p.getProductName());
		ex.setProductBrandCategory(p.getProductBrandCategory());
		ex.setProductBarcode(p.getProductBarcode());
		ex.setProductMrp(p.getProductMrp());

		productservice.updateProduct(id, ex);
	}


	//finds a product with given id
	@Transactional
	public ProductData findProduct(Integer id) throws ApiException {
		ProductPojo p = productservice.findProduct(id);
		
		if (p == null) {
			throw new ApiException("Product with given ID does not exist, id: " + id);
		}
		ProductData pd=convert(p);
		return pd;
	}

	//finds a product with given barcode
	@Transactional
	public ProductData findProduct(String barcode) throws ApiException {
		ProductPojo p = productservice.findProduct(barcode);
		if (p == null) {
			throw new ApiException("Product with given barcode does not exist, Barcode: " + barcode);
		}
		ProductData pd=convert(p);
		return pd;
	}


	//normalisation of product
	protected static void normalizeProduct(ProductForm p) {
		p.setProductName(p.getProductName().toLowerCase().trim());
		p.setProductBarcode(p.getProductBarcode().toLowerCase().trim());
		p.setProductBrandName(p.getProductBrandName().toLowerCase().trim());
		p.setProductBrandCategoryName(p.getProductBrandCategoryName().toLowerCase().trim());
	}

	//validations for editing product
	protected void validateEditProduct(ProductPojo p) throws ApiException {

		if(p.getProductBarcode().length()>15)
		{
			throw new ApiException("Barcode length cannot be greater than 15");
		}
		if(p.getProductName().length()>15)
		{
			throw new ApiException("Product name cannot be so long");
		}
		if(p.getProductMrp().intValue()>1000000)
		{
			throw new ApiException("Mrp cannot be greater than 1000000");
		}

		if (StringUtil.isEmpty(p.getProductName())) {
			throw new ApiException("Product Name cannot be empty");
		}

		if (StringUtil.isEmpty(p.getProductBarcode())) {
			throw new ApiException("Product Barcode cannot be empty");
		}

		if (p.getProductMrp() == null) {
			throw new ApiException("Product Mrp cannot be null");
		}

		if (p.getProductMrp()<=0.0) {
			throw new ApiException("Product Mrp cannot be less than or equal to 0");
		}
		ProductPojo check = productservice.find(p.getProductName(), p.getProductBrandCategory());


	}

	//validations for adding a product
	protected void validateProduct(ProductPojo p) throws ApiException {

		if(p.getProductBarcode().length()>15)
		{
			throw new ApiException("Barcode length cannot be greater than 15");
		}
		if(p.getProductName().length()>15)
		{
			throw new ApiException("Product name cannot be so long");
		}
		if(p.getProductMrp().intValue()>1000000)
		{
			throw new ApiException("Mrp cannot be greater than 1000000");
		}

		if (StringUtil.isEmpty(p.getProductName())) {
			throw new ApiException("Product Name cannot be empty");
		}

		if (StringUtil.isEmpty(p.getProductBarcode())) {
			throw new ApiException("Product Barcode cannot be empty");
		}

		if (p.getProductMrp() == null) {
			throw new ApiException("Product Mrp cannot be null");
		}

		if (p.getProductMrp()<=0.0) {
			throw new ApiException("Product Mrp cannot be less than or equal to 0");
		}

		if (productservice.findProduct(p.getProductBarcode()) != null) {
			throw new ApiException("Product with the same Barcode already exists");
		}

//		 To check if Product already exists with a different barcode
			ProductPojo check = productservice.find(p.getProductName(), p.getProductBrandCategory());

		if ((check != null) && check.getProductBarcode() == p.getProductBarcode()) {
			throw new ApiException("The given product already exists in the Database");
		}

		if ((check != null) && check.getProductBarcode() != p.getProductBarcode()) {
			throw new ApiException("The given product already exists with a different Barcode");
		}
	}

	//conversion of product pojo to product data
	public ProductData convert(ProductPojo p) throws ApiException {
		ProductData d = new ProductData();

		d.setProductBarcode(p.getProductBarcode());
		d.setProductName(p.getProductName());
		d.setProductId(p.getProductId());
		d.setProductMrp(p.getProductMrp());

		Integer productBrandId = p.getProductBrandCategory();
		BrandPojo brandPojo = brandservice.getBrand(productBrandId);
		if(brandPojo==null)
		{
			throw new ApiException("corresponding brand category does not exist");
		}
		d.setProductBrandCategoryName(brandPojo.getBrandCategory());
		d.setProductBrandName(brandPojo.getBrandName());

		return d;
	}

	//conversion of product form to porduct pojo
	public ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();

		if(f.getProductMrp()==null)
		{
			throw new ApiException("MRP cannot be null");
		}
		if (StringUtil.isEmpty(f.getProductBrandName())) {
			throw new ApiException("Product must belong to a brand, Brand name field cannot be empty");
		}

		if (StringUtil.isEmpty(f.getProductBrandCategoryName())) {
			throw new ApiException("Product must belong to a category, Category name field cannot be empty");
		}

		String toFindBrandName = StringUtil.toLowerCase(f.getProductBrandName());
		String toFindBrandCategoryName = StringUtil.toLowerCase(f.getProductBrandCategoryName());

		BrandPojo foundBrand = brandservice.findBrand(toFindBrandName, toFindBrandCategoryName);

		if(foundBrand==null)
		{
			throw new ApiException("Can't find brand-category combination");
		}
		Integer foundBrandId = foundBrand.getBrandId();

		p.setProductBrandCategory(foundBrandId);
		p.setProductName(f.getProductName());
		p.setProductMrp(f.getProductMrp());
		p.setProductBarcode(f.getProductBarcode());

		return p;
	}

}
