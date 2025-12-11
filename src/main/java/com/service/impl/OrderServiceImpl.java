package com.service.impl;

import com.dto.OrderDto;
import com.entity.Order;
import com.mapper.OrderMapper;
import com.repository.OrderRepository;
import com.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.specification.OrderSpecification.hasStatus;
import static com.specification.OrderSpecification.hasCreationDate;

@Service
public class OrderServiceImpl implements OrderService {

   private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }


    @Override
    public Order createOrder(OrderDto order, Long id) {
        Order orderRes = orderMapper.convertToEntity(order);
        orderRes.setTotalPrice(order.getTotalPrice());
        orderRes.setStatus(order.getStatus());
        orderRes.setUserId(id);
        orderRes.setDeleted(false);
        orderRes.setStatus("created");

        return orderRepository.save(orderRes);
    }

    @Override
    public Order updateOrder(OrderDto order, Long id) {
        Order orderRes = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id= "+ id + " is not found"));

        orderRes.setTotalPrice(order.getTotalPrice());
        orderRes.setStatus(order.getStatus());
        orderRes.setUserId(id);
        orderRes.setDeleted(false);
        orderRes.setStatus("updated");


        return orderRepository.save(orderRes);
    }

    @Override
    public void deleteOrder(boolean status, long orderId) {
        orderRepository.setDeletedStatus(status, orderId);
    }

       @Override
    public List<Order> findAllByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> findAllOrdersByUser(long id) {
        return orderRepository.findByUserId(id);
    }

    @Override
    public List<Order> findAllOrdersByUserWithStatus(String active,long id) {
        return orderRepository.findByUserIdAndStatus(id, active);
    }

    @Override
    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Page<Order> getOrdersOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> filterOrders(String status, LocalDateTime createdAt, Pageable pageable) {
        Specification<Order> spec = hasStatus(status).and(hasCreationDate(LocalDate.from(createdAt)));
        return orderRepository.findAll(spec, pageable);
    }
}
