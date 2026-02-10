package com.abdelrahman.e_com.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abdelrahman.e_com.model.Order;
import com.abdelrahman.e_com.model.OrderItem;
import com.abdelrahman.e_com.model.Product;
import com.abdelrahman.e_com.model.dto.OrderItemRequest;
import com.abdelrahman.e_com.model.dto.OrderItemResponse;
import com.abdelrahman.e_com.model.dto.OrderRequest;
import com.abdelrahman.e_com.model.dto.OrderResponse;
import com.abdelrahman.e_com.repository.OrderRepository;
import com.abdelrahman.e_com.repository.ProductRepository;


@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
       
        Order order = new Order();
        String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);
        order.setCustomerName(orderRequest.customerName());
        order.setEmail(orderRequest.email());
        order.setStatus("PLACED");
        order.setOrderDate(java.time.LocalDate.now());

        List<OrderItem> orderItems = new ArrayList<>();
        
        
        for(OrderItemRequest itemRequest : orderRequest.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemRequest.productId()));
            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(itemRequest.quantity())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())))
                .order(order)
                .build();

            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);
        Order saveOrder =  orderRepository.save(order);

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for(OrderItem item : saveOrder.getOrderItems()) {
            OrderItemResponse orderItemResponse = new OrderItemResponse(
                item.getProduct().getName(),
                item.getQuantity(),
                item.getTotalPrice()
            );
            itemResponses.add(orderItemResponse);
        }

        OrderResponse orderResponse = new OrderResponse(
            saveOrder.getOrderId(),
            saveOrder.getCustomerName(),
            saveOrder.getEmail(),
            saveOrder.getStatus(),
            saveOrder.getOrderDate(),
            itemResponses
        );

       return orderResponse;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersResponses() {

        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order order : orders) {
             List<OrderItemResponse> orderItemResponses = new ArrayList<>();
            for(OrderItem item : order.getOrderItems()) {
                OrderItemResponse orderItemResponse = new OrderItemResponse(
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice()
                );
                orderItemResponses.add(orderItemResponse);
            }
            OrderResponse orderResponse = new OrderResponse(
                order.getOrderId(),
                order.getCustomerName(),
                order.getEmail(),
                order.getStatus(),
                order.getOrderDate(),
                orderItemResponses
            );
            orderResponses.add(orderResponse);
        }        

        return orderResponses;
    }

}
