package edu.ph.cvsu.imus.restaurant.order.repository;

import edu.ph.cvsu.imus.restaurant.order.model.Order;
import edu.ph.cvsu.imus.restaurant.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByOrder(Order order);
}
