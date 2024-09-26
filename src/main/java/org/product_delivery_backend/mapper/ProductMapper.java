package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.product_delivery_backend.DTO.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    org.product_delivery_backend.dto.productDTO.AllProductResponseDto toAllProductResponseDTO(Product product);

    ProductResponseDto toProductResponseDTO(Product product);

    Product toProduct(org.product_delivery_backend.dto.productDTO.ProductRequestDto productRequestDto);



}

