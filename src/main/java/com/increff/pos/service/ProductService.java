package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.ProductDao;
import com.increff.pos.pojo.ProductPojo;

@Service
public class ProductService {

    @Autowired
    private ProductDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addProduct(ProductPojo p) throws ApiException {
        dao.insert(p);
    }

    @Transactional
    public List<ProductPojo> getAllProduct() {
        return dao.selectAll();
    }

    @Transactional(rollbackOn = ApiException.class)
    public void updateProduct(Integer id, ProductPojo p) {

        ProductPojo ex = findProduct(id);

        ex.setProductName(p.getProductName());
        ex.setProductBrandCategory(p.getProductBrandCategory());
        ex.setProductBarcode(p.getProductBarcode());
        ex.setProductMrp(p.getProductMrp());
        dao.update(ex);
    }

    @Transactional
    public ProductPojo findProduct(Integer id) {
        ProductPojo p = dao.select(id);
        return p;
    }

    @Transactional
    public ProductPojo findProduct(String barcode) {
        ProductPojo p = dao.select(barcode);
        return p;
    }

    //use for checking if product already exists with different barcode
    @Transactional
    public ProductPojo find(String name, Integer id) {
        ProductPojo p = dao.select(name, id);
        return p;
    }

    @Transactional
    public List<ProductPojo> findAllProduct(Integer brandId) {
        List<ProductPojo> p = dao.selectProduct(brandId);
        return p;
    }

}
