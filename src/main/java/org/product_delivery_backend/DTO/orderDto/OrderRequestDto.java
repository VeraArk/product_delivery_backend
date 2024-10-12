package org.product_delivery_backend.dto.orderDto;

import lombok.Data;
import org.product_delivery_backend.dto.OrderProduct.OrderProductRequestDto;
import org.product_delivery_backend.entity.OrderProduct;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderRequestDto {

    private Long userId;
    private String address;
    private LocalDateTime deliveryTime;
    private List<OrderProductRequestDto> orderProductRequestDtoList;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;

}


