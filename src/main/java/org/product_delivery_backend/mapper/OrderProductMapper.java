package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.OrderProduct.OrderProductResponseDto;
import org.product_delivery_backend.entity.CartProduct;
import org.product_delivery_backend.entity.OrderProduct;

@Mapper(componentModel = "spring")
public interface OrderProductMapper {

    @Mapping(target = "order", ignore = true)
    OrderProduct toOrderProduct(CartProduct cartProduct);

    @Mapping(source = "orderProduct.order.id", target = "orderId")
    @Mapping(source = "orderProduct.product.id", target = "productId")
    OrderProductResponseDto toOrderProductResponseDto (OrderProduct orderProduct);
}
