package com.controller;

import com.dto.ItemDto;
import com.dto.UserDto;
import com.entity.Item;
import com.entity.Order;
import com.service.ItemService;
import com.service.OrderService;
import com.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/app/admin-order")
@RequiredArgsConstructor
public class AdminController {

    private final UserServiceClient userService;
    private final OrderService orderService;
    private final ItemService itemService;


    @GetMapping("/{email}")
    public UserDto getUserById(@PathVariable String email) {
        return userService.getUserInfoByEmail(email);
    }


    @PostMapping("/add-item")
    public ResponseEntity<Item> createItem(@RequestBody ItemDto itemDto) {
        Item createdItem = itemService.createItem(itemDto);
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }


    @PutMapping("/update-item/{itemId}")
    public ResponseEntity<Item> updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        Item updatedItem = itemService.updateItem(itemDto, itemId);
        return ResponseEntity.ok(updatedItem);
    }


    @DeleteMapping("/delete-item/{itemId}")
    public ResponseEntity<Item> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping
    public ResponseEntity<Page<Order>> getOrdersOnPage(@RequestParam int pageNo, @RequestParam int pageSize) {
        Page<Order> page = orderService.getOrdersOnPage(pageNo, pageSize);
        return ResponseEntity.ok(page);
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<Order>> filterOrders(@RequestParam String status, @RequestParam LocalDateTime createdAt,
                                                    Pageable pageable) {
        Page<Order> filteredOrders = orderService.filterOrders(status, createdAt, pageable);
        return ResponseEntity.ok(filteredOrders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> findAllByStatus(@PathVariable String status) {
        List<Order> orders = orderService.findAllByStatus(status);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/users/{userId}/status/{status}")
    public ResponseEntity<List<Order>> findAllOrdersByUserWithStatus(@PathVariable Long userId,
                                                                     @PathVariable String status) {
        List<Order> orders = orderService.findAllOrdersByUserWithStatus(status, userId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Order>> findAllOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.findAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Optional<Order>> getOrderById(@PathVariable Long orderId) {

        Optional<Order> order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);

    }


}
