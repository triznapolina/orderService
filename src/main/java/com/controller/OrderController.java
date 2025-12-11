package com.controller;

import com.dto.OrderDto;
import com.dto.UserDto;
import com.entity.Order;
import com.service.OrderService;
import com.service.UserServiceClient;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/app/order")
public class OrderController {

    private final UserServiceClient userService;
    private final OrderService orderService;

    public OrderController(UserServiceClient userServiceClient, OrderService orderService) {
        this.userService = userServiceClient;
        this.orderService = orderService;
    }


    @GetMapping("/email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) {
        return userService.getUserInfoByEmail(email);
    }


    @PostMapping("/users/{userId}")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable Long userId) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        Order createdOrder = orderService.createOrder(orderDto, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody OrderDto orderDto, @PathVariable Long orderId) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        Order updatedOrder = orderService.updateOrder(orderDto, orderId);
        return ResponseEntity.ok(updatedOrder);
    }


    @DeleteMapping("/{orderId}/status/{status}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId, @PathVariable boolean status) {
        orderService.deleteOrder(status, orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Order>> findAllActiveOrders() {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        List<Order> activeOrders = orderService.findAllActiveOrders();
        return ResponseEntity.ok(activeOrders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> findAllByStatus(@PathVariable String status) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        List<Order> orders = orderService.findAllByStatus(status);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Order>> findAllOrdersByUser(@PathVariable Long userId) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        List<Order> orders = orderService.findAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/users/{userId}/status/{status}")
    public ResponseEntity<List<Order>> findAllOrdersByUserWithStatus(@PathVariable Long userId,
                                                                     @PathVariable String status) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        List<Order> orders = orderService.findAllOrdersByUserWithStatus(status, userId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {

        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);

    }

    @GetMapping
    public ResponseEntity<Page<Order>> getUsersOnPage(@RequestParam int pageNo, @RequestParam int pageSize) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        Page<Order> page = orderService.getOrdersOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<Order>> filterOrders(@RequestParam String status, @RequestParam LocalDateTime createdAt,
                                                    Pageable pageable) {
        UserDto user = userService.getUserInfoByEmail(userService.getEmailById(userId));
        Page<Order> filteredOrders = orderService.filterOrders(status, createdAt, pageable);
        return ResponseEntity.ok(filteredOrders);
    }

}
