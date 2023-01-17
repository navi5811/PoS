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
		dao.insert(p);
	}

	@Transactional
	public void deleteInventory(int id) {
		dao.delete(id);
	}

	@Transactional(rollbackOn = ApiException.class)
	public InventoryPojo getInventory(int productId) throws ApiException {
		return dao.select(productId);
	}

	
	@Transactional
	public List<InventoryPojo> getAllInventory() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateInventory(InventoryPojo p) throws ApiException {
	
		InventoryPojo ex = dao.select(p.getProductId());

		ex.setProductQuantity(p.getProductQuantity());

		dao.update(ex);
	}

	@Transactional
	public InventoryPojo findInventory(int id) throws ApiException {
		InventoryPojo p = dao.select(id);
		return p;
	}


}
