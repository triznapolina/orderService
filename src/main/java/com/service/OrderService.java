package com.service;

import com.dto.OrderDto;
import com.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {



    Order createOrder (OrderDto order, Long id);

    Order updateOrder (OrderDto order, Long id);

    void deleteOrder(boolean status, long orderId);

    List<Order> findAllByStatus (String status);

    List<Order> findAllOrdersByUser (long id);

    List<Order> findAllOrdersByUserWithStatus(String active,long id);

    Optional<Order> getOrderById (long id);

    Page<Order> getOrdersOnPage(int pageNo, int pageSize);

    Page<Order> filterOrders(String status, LocalDateTime createdAt, Pageable pageable);
}
