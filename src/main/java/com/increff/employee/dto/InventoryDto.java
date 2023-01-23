package com.increff.employee.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.model.BrandData;
import com.increff.employee.model.InventoryData;
import com.increff.employee.model.InventoryForm;
import com.increff.employee.model.ProductData;
import com.increff.employee.pojo.InventoryPojo;
import com.increff.employee.pojo.ProductPojo;
import com.increff.employee.service.ApiException;
import com.increff.employee.service.InventoryService;
import com.increff.employee.service.ProductService;
import com.increff.employee.util.StringUtil;

@Service
public class InventoryDto {

	@Autowired
	private InventoryService inventoryservice;
	@Autowired
	private ProductService productservice;
	@Autowired
	private ProductDto productdto;
	@Autowired
	private BrandDto branddto;

	@Transactional(rollbackOn = ApiException.class)
	public InventoryData getInventory(String inventoryProductBarcode) throws ApiException {
		ProductData pp = productdto.findProduct(inventoryProductBarcode);
		InventoryPojo ip = inventoryservice.getInventory(pp.getProductId());
		InventoryData id = convert(ip);
		return id;
	}

	@Transactional
	public List<InventoryData> getAllInventory() throws ApiException {
		List<InventoryPojo> list = inventoryservice.getAllInventory();
		List<InventoryData> list2 = new ArrayList<InventoryData>();
		for (InventoryPojo p : list) {
			InventoryData d = convert(p);
			ProductPojo productpojo = productservice.findProduct(d.getInventoryProductBarcode());

			BrandData bd = branddto.getBrand(productpojo.getProductBrandCategory());
			d.setProductName(productpojo.getProductName());
			d.setBrandName(bd.getBrandName());
			d.setBrandCategory(bd.getBrandCategory());
			list2.add(d);
		}
		return list2;
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateInventory(InventoryForm f) throws ApiException {
		InventoryPojo p = convert(f);
		validateInventory(p);
		InventoryPojo ex = inventoryservice.getInventory(p.getProductId());
		ex.setProductQuantity(p.getProductQuantity());
		inventoryservice.updateInventory(ex);
	}

	@Transactional
	public InventoryData findInventory(int id) throws ApiException {
		InventoryPojo p = inventoryservice.getInventory(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		InventoryData i = convert(p);
		return i;
	}

	protected static void validateInventory(InventoryPojo p) throws ApiException {

		if (p.getProductQuantity() == null) {
			throw new ApiException("Product quantity cannot be null");
		}

		if (p.getProductQuantity() < 0) {
			throw new ApiException("Product quantity cannot be less than 0");
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

		p.setProductId(prod.getProductId());
		p.setProductQuantity(f.getProductQuantity());
		return p;
	}

	public InventoryPojo convertip(InventoryData f) throws ApiException {
		InventoryPojo p = new InventoryPojo();

		if (StringUtil.isEmpty(f.getInventoryProductBarcode())) {
			throw new ApiException("Please enter a valid barcode, Barcode field cannot be empty");
		}
		ProductPojo prod = productservice.findProduct(f.getInventoryProductBarcode());

		p.setProductId(prod.getProductId());
		p.setProductQuantity(f.getProductQuantity());
		return p;
	}
}
