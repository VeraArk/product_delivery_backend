package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.product_delivery_backend.entity.CartProduct;
import org.product_delivery_backend.entity.OrderProduct;

@Mapper(componentModel = "spring")
public interface OrderProductMapper {

    @Mapping(source = "cart", target = "order")
    OrderProduct toOrderProduct(CartProduct cartProduct);
}
