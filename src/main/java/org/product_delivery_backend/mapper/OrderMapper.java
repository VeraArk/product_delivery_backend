package org.product_delivery_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "order.user.id", target = "userId")
    OrderResponseDto toOrderResponseDto(Order order);

    UpdateStatusOrderResponseDto toUpdateStatusOrderResponseDto(Order order);
}
