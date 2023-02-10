package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
@Getter
@Setter
public class DailyReportData {

    @Temporal(TemporalType.TIMESTAMP)
    public Date date;
    public Integer numberOfOrders;
    public Integer numberOfItems;

    public Double total;

}
