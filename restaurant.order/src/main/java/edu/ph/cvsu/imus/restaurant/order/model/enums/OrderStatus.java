package edu.ph.cvsu.imus.restaurant.order.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PLACED("PLACED"),
    PREPARING("PREPARING"),
    READY("READY"),
    SERVED("SERVED"),
    CANCELLED("CANCELLED");


    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus category : OrderStatus.values()) {
            if (category.value.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid menu category: " + value);
    }
}
