package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.InventoryDao;
import com.increff.pos.pojo.InventoryPojo;

@Service
public class InventoryService {

	@Autowired
	private InventoryDao dao;

	@Transactional(rollbackOn = ApiException.class)
	public void addInventory(InventoryPojo p) {
		dao.insert(p);
	}

	@Transactional(rollbackOn = ApiException.class)
	public InventoryPojo getInventory(Integer productId) {
		return dao.select(productId);
	}

	@Transactional
	public List<InventoryPojo> getAllInventory() {
		return dao.selectAll();
	}

	@Transactional(rollbackOn = ApiException.class)
	public void updateInventory(InventoryPojo p) {
		InventoryPojo ex = dao.select(p.getProductId());
		ex.setProductQuantity(p.getProductQuantity());
		dao.update(ex);
	}

	@Transactional
	public InventoryPojo findInventory(Integer id) throws ApiException {
		InventoryPojo p = dao.select(id);
		return p;
	}

}
