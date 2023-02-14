package com.increff.pos.dto;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import com.increff.pos.model.InfoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.increff.pos.model.BrandData;
import com.increff.pos.model.BrandForm;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.util.StringUtil;

//todo dto to service call is done via flow service
//todo @transactional removal from dto layer compulsary to apply on dao layer
//todo
@Component
public class BrandDto {

    @Autowired
    private BrandService brandservice;
    @Autowired
    private InfoData info;

    // adding a brand
    @Transactional(rollbackOn = ApiException.class)
    public void addBrand(BrandForm f) throws ApiException {
        normalizeBrand(f);
        validation(f);
        BrandPojo p = convertBrandFormToPojo(f);
        brandservice.addBrand(p);
    }


    // gets brand by id
    @Transactional(rollbackOn = ApiException.class)
    public BrandData getBrand(Integer id) throws ApiException {
        BrandPojo bp = brandservice.getBrand(id);
        if (bp == null) {
            throw new ApiException("Brand with given ID does not exit, id: " + id);
        }
        BrandData bd = convertBrandPojoToData(bp);
        return bd;
    }

    // get list of all brands
    @Transactional
    public List<BrandData> getAllBrand() {

        List<BrandPojo> list = brandservice.getAllBrand();
        List<BrandData> list2 = new ArrayList<BrandData>();
        for (BrandPojo p : list) {
            list2.add(convertBrandPojoToData(p));
        }
        return list2;
    }

    // update a brand using id
    @Transactional(rollbackOn = ApiException.class)
    public void updateBrand(Integer id, BrandForm f) throws ApiException {
        normalizeBrand(f);
        validation(f);
        BrandData ex = getBrand(id);

        ex.setBrandName(f.getBrandName());
        ex.setBrandCategory(f.getBrandCategory());

        BrandPojo p = convertBrandDataToPojo(ex);
        brandservice.updateBrand(id, p);
    }

    // finding a brand using brandname and category
    @Transactional
    public BrandData findBrand(String brandName, String brandCategory) throws ApiException {
        BrandPojo p = brandservice.findBrand(brandName, brandCategory);
        if (p != null) {
            return convertBrandPojoToData(p);
        } else {
            throw new ApiException(
                    "The given Brand: " + brandName + " Category: " + brandCategory + " Pair does not exist");
        }
    }

    // noramlization of brand
    public void normalizeBrand(BrandForm p) {
        p.setBrandName(StringUtil.toLowerCase(p.getBrandName()));
        p.setBrandCategory(StringUtil.toLowerCase(p.getBrandCategory()));
    }


    // validation of brand
    public void validation(BrandForm p) throws ApiException {
        if (StringUtil.isEmpty(p.getBrandName())) {
            throw new ApiException("Brand cannot be empty");
        }

        if (StringUtil.isEmpty(p.getBrandCategory())) {
            throw new ApiException("Category cannot be empty");
        }

        // Check if Brand exists already
        if (brandservice.findBrand(p.getBrandName(), p.getBrandCategory()) != null) {
            throw new ApiException("Given Brand-Category combination already exists");
        }
    }


    private static BrandPojo convertBrandDataToPojo(BrandData p) {
        BrandPojo d = new BrandPojo();
        d.setBrandCategory(p.getBrandCategory());
        d.setBrandName(p.getBrandName());
        d.setBrandId(p.getBrandId());
        return d;
    }

    // conversion from pojo to data
    private static BrandData convertBrandPojoToData(BrandPojo p) {
        BrandData d = new BrandData();
        d.setBrandCategory(p.getBrandCategory());
        d.setBrandName(p.getBrandName());
        d.setBrandId(p.getBrandId());
        return d;
    }

    // conversion from form to pojo
    private static BrandPojo convertBrandFormToPojo(BrandForm f) {
        BrandPojo p = new BrandPojo();
        p.setBrandName(f.getBrandName());
        p.setBrandCategory(f.getBrandCategory());
        return p;
    }
}
