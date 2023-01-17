package com.increff.employee.dao;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.employee.pojo.BrandPojo;
import com.increff.employee.pojo.OrderItemPojo;
import com.increff.employee.pojo.OrderPojo;

@Repository
public class OrderItemDao extends AbstractDao {
	
	private static String delete_id = "delete from OrderItemPojo p where orderItemId=:id";
	private static String select_id = "select p from OrderItemPojo p where orderItemId=:id";

	
	
	public void delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	public OrderItemPojo select(int id) {
		TypedQuery<OrderItemPojo> query = getQuery(select_id, OrderItemPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}
	
	public void update(OrderItemPojo p) {
	}
	
	
	@Transactional
	public void insert(OrderItemPojo p) {
		em.persist(p);
	}
	// Select all OrderItems
	public List<OrderItemPojo> selectAll() {
		TypedQuery<OrderItemPojo> query = getQuery("select p from OrderItemPojo p",
				OrderItemPojo.class);
		return query.getResultList();
	}

	// Select all OrderItems of particular order
	public List<OrderItemPojo> selectOrder(int orderId) {
		TypedQuery<OrderItemPojo> query = getQuery("select p from OrderItemPojo p where orderId=:orderId",
				OrderItemPojo.class);
		query.setParameter("orderId", orderId);
		return query.getResultList();
	}
}