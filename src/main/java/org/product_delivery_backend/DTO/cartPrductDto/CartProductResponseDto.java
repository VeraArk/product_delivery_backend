package org.product_delivery_backend.dto.cartPrductDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.product_delivery_backend.entity.Cart;
import org.product_delivery_backend.entity.Product;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class CartProductResponseDto {
    private Long id;
    private Integer productQuantity;
    private BigDecimal sum;
}
