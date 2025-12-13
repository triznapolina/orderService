package com.unit;

import java.util.*;
import com.dto.OrderDto;
import com.dto.UserDto;
import com.entity.Order;
import com.mapper.OrderMapper;
import com.repository.OrderRepository;
import com.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;


    private OrderDto testOrderDto;
    private Order testOrder;
    private Long testOrderId;
    private Long testUserId;
    private UserDto user;

    @Test
    void createOrderTest() {
        // Arrange
        testOrderId = 1L;
        testUserId = 1L;
        testOrderDto = new OrderDto();
        testOrderDto.setId(testOrderId);
        testOrder = new Order();
        user = new UserDto();
        user.setId(testUserId);

        when(orderMapper.convertToEntity(testOrderDto)).thenReturn(testOrder);
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        // Act
        Order result = orderService.createOrder(testOrderDto, user.getId());


        //Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
    }


    @Test
    void updateOrderTest() {
        // Arrange
        testOrderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setId(testOrderId);

        OrderDto updateDto = new OrderDto();
        updateDto.setStatus("updated");

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(existingOrder);

        // Act
        Order result = orderService.updateOrder(updateDto, testOrderId);

        // Assert
        assertNotNull(result);
        assertEquals("updated", result.getStatus());
    }


    @Test
    void findAllOrdersByUser() {
        // Arrange
        testOrderId = 1L;
        testUserId = 1L;

        testOrderDto = new OrderDto();

        testOrder = new Order();

        List<Order> userOrders = Collections.singletonList(testOrder);
        when(orderRepository.findByUserId(testUserId)).thenReturn(userOrders);

        // Act
        List<Order> result = orderService.findAllOrdersByUser(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    void getOrderById() {
        // Arrange
        testOrderId = 1L;

        testOrder = new Order();
        testOrder.setId(testOrderId);

        when(orderRepository.getOrderById(testOrderId)).thenReturn(testOrder);

        // Act
        Optional<Order> result = orderService.getOrderById(testOrderId);

        // Assert
        assertEquals(testOrder.getId(), 1);
    }




}
