package edu.ph.cvsu.imus.restaurant.order.repository;

import edu.ph.cvsu.imus.restaurant.order.model.Order;
import edu.ph.cvsu.imus.restaurant.order.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findOrderByOrderStatus(OrderStatus orderStatus);

    //@Query(value = "Select * from orders where customenr_name = ?1 and table_number = ?2", nativeQuery = true )
    Optional<Order> findOrderByCustomerNameAndTableNumber(String customerName, String tableNumber);
}
