package org.product_delivery_backend.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.product_delivery_backend.dto.OrderProduct.OrderProductResponseDto;
import org.product_delivery_backend.entity.OrderProduct;
import org.product_delivery_backend.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class OrderResponseDto {

        private Long id;
        private Long userId;
        private LocalDateTime orderTime;
        private List<OrderProductResponseDto> orderProducts;
        private BigDecimal totalSum;
        private OrderStatus orderStatus;
    }

