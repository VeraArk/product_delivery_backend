package org.product_delivery_backend.mapper;


import org.mapstruct.Mapper;
import org.product_delivery_backend.dto.cartPrductDto.CartProductResponseDto;
import org.product_delivery_backend.entity.CartProduct;

@Mapper(componentModel = "spring")
public interface CartProductMapper {


    CartProduct toCart (CartProductResponseDto cartProductResponseDto);
    CartProductResponseDto toCartProductResponseDto(CartProduct cartProduct);


}
