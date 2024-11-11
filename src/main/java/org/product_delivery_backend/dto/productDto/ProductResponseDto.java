package org.product_delivery_backend.dto.productDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.product_delivery_backend.exceptions.InvalidDataException;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String title;
    private BigDecimal price;
    private String productCode;
    private String minQuantity;
    private String description;
    private String photoLink;
}
