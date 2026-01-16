package edu.ph.cvsu.imus.restaurant.order.service.impl;

import edu.ph.cvsu.imus.restaurant.order.exceptions.OrderDuplicateEntryException;
import edu.ph.cvsu.imus.restaurant.order.exceptions.OrderNotFoundException;
import edu.ph.cvsu.imus.restaurant.order.model.MenuResponse; // <--- MAKE SURE YOU HAVE THIS CLASS
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
    private final RestTemplate restTemplate; // <--- The phone to call Menu App

    // This reads the http://localhost:8081/v1/menu from your application.properties
    @Value("${menu.service.url}")
    private String menuApiUrl;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository itemRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Order createOrder(Order order) {
        // 1. Check for duplicates (existing logic)
        if(orderRepository.findOrderByCustomerNameAndTableNumber(order.getCustomerName(),
                order.getTableNumber()).isPresent()){
            throw new OrderDuplicateEntryException("Order for Table "+order.getTableNumber()+
                    " and customer "+ order.getCustomerName()+" is already in the system");
        }

        BigDecimal totalOrderAmount = BigDecimal.ZERO;

        // 2. Loop through every item in the order
        for(OrderItem orderItem: order.getOrderItems()){

            // Construct the URL: http://localhost:8081/v1/menu/items/{id}
            String fullUrl = menuApiUrl + "/items/" + orderItem.getMenuId();

            // CALL THE MENU API!
            // We ask: "Hey, give me details for this ID"
            try {
                MenuResponse menuData = restTemplate.getForObject(fullUrl, MenuResponse.class);

                if (menuData != null) {
                    // We got the data! Set the price and calculate total.
                    orderItem.setPrice(menuData.getPrice());

                    // Price * Quantity
                    BigDecimal itemTotal = menuData.getPrice().multiply(new BigDecimal(orderItem.getQuantity()));

                    // Add to the Grand Total
                    totalOrderAmount = totalOrderAmount.add(itemTotal);
                }
            } catch (Exception e) {
                // If Menu app is down or ID is wrong, this prevents a crash
                System.out.println("Error fetching menu item: " + e.getMessage());
            }

            orderItem.setOrder(order);
        }

        // 3. Set the calculated total before saving
        order.setTotalAmount(totalOrderAmount);

        // 4. Save to Database
        itemRepository.saveAll(order.getOrderItems());
        return orderRepository.save(order);
    }

    // --- The rest of the methods stay the same ---

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
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}