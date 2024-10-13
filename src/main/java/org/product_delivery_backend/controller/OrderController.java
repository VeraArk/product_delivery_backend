package org.product_delivery_backend.controller;


import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.ErrorResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.service.OrderService;
import org.product_delivery_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder() {
        User user = userService.getUser();
        return ResponseEntity.ok(orderService.createOrder(user.getId()));
    }

    @PutMapping("/confirmed")
    public ResponseEntity<UpdateStatusOrderResponseDto> confirmOrder(
            @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.confirmOrder(orderRequestDto));
    }

    @PutMapping("/paid/{orderId}")
    public ResponseEntity<UpdateStatusOrderResponseDto> payForOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.payForOrder(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<UpdateStatusOrderResponseDto> cancelOrder(@PathVariable Long orderId) throws OrderException {
        UpdateStatusOrderResponseDto responseDto = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> clearOrder(@PathVariable Long orderId) {
        orderService.clearOrder(orderId);
        return ResponseEntity.ok("Order cleared");
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDto>> getOrders() {
        User user = userService.getUser();
        orderService.getOrders(user.getId());
        return ResponseEntity.ok(orderService.getOrders(user.getId()));
    }
    @GetMapping()
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        orderService.getAllOrders();
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}