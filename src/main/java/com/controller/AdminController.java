package com.controller;

import com.entity.CurrentUser;
import com.dto.UserOrdersResponse;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
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
    public UserDto getUserByEmail(@PathVariable String email) {
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



    @GetMapping("/status/{status}")
    public ResponseEntity<UserOrdersResponse> findAllByStatus(@PathVariable String status,
                                                              @AuthenticationPrincipal CurrentUser authPrincipal) {

        String username = authPrincipal.getUsername();

        UserDto user = userService.getUserInfoByEmail(username);
        List<Order> orders = orderService.findAllByStatus(status);

        UserOrdersResponse response = new UserOrdersResponse(user, orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserOrdersResponse> findAllOrdersByUser(@PathVariable Long userId,
                                                                  @AuthenticationPrincipal CurrentUser authPrincipal) {

        String username = authPrincipal.getUsername();
        UserDto user = userService.getUserInfoByEmail(username);
        List<Order> orders = orderService.findAllOrdersByUser(userId);

        UserOrdersResponse response = new UserOrdersResponse(user, orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/status/{status}")
    public ResponseEntity<UserOrdersResponse> findAllOrdersByUserWithStatus(@PathVariable Long userId, @PathVariable String status,
                                                                            @AuthenticationPrincipal CurrentUser authPrincipal) {

        String username = authPrincipal.getUsername();
        UserDto user = userService.getUserInfoByEmail(username);
        List<Order> orders = orderService.findAllOrdersByUserWithStatus(status, userId);

        UserOrdersResponse response = new UserOrdersResponse(user, orders);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<UserOrdersResponse> getOrderById(@PathVariable Long orderId,
                                                           @AuthenticationPrincipal CurrentUser authPrincipal) {

        String username = authPrincipal.getUsername();
        UserDto user = userService.getUserInfoByEmail(username);
        Optional<Order> order = orderService.getOrderById(orderId);

        List<Order> orders = order.map(Collections::singletonList).orElse(Collections.emptyList());
        UserOrdersResponse response = new UserOrdersResponse(user, orders);
        return ResponseEntity.ok(response);
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


}
