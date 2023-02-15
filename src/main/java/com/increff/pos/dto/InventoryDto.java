package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.InventoryData;
import com.increff.pos.model.InventoryForm;
import com.increff.pos.model.ProductData;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.InventoryService;
import com.increff.pos.service.ProductService;
import com.increff.pos.util.StringUtil;

@Component
public class InventoryDto {

	@Autowired
	private InventoryService inventoryservice;
	@Autowired
	private ProductService productservice;
	@Autowired
	private BrandService brandService;

	@Transactional(rollbackOn = ApiException.class)
	public InventoryData getInventory(String inventoryProductBarcode) throws ApiException {
		if (StringUtil.isEmpty(inventoryProductBarcode)) {
			throw new ApiException("Please enter a valid barcode, Barcode field cannot be empty");
		}
		ProductPojo pp=productservice.findProduct(inventoryProductBarcode);
		if (pp == null) {
			throw new ApiException("Product with given barcode does not exist");
		}
		InventoryPojo ip = inventoryservice.getInventory(pp.getProductId());
		BrandPojo bp=brandService.getBrand(pp.getProductBrandCategory());
		InventoryData id = convert(ip);
		id.setMrp(pp.getProductMrp());
		id.setProductName(pp.getProductName());
		id.setBrandName(bp.getBrandName());
		id.setBrandCategory(bp.getBrandCategory());
		return id;
	}

	@Transactional
	public List<InventoryData> getAllInventory() throws ApiException {
		List<InventoryPojo> list = inventoryservice.getAllInventory();
		List<InventoryData> list2 = new ArrayList<InventoryData>();
		for (InventoryPojo p : list) {
			InventoryData d = convert(p);
			ProductPojo productpojo = productservice.findProduct(d.getInventoryProductBarcode());

			BrandPojo bd = brandService.getBrand(productpojo.getProductBrandCategory());
			if(bd==null)
			{
				throw new ApiException("Brand with given id does not exist" + productpojo.getProductBrandCategory());
			}
			d.setProductName(productpojo.getProductName());
			d.setBrandName(bd.getBrandName());
			d.setBrandCategory(bd.getBrandCategory());
			d.setMrp(productpojo.getProductMrp());
			list2.add(d);
		}
		return list2;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateInventory(InventoryForm f) throws ApiException {

		System.out.println(f);
		validateInventory(f);
		InventoryPojo p = convert(f);
		InventoryPojo ex = inventoryservice.getInventory(p.getProductId());
		ex.setProductQuantity(p.getProductQuantity());
		inventoryservice.updateInventory(ex);
	}

	@Transactional
	public InventoryData findInventory(Integer id) throws ApiException {
		InventoryPojo p = inventoryservice.getInventory(id);
		if (p == null) {
			throw new ApiException("Inventory with given ID does not exit, id: " + id);
		}
		InventoryData i = convert(p);
		return i;
	}

	protected static void validateInventory(InventoryForm p) throws ApiException {

		if(StringUtil.isEmpty(p.getInventoryProductBarcode()))
		{
			throw new ApiException("Barcode field cannot be empty");
		}
		if (p.getProductQuantity()==null) {
			throw new ApiException("Quantity cannot be null");
		}

		if(p.getProductQuantity()>1000000)
		{
			throw new ApiException("Quantity cannot be greater than 1000000");
		}

		if (p.getProductQuantity() < 0) {
			throw new ApiException("Quantity cannot be less than 0");
		}

	}

	public InventoryData convert(InventoryPojo p) throws ApiException {
		InventoryData d = new InventoryData();

		ProductPojo prod = productservice.findProduct(p.getProductId());

		d.setProductId(prod.getProductId());
		d.setInventoryProductBarcode(prod.getProductBarcode());
		d.setProductQuantity(p.getProductQuantity());

		return d;
	}

	public InventoryPojo convert(InventoryForm f) throws ApiException {
		InventoryPojo p = new InventoryPojo();

		if (StringUtil.isEmpty(f.getInventoryProductBarcode())) {
			throw new ApiException("Please enter a valid barcode, Barcode field cannot be empty");
		}
		ProductPojo prod = productservice.findProduct(f.getInventoryProductBarcode());

		if(prod==null)
		{
			throw new ApiException("product with given barcode does not exist");
		}
		p.setProductId(prod.getProductId());
		p.setProductQuantity(f.getProductQuantity());
		return p;
	}

}
