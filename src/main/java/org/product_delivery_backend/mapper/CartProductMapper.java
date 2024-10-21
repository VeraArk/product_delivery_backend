package org.product_delivery_backend.mapper;


import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;

import org.product_delivery_backend.entity.CartProduct;


@Mapper(componentModel = "spring")
public interface CartProductMapper {

    @Mapping(source = "cartId", target = "cart.id") // Теперь правильно указываем путь к cart.id
    @Mapping(source = "productId", target = "product.id") // И к product.id
    CartProduct toCartProduct(CartProductResponseDto cartProductResponseDto);

    @Mapping(source = "cart.id", target = "cartId") // Указываем путь к cart.id
    @Mapping(source = "product.id", target = "productId") // Указываем путь к product.id
    CartProductResponseDto toCartProductResponseDto(CartProduct cartProduct);
}

