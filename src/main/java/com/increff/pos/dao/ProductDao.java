package com.increff.pos.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.pos.pojo.ProductPojo;

@Repository
public class ProductDao extends AbstractDao {

	private static String delete_id = "delete from ProductPojo p where productId=:id";
	private static String select_id = "select p from ProductPojo p where productId=:id";
	private static String select_all = "select p from ProductPojo p";
	private static String select_barcode = "select p from ProductPojo p where productBarcode=:barcode";
	private static String select_product = "select p from ProductPojo p where productName=:productName and productBrandCategory =:productBrandId";
	private static String select_productid = "select p from ProductPojo p where productBrandCategory=:id";

	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void insert(ProductPojo p) {
		em.persist(p);
	}

	public int delete(int id) {
		Query query = em.createQuery(delete_id);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public ProductPojo select(int id) {
		TypedQuery<ProductPojo> query = getQuery(select_id, ProductPojo.class);
		query.setParameter("id", id);
		return getSingle(query);
	}

	// To search if same barcode exists already
	public ProductPojo select(String barcode) {
		TypedQuery<ProductPojo> query = getQuery(select_barcode, ProductPojo.class);
		query.setParameter("barcode", barcode);
		return getSingle(query);
	}

	// To search if same Product already exists with a different barcode
	public ProductPojo select(String productName, int productBrandId) {
		TypedQuery<ProductPojo> query = getQuery(select_product, ProductPojo.class);
		query.setParameter("productName", productName);
		query.setParameter("productBrandId", productBrandId);
		return getSingle(query);
	}

	public List<ProductPojo> selectAll() {
		TypedQuery<ProductPojo> query = getQuery(select_all, ProductPojo.class);
		return query.getResultList();
	}

	public List<ProductPojo> selectProduct(int id) {
		TypedQuery<ProductPojo> query = getQuery(select_productid, ProductPojo.class);
		query.setParameter("id", id);
		return query.getResultList();
	}

	public void update(ProductPojo p) {
	}
}
