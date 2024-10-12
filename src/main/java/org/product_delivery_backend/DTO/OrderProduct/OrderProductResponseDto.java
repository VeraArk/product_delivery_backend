package org.product_delivery_backend.dto.OrderProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.product_delivery_backend.entity.Product;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class OrderProductResponseDto {

    private Long id;
    private Long orderId;
    private Product product;
    private Integer productQuantity;
    private BigDecimal sum;

}
