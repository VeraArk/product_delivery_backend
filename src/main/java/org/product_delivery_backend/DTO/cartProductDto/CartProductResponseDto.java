package org.product_delivery_backend.dto.cartProductDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class CartProductResponseDto {
    private Long id;
    private Long cartId;
    private Long productId;
    private Integer productQuantity;
    private BigDecimal sum;
}
