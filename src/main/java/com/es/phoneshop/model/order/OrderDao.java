package com.es.phoneshop.model.order;

import java.util.List;

public interface OrderDao {
    void save(Order order);
    List<Order> getOrders();
    void setOrders(List<Order> orders);
    Order getOrder(Long id) throws NoSuchOrderException;
    Order getOrderBySecureId(String secureId) throws NoSuchOrderException;
}