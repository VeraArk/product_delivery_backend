package org.product_delivery_backend.dto.OrderProduct;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductRequestDto {
        private Long productId;
        private int quantity;
        private BigDecimal price;  // Цена продукта
    }
