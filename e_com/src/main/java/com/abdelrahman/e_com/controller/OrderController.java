package com.abdelrahman.e_com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abdelrahman.e_com.model.dto.OrderRequest;
import com.abdelrahman.e_com.model.dto.OrderResponse;
import com.abdelrahman.e_com.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @GetMapping("")
    public ResponseEntity<List<OrderResponse>> getAllOrdersResponses() {
        try {
            List<OrderResponse> orders = orderService.getAllOrdersResponses();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch(RuntimeException e) {
            System.out.println("Error fetching orders: " + e.getMessage());
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            if(orderRequest.items() == null || orderRequest.items().isEmpty()) {
                return new ResponseEntity<>("Items cannot be empty", HttpStatus.BAD_REQUEST);
            }
            OrderResponse orderResponse = orderService.placeOrder(orderRequest);
            return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        } catch(RuntimeException e) {
            System.out.println("Error placing order: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
