package org.product_delivery_backend.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.InvalidDataException;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.service.OrderService;
import org.product_delivery_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_UserNotFound() {
        when(userService.getUser()).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderController.createOrder();
        });

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void testCreateOrder_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.getUser()).thenReturn(mockUser);
        OrderResponseDto mockResponse = new OrderResponseDto();
        when(orderService.createOrder(mockUser.getId())).thenReturn(mockResponse);

        ResponseEntity<OrderResponseDto> response = orderController.createOrder();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).createOrder(mockUser.getId());
    }

    @Test
    void testConfirmOrder_InvalidData() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setId(null);

        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            orderController.confirmOrder(orderRequestDto);
        });

        assertEquals("Order ID cannot be null.", exception.getMessage());
    }

    @Test
    void testConfirmOrder_Success() throws Exception {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setId(1L);
        UpdateStatusOrderResponseDto mockResponse = new UpdateStatusOrderResponseDto();
        when(orderService.confirmOrder(orderRequestDto)).thenReturn(mockResponse);

        ResponseEntity<UpdateStatusOrderResponseDto> response = orderController.confirmOrder(orderRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).confirmOrder(orderRequestDto);
    }

    @Test
    void testPayForOrder_OrderNotFound() {
        Long orderId = 1L;
        when(orderService.findOrderById(orderId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderController.payForOrder(orderId);
        });

        assertEquals("Order not found.", exception.getMessage());
    }

    @Test
    void testPayForOrder_Success() {
        Long orderId = 1L;
        UpdateStatusOrderResponseDto mockResponse = new UpdateStatusOrderResponseDto();
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        when(orderService.findOrderById(orderId)).thenReturn(orderResponseDto);
        when(orderService.payForOrder(orderId)).thenReturn(mockResponse);

        ResponseEntity<UpdateStatusOrderResponseDto> response = orderController.payForOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).payForOrder(orderId);
    }

    @Test
    void testCancelOrder_OrderNotFound() {
        Long orderId = 1L;
        when(orderService.findOrderById(orderId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderController.cancelOrder(orderId);
        });

        assertEquals("Order not found.", exception.getMessage());
    }

    @Test
    void testCancelOrder_Success() throws Exception, OrderException {
        Long orderId = 1L;
        UpdateStatusOrderResponseDto mockResponse = new UpdateStatusOrderResponseDto();
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        when(orderService.findOrderById(orderId)).thenReturn(orderResponseDto);
        when(orderService.cancelOrder(orderId)).thenReturn(mockResponse);

        ResponseEntity<UpdateStatusOrderResponseDto> response = orderController.cancelOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testGetOrders_Success() {
        User mockUser = new User();
        mockUser.setId(1L);
        List<OrderResponseDto> mockOrders = new ArrayList<>();
        when(userService.getUser()).thenReturn(mockUser);
        when(orderService.getOrders(mockUser.getId())).thenReturn(mockOrders);

        ResponseEntity<List<OrderResponseDto>> response = orderController.getOrders();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService, times(1)).getOrders(mockUser.getId());
    }

    @Test
    void testClearOrder_OrderNotFound() {
        Long orderId = 1L;
        when(orderService.findOrderById(orderId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            orderController.clearOrder(orderId);
        });

        assertEquals("Order not found.", exception.getMessage());
    }

    @Test
    void testClearOrder_Success() {
        Long orderId = 1L;
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        when(orderService.findOrderById(orderId)).thenReturn(orderResponseDto);

        ResponseEntity<String> response = orderController.clearOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order cleared", response.getBody());
        verify(orderService, times(1)).clearOrder(orderId);
    }
}