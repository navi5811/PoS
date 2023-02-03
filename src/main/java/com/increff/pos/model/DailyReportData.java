package com.increff.pos.model;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

public class DailyReportData {
    @Temporal(TemporalType.TIMESTAMP)
    public Date date;
    public Integer numberOfOrders;
    public Integer numberOfItems;
    public Double total;

    public Date getDate() {
        return date;
    }

    public Integer getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(Integer numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public Integer getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Integer numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
