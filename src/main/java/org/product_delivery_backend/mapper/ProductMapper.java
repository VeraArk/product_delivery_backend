package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.product_delivery_backend.dto.productDto.*;
import org.product_delivery_backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    AllProductResponseDto toAllProductResponseDTO(Product product);

    ProductResponseDto toProductResponseDTO(Product product);

    Product toProduct(ProductRequestDto productRequestDto);

    Product toProductForAddToCart(ProductAddToCartRequestDto productAddToCartRequestDto);
}

