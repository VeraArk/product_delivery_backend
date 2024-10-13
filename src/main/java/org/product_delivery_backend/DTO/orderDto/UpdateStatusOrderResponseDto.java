package org.product_delivery_backend.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UpdateStatusOrderResponseDto {

    private Long id;
    private String address;
    private LocalDateTime deliveryTime;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;

}
