package com.increff.pos.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.OrderPojo;

@Repository
public class OrderDao extends AbstractDao {

	
	private static String select_id = "select p from OrderPojo p where orderId=:id";
	private static String delete_id = "delete from OrderPojo p where orderId=:id";
	@Transactional
	public void insert(OrderPojo p) {
		em.persist(p);
	}
	
	public void delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		query.executeUpdate();
	}

	
	public OrderPojo select(int id) {
		TypedQuery<OrderPojo> query = getQuery(select_id, OrderPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}
	
	public List<OrderPojo> selectAll() {
		TypedQuery<OrderPojo> query = getQuery("select p from OrderPojo p", OrderPojo.class);
		return query.getResultList();
	}

	// Select orders between dates
	public List<OrderPojo> selectBetweenDate(Date startDate, Date endDate) {
		TypedQuery<OrderPojo> query = getQuery("select p from OrderPojo p where date between :start and :end",
				OrderPojo.class);
		query.setParameter("start", startDate);
		query.setParameter("end", endDate);
		return query.getResultList();
	}

	public List<OrderPojo> selectInvoicedDate(Date startDate,Date endDate) {
		TypedQuery<OrderPojo> query = getQuery("select p from OrderPojo p where invoicedTime between :start and :end ",
				OrderPojo.class);
		query.setParameter("start", startDate);
		query.setParameter("end", endDate);
		return query.getResultList();
	}
}
