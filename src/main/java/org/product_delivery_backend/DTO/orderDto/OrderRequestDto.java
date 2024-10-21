package org.product_delivery_backend.dto.orderDto;

import lombok.Data;
import org.product_delivery_backend.entity.PaymentMethod;

@Data
public class OrderRequestDto {

    private Long id;
    private String address;
    private String deliveryTime;
    private PaymentMethod paymentMethod;

}


