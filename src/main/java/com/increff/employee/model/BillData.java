
package com.increff.employee.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="billData")
public class BillData {

    private int orderId;
    private List<OrderItemData> orderItemData;
    private String datetime;
    private double billAmount;
    
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<OrderItemData> getOrderItemData() {
        return orderItemData;
    }

    public void setOrderItemData(List<OrderItemData> orderItemData) {
        this.orderItemData = orderItemData;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }
}