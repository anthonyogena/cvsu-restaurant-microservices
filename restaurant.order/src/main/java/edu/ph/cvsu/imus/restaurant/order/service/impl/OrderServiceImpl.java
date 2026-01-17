package edu.ph.cvsu.imus.restaurant.order.service.impl;

import edu.ph.cvsu.imus.restaurant.order.exceptions.OrderDuplicateEntryException;
import edu.ph.cvsu.imus.restaurant.order.exceptions.OrderNotFoundException;
import edu.ph.cvsu.imus.restaurant.order.model.MenuResponse;
import edu.ph.cvsu.imus.restaurant.order.model.Order;
import edu.ph.cvsu.imus.restaurant.order.model.OrderItem;
import edu.ph.cvsu.imus.restaurant.order.model.enums.OrderStatus;
import edu.ph.cvsu.imus.restaurant.order.repository.OrderItemRepository;
import edu.ph.cvsu.imus.restaurant.order.repository.OrderRepository;
import edu.ph.cvsu.imus.restaurant.order.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final RestTemplate restTemplate;

    @Value("${menu.service.url}")
    private String menuApiUrl;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository itemRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Order createOrder(Order order) {
        if(orderRepository.findOrderByCustomerNameAndTableNumber(order.getCustomerName(),
                order.getTableNumber()).isPresent()){
            throw new OrderDuplicateEntryException("Order for Table "+order.getTableNumber()+
                    " and customer "+ order.getCustomerName()+" is already in the system");
        }

        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new RuntimeException("Error: You must provide at least one item in the order.");
        }

        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        for(OrderItem orderItem: order.getOrderItems()){
            String fullUrl = menuApiUrl + "/items/" + orderItem.getMenuId();
            try {
                MenuResponse menuData = restTemplate.getForObject(fullUrl, MenuResponse.class);
                if (menuData != null) {
                    orderItem.setPrice(menuData.getPrice());
                    BigDecimal itemTotal = menuData.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));
                    totalOrderAmount = totalOrderAmount.add(itemTotal);
                }
            } catch (Exception e) {
                System.out.println("CRITICAL: Failed to find menu item: " + orderItem.getMenuId());
            }
            orderItem.setOrder(order);
        }

        order.setTotalAmount(totalOrderAmount);
        order.setOrderStatus(OrderStatus.PLACED);

        itemRepository.saveAll(order.getOrderItems());
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setOrderItems(itemRepository.findByOrder(order));
        return order;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findOrderByOrderStatus(status);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    /**
     * FIXED PATCH LOGIC:
     * This method now finds the record first and only updates fields that are provided.
     * It avoids the NullPointerException by NOT looping through items unless necessary.
     */
    @Override
    public Order patchOrder(String id, Order incomingUpdate) {
        // 1. Fetch the existing order from Database
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // 2. Only update the status if provided in the request
        if (incomingUpdate.getOrderStatus() != null) {
            existingOrder.setOrderStatus(incomingUpdate.getOrderStatus());
        }

        // 3. Only update the table number if provided
        if (incomingUpdate.getTableNumber() != null) {
            existingOrder.setTableNumber(incomingUpdate.getTableNumber());
        }

        // 4. Save the modified record
        return orderRepository.save(existingOrder);
    }

    @Override
    public Order deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}