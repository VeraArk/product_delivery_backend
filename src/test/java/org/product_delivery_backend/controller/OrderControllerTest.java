package org.product_delivery_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.entity.Order;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.repository.OrderRepository;
import org.product_delivery_backend.service.OrderService;
import org.product_delivery_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "test@example.com")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderRepository orderRepository;


    private ObjectMapper objectMapper;
    private User user;
    private OrderResponseDto orderResponseDto;
    private OrderRequestDto orderRequestDto;
    private  UpdateStatusOrderResponseDto updateStatusOrderResponseDto;
    private Order order;


    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setId(1L);

        updateStatusOrderResponseDto = new UpdateStatusOrderResponseDto();

        order =new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING);
    }

    @Test
    void testCreateOrder_Success() throws Exception {

        when(userService.getUser()).thenReturn(user);

        when(orderService.createOrder(user.getId())).thenReturn(orderResponseDto);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponseDto.getId()));
    }

    @Test
    void testConfirmOrder_Success() throws Exception {

        updateStatusOrderResponseDto.setOrderStatus(OrderStatus.CONFIRMED);

        when(orderService.confirmOrder(any(OrderRequestDto.class))).thenReturn(updateStatusOrderResponseDto);

        mockMvc.perform(put("/api/order/confirmed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CONFIRMED.name()));
    }

    @Test
    void testConfirmOrder_OrderNotFound() throws Exception {

        when(orderService.confirmOrder(any(OrderRequestDto.class))).thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(put("/api/order/confirmed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPayForOrder_Success() throws Exception {

        updateStatusOrderResponseDto.setOrderStatus(OrderStatus.PAID);

        when(orderService.findOrderById(1L)).thenReturn(orderResponseDto);
        when(orderService.payForOrder(1L)).thenReturn(updateStatusOrderResponseDto);

        mockMvc.perform(put("/api/order/paid/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.PAID.name()));
    }

    @Test
    void testPayForOrder_OrderNotFound() throws Exception {
        when(orderService.payForOrder(1L)).thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(put("/api/order/paid/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCancelOrder_Success() throws Exception, OrderException {

        updateStatusOrderResponseDto.setOrderStatus(OrderStatus.CANCELLED);

        when(orderService.findOrderById(1L)).thenReturn(orderResponseDto);
        when(orderService.cancelOrder(1L)).thenReturn(updateStatusOrderResponseDto);

        mockMvc.perform(put("/api/order/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value(OrderStatus.CANCELLED.name()));
    }

    @Test
    void testClearOrder_Success() throws Exception {

        when(orderService.findOrderById(1L)).thenReturn(orderResponseDto);
        mockMvc.perform(delete("/api/order/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cleared"));
    }

    @Test
    void testGetOrders_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        List<OrderResponseDto> orders = Arrays.asList(new OrderResponseDto(), new OrderResponseDto());
        when(orderService.getOrders(user.getId())).thenReturn(orders);

        mockMvc.perform(get("/api/order/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(orders.size()));
    }

    @Test
    void testGetAllOrders_Success() throws Exception {
        List<OrderResponseDto> orders = Arrays.asList(new OrderResponseDto(), new OrderResponseDto());
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/order")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(orders.size()));
    }

}


