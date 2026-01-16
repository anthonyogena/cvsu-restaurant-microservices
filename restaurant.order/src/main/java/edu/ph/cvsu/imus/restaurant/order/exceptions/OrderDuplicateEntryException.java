package edu.ph.cvsu.imus.restaurant.order.exceptions;

public class OrderDuplicateEntryException extends RuntimeException {
    public OrderDuplicateEntryException(String message) {
        super(message);
    }
}
