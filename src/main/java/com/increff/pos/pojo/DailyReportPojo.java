package com.increff.pos.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
@Getter
@Setter
@Entity
public class DailyReportPojo {

    private Integer orders;
    private Integer items;
    private Double total;
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

}
