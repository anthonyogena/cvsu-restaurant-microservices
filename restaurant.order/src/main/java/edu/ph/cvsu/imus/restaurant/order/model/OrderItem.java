package edu.ph.cvsu.imus.restaurant.order.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name="order_item")
public class OrderItem {

    public String getId() {
        return id;
    }
    public String getMenuId() {
        return menuId;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem() {
    }
    public OrderItem(String menuId, int quantity, BigDecimal price) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String menuId;
    private int quantity;
    private BigDecimal price;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OrderId", referencedColumnName = "id")
    private Order order; // This references the User entity
}
