package org.product_delivery_backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.ErrorResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.InvalidDataException;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.repository.OrderRepository;
import org.product_delivery_backend.service.OrderService;
import org.product_delivery_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Tag(name = "Order controller")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    @Operation(summary = "Create a new order",
            description = "Create a new order for the currently logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = OrderResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping()
    public ResponseEntity<OrderResponseDto> createOrder() {
        User user = userService.getUser();
        if (user == null) {
            throw new NotFoundException("User not found.");
        }
        return ResponseEntity.ok(orderService.createOrder(user.getId()));
    }

    @Operation(summary = "Confirm an order",
            description = "Confirm an order with the specified details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order confirmed successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateStatusOrderResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid order request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/confirmed")
    public ResponseEntity<UpdateStatusOrderResponseDto> confirmOrder(
            @RequestBody OrderRequestDto orderRequestDto) {
        if (orderRequestDto.getId() == null) {
            throw new InvalidDataException("Order ID cannot be null.");
        }
        return ResponseEntity.ok(orderService.confirmOrder(orderRequestDto));
    }

    @Operation(summary = "Pay for an order",
            description = "Process payment for the specified order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateStatusOrderResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid order ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/paid/{orderId}")
    public ResponseEntity<UpdateStatusOrderResponseDto> payForOrder(
            @PathVariable Long orderId) {
        OrderResponseDto order = orderService.findOrderById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found.");
        }
        return ResponseEntity.ok(orderService.payForOrder(orderId));
    }

    @Operation(summary = "Cancel an order",
            description = "Cancel the specified order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order canceled successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateStatusOrderResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid order ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<UpdateStatusOrderResponseDto> cancelOrder(@PathVariable Long orderId) throws OrderException {
        OrderResponseDto order = orderService.findOrderById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found.");
        }
        UpdateStatusOrderResponseDto responseDto = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Clear an order",
            description = "Remove the specified order from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cleared successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid order ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> clearOrder(@PathVariable Long orderId) {
        OrderResponseDto order = orderService.findOrderById(orderId);
        if (order == null) {
            throw new NotFoundException("Order not found.");
        }
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