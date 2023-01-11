package com.increff.employee.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.employee.dao.InventoryDao;
import com.increff.employee.pojo.InventoryPojo;

@Service
public class InventoryService {

	@Autowired
	private InventoryDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addInventory(InventoryPojo p) throws ApiException {
		validateInventory(p);

		dao.insert(p);
	}

	@Transactional
	public void deleteInventory(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public InventoryPojo getInventorty(int id) throws ApiException {
		return dao.select(id);
	}

	@Transactional
	public List<InventoryPojo> getAllInventory() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateInventory(InventoryPojo p) throws ApiException {
		validateInventory(p);
		InventoryPojo ex = dao.select(p.getProductId());

		ex.setProductQuantity(p.getProductQuantity());

		dao.update(ex);
	}

	@Transactional
	public InventoryPojo findInventory(int id) throws ApiException {
		InventoryPojo p = dao.select(id);
		if (p == null) {
			throw new ApiException("Brand with given ID does not exit, id: " + id);
		}
		return p;
	}

	protected static void validateInventory(InventoryPojo p) throws ApiException {
		
		if(p.getProductQuantity()==null) {
			throw new ApiException("Product quantity cannot be null");
		}
		
		if(p.getProductQuantity()<0) {
			throw new ApiException("Product quantity cannot be less than 0");
		}
	}
}
