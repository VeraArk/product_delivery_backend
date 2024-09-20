package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.product_delivery_backend.DTO.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;

@Mapper(componentModel = "spring")
    public interface ProductMapper {

        ProductResponseDto toProductResponseDTO(Product product);
    }
