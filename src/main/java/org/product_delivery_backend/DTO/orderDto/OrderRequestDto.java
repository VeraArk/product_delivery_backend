package org.product_delivery_backend.dto.orderDto;

import lombok.Data;
import org.product_delivery_backend.dto.OrderProduct.OrderProductRequestDto;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderRequestDto {

    private Long id;
    private String address;
    private String deliveryTime;
    private PaymentMethod paymentMethod;

}


