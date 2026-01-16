package edu.ph.cvsu.imus.restaurant.order.controller;

import edu.ph.cvsu.imus.restaurant.order.model.Order;
import edu.ph.cvsu.imus.restaurant.order.model.enums.OrderStatus;
import edu.ph.cvsu.imus.restaurant.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")

public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("")
    public ResponseEntity<Order> createOrder(
            @RequestBody Order order) {

        Order created = orderService.createOrder(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
    @GetMapping("")
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) OrderStatus status){
        if (status != null) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        return ResponseEntity.ok(orderService.getOrders());
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable String id,
            @RequestBody Order menuItem) {

        return ResponseEntity.ok(orderService.patchOrder(id, menuItem));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }
}
