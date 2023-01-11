package com.increff.employee.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.employee.pojo.BrandPojo;

@Repository
public class BrandDao extends AbstractDao {

	private static String delete_id = "delete from BrandPojo p where brandId=:id";
	private static String select_id = "select p from BrandPojo p where brandId=:id";

	private static String select_brand = "select p from BrandPojo p where brandName=:brandName and brandCategory=:brandCategory";
	
	private static String select_all = "select p from BrandPojo p";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void insert(BrandPojo p) {
		em.persist(p);
	}

	public int delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public BrandPojo select(int id) {
		TypedQuery<BrandPojo> query = getQuery(select_id, BrandPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	public List<BrandPojo> selectAll() {
		TypedQuery<BrandPojo> query = getQuery(select_all, BrandPojo.class);
		return query.getResultList();
	}
	
	public BrandPojo select(String brandName, String brandCategory) {
		TypedQuery<BrandPojo> query = getQuery(select_brand, BrandPojo.class);
		query.setParameter("brandName", brandName);
		query.setParameter("brandCategory", brandCategory);
		return getSingle(query);
	}

	public void update(BrandPojo p) {
	}
}
