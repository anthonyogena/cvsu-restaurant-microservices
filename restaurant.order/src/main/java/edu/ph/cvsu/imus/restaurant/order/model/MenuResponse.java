package edu.ph.cvsu.imus.restaurant.order.model;

import java.math.BigDecimal;

public class MenuResponse {

    private String id;
    private String name;
    private BigDecimal price;

    // --- Getters and Setters (Java needs these to work) ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}