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
        // 1. Check for duplicates
        if(orderRepository.findOrderByCustomerNameAndTableNumber(order.getCustomerName(),
                order.getTableNumber()).isPresent()){
            throw new OrderDuplicateEntryException("Order for Table "+order.getTableNumber()+
                    " and customer "+ order.getCustomerName()+" is already in the system");
        }

        // 2. SAFETY CHECK: Prevent NullPointerException if orderItems is missing in JSON
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new RuntimeException("Error: You must provide at least one item in the order.");
        }

        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        // 3. Loop through items to fetch prices from Menu API
        for(OrderItem orderItem: order.getOrderItems()){

            String fullUrl = menuApiUrl + "/items/" + orderItem.getMenuId();

            try {
                // Call Menu API (Port 8081)
                MenuResponse menuData = restTemplate.getForObject(fullUrl, MenuResponse.class);

                if (menuData != null) {
                    // Set item price from Menu
                    orderItem.setPrice(menuData.getPrice());

                    // Price * Quantity
                    BigDecimal itemTotal = menuData.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));

                    // Add to the Grand Total
                    totalOrderAmount = totalOrderAmount.add(itemTotal);
                }
            } catch (Exception e) {
                // Log the error if an item isn't found
                System.out.println("CRITICAL: Failed to find menu item: " + orderItem.getMenuId());
            }

            orderItem.setOrder(order);
        }

        // 4. Set final calculated data
        order.setTotalAmount(totalOrderAmount);
        order.setOrderStatus(OrderStatus.PLACED); // Requirement: Start as PLACED

        // 5. Save everything
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

    @Override
    public Order patchOrder(String id, Order order) {
        if(orderRepository.findById(id).isEmpty()){
            throw new OrderNotFoundException(id);
        }
        for(OrderItem orderItem: order.getOrderItems()){
            orderItem.setOrder(order);
        }
        itemRepository.saveAll(order.getOrderItems());
        return orderRepository.save(order);
    }

    @Override
    public Order deleteOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Requirement: Delete sets status to CANCELLED
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}