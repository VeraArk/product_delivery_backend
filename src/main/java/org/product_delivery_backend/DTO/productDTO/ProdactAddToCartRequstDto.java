package org.product_delivery_backend.dto.productDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdactAddToCartRequstDto {
    private Long id;
    private String title;
    private BigDecimal price;
}

