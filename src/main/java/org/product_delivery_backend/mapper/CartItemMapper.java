package org.product_delivery_backend.mapper;


import org.mapstruct.Mapper;
import org.product_delivery_backend.dto.cartItemDto.CartItemResponseDto;
import org.product_delivery_backend.entity.CartProduct;

@Mapper(componentModel = "spring")
public interface CartItemMapper {


    CartProduct toCart (CartItemResponseDto cartItemResponseDto);
    CartItemResponseDto toCartItemResponseDto(CartProduct cartItem);

}
