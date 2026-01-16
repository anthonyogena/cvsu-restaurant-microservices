package edu.ph.cvsu.imus.restaurant.order.service;

import edu.ph.cvsu.imus.restaurant.order.model.Order;
import edu.ph.cvsu.imus.restaurant.order.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    public Order createOrder(Order order);
    public Order getOrderById(String id);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrders();

    public Order patchOrder(String id, Order order);
    public Order deleteOrder(String id);
}
