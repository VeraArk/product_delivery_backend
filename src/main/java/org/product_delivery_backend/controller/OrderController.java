package org.product_delivery_backend.controller;


import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.orderDto.ConfirmedOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.createOrder(userId));
    }

    @PutMapping("/confirmed")
    public ResponseEntity<ConfirmedOrderResponseDto> confirmOrder(
            @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.confirmOrder(orderRequestDto));
    }

    @PutMapping("/paid/{orderId}")
    public ResponseEntity<OrderResponseDto> payForOrder(
            @PathVariable Long orderId,
            @RequestBody PaymentMethod paymentMethod) {
        return ResponseEntity.ok(orderService.payForOrder(orderId, paymentMethod));
    }

    @PutMapping("/cancelled/{orderId}")
    public ResponseEntity<OrderStatus> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody OrderStatus orderStatus) throws OrderException {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, orderStatus));
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> clearOrder(@PathVariable Long orderId) {
        orderService.clearOrder(orderId);
        return ResponseEntity.ok("Order cleared");
    }
}