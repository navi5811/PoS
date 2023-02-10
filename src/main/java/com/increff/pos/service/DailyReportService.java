package com.increff.pos.service;

import com.increff.pos.dao.BrandDao;
import com.increff.pos.dao.DailyReportDao;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class DailyReportService {


    @Autowired
    private DailyReportDao dao;

    @Transactional(rollbackOn = ApiException.class)
    public void addReport(DailyReportPojo p) {
        dao.insert(p);
    }

    @Transactional
    public List<DailyReportPojo> getAllReport() {
        return dao.selectAll();
    }


}
