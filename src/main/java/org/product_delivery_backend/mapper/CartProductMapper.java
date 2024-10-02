package org.product_delivery_backend.mapper;


import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;

import org.product_delivery_backend.entity.CartProduct;

@Mapper(componentModel = "spring")
public interface CartProductMapper {

    CartProduct toCart (CartProductResponseDto cartProductResponseDto);

    @Mapping(source = "cartProduct.cart.id", target = "cartId")
    @Mapping(source = "cartProduct.product.id", target = "productId")
    CartProductResponseDto toCartProductResponseDto(CartProduct cartProduct);


}
