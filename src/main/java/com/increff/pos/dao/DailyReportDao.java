package com.increff.pos.dao;


import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.pojo.OrderPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
@Repository
public class DailyReportDao extends AbstractDao{



    private static String select_all = "select p from DailyReportPojo p";
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(DailyReportPojo p) {
        em.persist(p);
    }

    public List<DailyReportPojo> selectAll() {
        TypedQuery<DailyReportPojo> query = getQuery(select_all, DailyReportPojo.class);
        return query.getResultList();
    }


}
