
package com.increff.pos.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name="billData")
public class BillData {

    private Integer orderId;
    private List<OrderItemData> orderItemData;
    private String datetime;
    private double billAmount;

}