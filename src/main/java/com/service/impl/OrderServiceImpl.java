package com.service.impl;

import com.dto.OrderDto;
import com.entity.Order;
import com.mapper.OrderMapper;
import com.repository.OrderRepository;
import com.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    @Override
    @Transactional
    public OrderDto createOrder(OrderDto order, Long id) {
        Order orderRes = orderMapper.convertToEntity(order);
        orderRes.setTotalPrice(order.getTotalPrice());
        orderRes.setStatus(order.getStatus());
        orderRes.setUserId(id);
        orderRes.setDeleted(false);
        orderRes.setStatus("created");

        orderRes = orderRepository.save(orderRes);

        return orderMapper.convertToDTO(orderRes);

    }

    @Override
    @Transactional
    public OrderDto updateOrder(OrderDto order, Long id) {
        Order orderRes = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id= "+ id + " is not found"));

        orderRes.setTotalPrice(order.getTotalPrice());
        orderRes.setStatus("updated");
        orderRes = orderRepository.save(orderRes);
        return orderMapper.convertToDTO(orderRes);
    }

    @Override
    @Transactional
    public void deleteOrder(boolean status, long orderId) {
        orderRepository.setDeletedStatus(status, orderId);
    }

    @Override
    @Transactional
    public List<Order> findAllActiveOrders() {
        return orderRepository.findAllActiveOrders();
    }

    @Override
    @Transactional
    public List<Order> findAllByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public List<Order> findAllOrdersByUser(long id) {
        return orderRepository.findByUserId(id);
    }

    @Override
    @Transactional
    public List<Order> findAllOrdersByUserWithStatus(String active,long id) {
        return orderRepository.findByUserIdAndStatus(id, active);
    }

    @Override
    @Transactional
    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Page<Order> getOrdersOnPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Page<Order> filterOrders(String status, LocalDateTime createdAt, Pageable pageable) {
        Specification<Order> spec = hasStatus(status).and(hasCreationDate(LocalDate.from(createdAt)));
        return orderRepository.findAll(spec, pageable);
    }
}
