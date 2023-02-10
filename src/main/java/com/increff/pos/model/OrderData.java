package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
@Getter
@Setter
public class OrderData {

	private Integer orderId;
	private Double billAmount;
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetime;
	private boolean invoiced;

}
