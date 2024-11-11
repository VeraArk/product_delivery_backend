package org.product_delivery_backend.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.product_delivery_backend.dto.orderProduct.OrderProductResponseDto;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDto {

        private Long id;
        private Long userId;
        private LocalDateTime orderTime;
        private String address;
        private LocalDateTime deliveryTime;
        private List<OrderProductResponseDto> orderProducts;
        private BigDecimal totalSum;
        private OrderStatus orderStatus;
        private PaymentMethod paymentMethod;
        private String paymentUrl;
    }

