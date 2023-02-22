package com.increff.pos.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.pojo.BrandPojo;

@Service
public class BrandService {

    @Autowired
    private BrandDao brandDao;

    @Transactional
    public void addBrand(BrandPojo p) {
        brandDao.insert(p);
    }

    @Transactional(rollbackOn = ApiException.class)
    public BrandPojo getBrand(Integer id) {
        BrandPojo p = brandDao.select(id);
        return p;
    }

    @Transactional
    public List<BrandPojo> getAllBrand() {
        return brandDao.selectAll();
    }

    @Transactional
    public void updateBrand(Integer id, BrandPojo p) {
        BrandPojo ex = getBrand(id);
        ex.setBrandName(p.getBrandName());
        ex.setBrandCategory(p.getBrandCategory());
        brandDao.update(ex);
    }

    @Transactional
    public BrandPojo findBrand(String brandName, String brandCategory) {
        BrandPojo p = brandDao.select(brandName, brandCategory);
        return p;
    }
}
