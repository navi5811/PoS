package com.increff.pos.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.pos.dao.OrderDao;
import com.increff.pos.dao.OrderItemDao;
import com.increff.pos.pojo.InventoryPojo;
import com.increff.pos.pojo.OrderItemPojo;
import com.increff.pos.pojo.OrderPojo;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    // Creating order
    @Transactional(rollbackFor = ApiException.class)
    public Integer createOrder(List<OrderItemPojo> orderItemList) {
        OrderPojo orderPojo = new OrderPojo();
        orderPojo.setDate(new Date());
        orderPojo.setInvoiced(false);
        orderDao.insert(orderPojo);
        for (OrderItemPojo p : orderItemList) {
            p.setOrderId(orderPojo.getOrderId());
            orderItemDao.insert(p);
        }
        return orderPojo.getOrderId();
    }

    // Fetching an Order item by id
    @Transactional
    public OrderItemPojo get(Integer id) {
        OrderItemPojo p = checkIfExists(id);
        return p;
    }

    // Fetching an Order by id
    @Transactional
    public OrderPojo getOrder(Integer id) {
        OrderPojo p = checkIfExistsOrder(id);
        return p;
    }

    // Fetch all order items of a particular order
    @Transactional
    public List<OrderItemPojo> getOrderItems(Integer order_id) throws ApiException {
        List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
        return lis;
    }

    // Calculate Total
    public Double billTotal(Integer orderId) {
        Double total = 0.0;
        List<OrderItemPojo> lis = orderItemDao.selectOrder(orderId);
        for (OrderItemPojo orderItemPojo : lis) {
            total += orderItemPojo.getOrderQuantity() * orderItemPojo.getOrderSellingPrice();
        }
        return total;
    }

    // Fetching all order items
    @Transactional
    public List<OrderItemPojo> getAll() {
        return orderItemDao.selectAll();
    }

    // Fetching all orders
    @Transactional
    public List<OrderPojo> getAllOrders() {
        return orderDao.selectAll();
    }

    // Deletion of order item
    @Transactional
    public OrderItemPojo delete(Integer id) {
        Integer order_id = orderItemDao.select(id).getOrderId();
        OrderItemPojo orderItemPojo = orderItemDao.select(id);
        orderItemDao.delete(id);
        List<OrderItemPojo> lis = orderItemDao.selectOrder(order_id);
        if (lis.isEmpty()) {
            orderDao.delete(order_id);
        }
        return orderItemPojo;
    }

    // Deletion of order doubtfull
    @Transactional
    public void deleteOrder(OrderItemPojo orderItemPojo) {
        orderItemDao.delete(orderItemPojo.getOrderId());

    }

    public void deleteOrderhelper(Integer orderId) {
        orderDao.delete(orderId);
    }


    // Updating order item
    @Transactional(rollbackFor = ApiException.class)
    public void update(OrderItemPojo ex) {
        orderItemDao.update(ex);
    }

    // Increasing inventory while deleting order
    public InventoryPojo increaseInventory(InventoryPojo inventoryPojo, OrderItemPojo orderItemPojo)
            throws ApiException {
        Integer updatedQuantity = inventoryPojo.getProductQuantity() + orderItemPojo.getOrderQuantity();
        inventoryPojo.setProductQuantity(updatedQuantity);
        return inventoryPojo;
    }

    // To update inventory while adding order
    public Integer updateInventory(InventoryPojo inventoryPojo, OrderItemPojo orderItemPojo) throws ApiException {
        Integer updatedQuantity = inventoryPojo.getProductQuantity() - orderItemPojo.getOrderQuantity();
        return updatedQuantity;
    }

    // Checking if a particular order item exists or not
    @Transactional(rollbackFor = ApiException.class)
    public OrderItemPojo checkIfExists(Integer id) {
        OrderItemPojo p = orderItemDao.select(id);
        return p;
    }

    // Checking if a particular order exists or not
    @Transactional(rollbackFor = ApiException.class)
    public OrderPojo checkIfExistsOrder(Integer id) {
        OrderPojo p = orderDao.select(id);
        return p;
    }

    public List<OrderPojo> getAllBetween(Date startingDate, Date endingDate) {
        return orderDao.selectBetweenDate(startingDate, endingDate);
    }

    public List<OrderPojo> getInvoicedBetween(Date startDate, Date endDate) {
        return orderDao.selectInvoicedDate(startDate, endDate);
    }
}