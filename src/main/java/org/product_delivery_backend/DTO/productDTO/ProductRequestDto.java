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
public class ProductRequestDto {
    private String title;
    private BigDecimal price;
    private String productCode;
    private String minQuantity;
    private String description;
    private String photoLink;
}
