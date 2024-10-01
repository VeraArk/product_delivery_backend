package org.product_delivery_backend.dto.cartItemDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.product_delivery_backend.entity.Cart;
import org.product_delivery_backend.entity.Product;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private Cart cart;
    private Product product;
    private Integer productQuantity;
    private BigDecimal sum;
}
