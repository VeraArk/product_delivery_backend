package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.product_delivery_backend.dto.productDTO.AllProductResponseDto;
import org.product_delivery_backend.dto.productDTO.ProductRequestDto;
import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {


//    @Mapping(source = "product.title", target = "title")
    AllProductResponseDto toAllProductResponseDTO(Product product);


    ProductResponseDto toProductResponseDTO(Product product);

//    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequestDto productRequestDto);



}

