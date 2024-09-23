package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.product_delivery_backend.dto.productDTO.ProductRequestDto;
import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {



    Product toProduct(ProductRequestDto productRequestDto);


    @Mapping(source = "product.id", target = "id")
    @Mapping(source = "product.title", target = "title")
    @Mapping(source = "product.price", target = "price")
    @Mapping(source = "product.productCode", target = "productCode")
    @Mapping(source = "product.minQuantity", target = "minQuantity")
    @Mapping(source = "product.description", target = "description")
    @Mapping(source = "product.photoLink", target = "photoLink")
        ProductResponseDto toProductResponseDTO(Product product);
    }

