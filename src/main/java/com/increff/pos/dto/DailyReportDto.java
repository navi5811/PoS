package com.increff.pos.dto;

import com.increff.pos.model.BrandForm;
import com.increff.pos.model.DailyReportData;
import com.increff.pos.pojo.BrandPojo;
import com.increff.pos.pojo.DailyReportPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;
import com.increff.pos.service.ApiException;
import com.increff.pos.service.BrandService;
import com.increff.pos.service.DailyReportService;
import com.increff.pos.service.OrderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@EnableScheduling
@Component
public class DailyReportDto {

    @Autowired
    DailyReportService dailyReportService;

    @Autowired
    OrderService orderService;

    @Autowired
    private BrandService brandservice;

    // adding a brand
//    @Scheduled(fixedRate = 1000)
    @Scheduled(cron = "0 0 0 * * *")
    public void addReport() throws ApiException {
        DailyReportPojo p=new DailyReportPojo();
        Date date=new Date();
        date.setDate(date.getDate()-1);
        Date todayDate=new Date();
        List<OrderPojo> op=orderService.getInvoicedBetween(date,todayDate);
        Integer orderItems=0;
        Integer orders=0;
        Double amount=0.0;
        orders= op.size();

        List<OrderItemPojo> orderItemList = new ArrayList<OrderItemPojo>();

        for (OrderPojo order : op) {
            List<OrderItemPojo> orderItemListTemp = orderService.getOrderItems(order.getOrderId());
            orderItemList.addAll(orderItemListTemp);
        }
        orderItems=orderItemList.size();

        for(OrderItemPojo oip:orderItemList)
        {
            amount+=oip.getOrderQuantity()*oip.getOrderSellingPrice();
        }
        p.setTotal(amount);
        p.setItems(orderItems);
        p.setDate(date);
        p.setOrders(orders);

        dailyReportService.addReport(p);
    }



    public List<DailyReportData> getAllReport(){
        List<DailyReportPojo> list=dailyReportService.getAllReport();
        List<DailyReportData> list1= new ArrayList<>();
        for(DailyReportPojo p:list)
        {
            list1.add(convert(p));
        }
        return list1;
    }


    public DailyReportData convert(DailyReportPojo p)
    {
        DailyReportData d=new DailyReportData();
        d.setDate(p.getDate());
        d.setNumberOfItems(p.getItems());
        d.setTotal(p.getTotal());
        d.setNumberOfOrders(p.getOrders());
        return d;
    }

}
