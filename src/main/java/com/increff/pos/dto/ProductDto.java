package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class ProductDto {
	
	@Autowired
	private ProductService productservice;
	@Autowired
	private BrandService brandservice;
	@Autowired
	private InventoryService inventoryservice;

	@Transactional(rollbackOn = ApiException.class)
	public void addProduct(ProductForm f) throws ApiException {

		ProductPojo p = convert(f);
		normalizeProduct(p);
		validateProduct(p);
		productservice.addProduct(p);
		InventoryPojo inv = new InventoryPojo(p.getProductId(), 0);
		inventoryservice.addInventory(inv);
		
	}
	@Transactional
	public void deleteProduct(int id) {
		productservice.deleteProduct(id);
	}
//
//	@Transactional(rollbackOn = ApiException.class)
//	public ProductData getProduct(int id) throws ApiException {
//
//		ProductPojo pp = productservice.findProduct(id);
//		if (pp == null) {
//			throw new ApiException("Product with given id does not exist, Barcode: " + id);
//		}
//		ProductData pd = convert(pp);
//		return pd;
//	}

	@Transactional
	public List<ProductData> getAllProduct() throws ApiException {

		List<ProductPojo> list = productservice.getAllProduct();
		List<ProductData> list2 = new ArrayList<ProductData>();
		for (ProductPojo p : list) {
			list2.add(convert(p));
		}
		return list2;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateProduct(int id, ProductForm f) throws ApiException {

		ProductPojo p = convert(f);
		normalizeProduct(p);

		validateEditProduct(p);
		ProductPojo ex = productservice.findProduct(id);

		ex.setProductName(p.getProductName());
		ex.setProductBrandCategory(p.getProductBrandCategory());
		ex.setProductBarcode(p.getProductBarcode());
		ex.setProductMrp(p.getProductMrp());

		productservice.updateProduct(id, ex);
	}

	@Transactional
	public ProductData findProduct(int id) throws ApiException {
		ProductPojo p = productservice.findProduct(id);
		
		if (p == null) {
			throw new ApiException("Product with given ID does not exist, id: " + id);
		}
		ProductData pd=convert(p);
		return pd;
	}

	@Transactional
	public ProductData findProduct(String barcode) throws ApiException {
		ProductPojo p = productservice.findProduct(barcode);
		if (p == null) {
			throw new ApiException("Product with given barcode does not exist, Barcode: " + barcode);
		}
		ProductData pd=convert(p);
		return pd;
	}

	protected static void normalizeProduct(ProductPojo p) {

		p.setProductName(StringUtil.toLowerCase(p.getProductName()));
		p.setProductBarcode(StringUtil.toLowerCase(p.getProductBarcode()));
	}

	protected void validateEditProduct(ProductPojo p) throws ApiException {

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

		if ((check != null) && check.getProductBarcode() == p.getProductBarcode()) {
			throw new ApiException("The given product already exists in the Database");
		}

		if ((check != null) && check.getProductBarcode() != p.getProductBarcode()) {
			throw new ApiException("The given product already exists with a different Barcode");
		}
	}
	protected void validateProduct(ProductPojo p) throws ApiException {

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

	public ProductData convert(ProductPojo p) throws ApiException {
		ProductData d = new ProductData();

		d.setProductBarcode(p.getProductBarcode());
		d.setProductName(p.getProductName());
		d.setProductId(p.getProductId());
		d.setProductMrp(p.getProductMrp());

		int productBrandId = p.getProductBrandCategory();
		BrandPojo brandPojo = brandservice.getBrand(productBrandId);
		if(brandPojo==null)
		{
			throw new ApiException("corresponding brand category does not exist");
		}
		d.setProductBrandCategoryName(brandPojo.getBrandCategory());
		d.setProductBrandName(brandPojo.getBrandName());

		return d;
	}

	public ProductPojo convert(ProductForm f) throws ApiException {
		ProductPojo p = new ProductPojo();

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
			throw new ApiException("can't find brand");
		}
		int foundBrandId = foundBrand.getBrandId();

		p.setProductBrandCategory(foundBrandId);
		p.setProductName(f.getProductName());
		p.setProductMrp(f.getProductMrp());
		p.setProductBarcode(f.getProductBarcode());

		return p;
	}
//	public ProductPojo convertdp(ProductData f) throws ApiException {
//		ProductPojo p=new ProductPojo();
//		
//
//		if (StringUtil.isEmpty(f.getProductBrandName())) {
//			throw new ApiException("Product must belong to a brand, Brand name field cannot be empty");
//		}
//
//		if (StringUtil.isEmpty(f.getProductBrandCategoryName())) {
//			throw new ApiException("Product must belong to a , Category name field cannot be empty");
//		}
//
//		String toFindBrandName = StringUtil.toLowerCase(f.getProductBrandName());
//		String toFindBrandCategoryName = StringUtil.toLowerCase(f.getProductBrandCategoryName());
//
//		BrandPojo foundBrand = brandservice.findBrand(toFindBrandName, toFindBrandCategoryName);
//
//		int foundBrandId = foundBrand.getBrandId();
//
//		p.setProductBrandCategory(foundBrandId);
//		p.setProductName(f.getProductName());
//		p.setProductMrp(f.getProductMrp());
//		p.setProductBarcode(f.getProductBarcode());
//		p.setProductId(f.getProductId());
//		return p;
//		
//	}
}
