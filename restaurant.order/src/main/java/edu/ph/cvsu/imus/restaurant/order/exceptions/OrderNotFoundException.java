package edu.ph.cvsu.imus.restaurant.order.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super("Order not found with id:"+ message);
    }
}
