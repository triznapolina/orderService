package com.controller;

import com.entity.CurrentUser;
import com.dto.OrderDto;
import com.dto.UserDto;
import com.dto.UserOrdersResponse;
import com.entity.Order;
import com.service.OrderService;
import com.service.UserServiceClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserServiceClient userService;


    @DeleteMapping("/{orderId}/status/{status}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId, @PathVariable boolean status) {
        orderService.deleteOrder(status, orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping
    public ResponseEntity<UserOrdersResponse> createOrder(@Valid @RequestBody OrderDto orderDto,
                                                          @AuthenticationPrincipal CurrentUser authPrincipal) {

        Long userId = authPrincipal.getUserId();
        String username = authPrincipal.getUsername();

        UserDto user = userService.getUserInfoByEmail(username);
        Order createdOrder = orderService.createOrder(orderDto, userId);

        UserOrdersResponse response = new UserOrdersResponse(user, List.of(createdOrder));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PutMapping("/{orderId}")
    public ResponseEntity<UserOrdersResponse> updateOrder(@Valid @RequestBody OrderDto orderDto,
                                                          @PathVariable Long orderId,
                                                          @AuthenticationPrincipal CurrentUser authPrincipal) {

        String username = authPrincipal.getUsername();

        UserDto user = userService.getUserInfoByEmail(username);
        Order updatedOrder = orderService.updateOrder(orderDto, orderId);

        UserOrdersResponse response = new UserOrdersResponse(user, List.of(updatedOrder));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Order>> findAllOrdersByMe(@AuthenticationPrincipal CurrentUser authPrincipal) {

        Long userId = authPrincipal.getUserId();
        List<Order> orders = orderService.findAllOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

}
