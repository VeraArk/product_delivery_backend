package org.product_delivery_backend.dto.orderProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class OrderProductResponseDto {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer productQuantity;
    private BigDecimal sum;

}
