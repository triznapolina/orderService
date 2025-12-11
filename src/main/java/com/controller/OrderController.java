package com.controller;

import com.dto.OrderDto;
import com.entity.Order;
import com.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/users/{userId}")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable Long userId) {
        Order createdOrder = orderService.createOrder(orderDto, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable Long orderId) {
        Order updatedOrder = orderService.updateOrder(orderDto, orderId);
        return ResponseEntity.ok(updatedOrder);
    }


    @DeleteMapping("/{orderId}/status/{status}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId, @PathVariable boolean status) {
        orderService.deleteOrder(status, orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Order>> findAllOrdersByMe(@PathVariable Long userId) {
        List<Order> orders = orderService.findAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Order>> findAllItems(@PathVariable Long userId) {
        List<Order> orders = orderService.findAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

}
