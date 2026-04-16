package com.canteen.management.controller;

import com.canteen.management.dto.OrderResponse;
import com.canteen.management.dto.PlaceOrderRequest;
import com.canteen.management.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/users/{userId}/orders")
    public OrderResponse placeOrder(@PathVariable Long userId, @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(userId, request);
    }

    @GetMapping("/api/users/{userId}/orders")
    public List<OrderResponse> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/api/admin/orders")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }
}
