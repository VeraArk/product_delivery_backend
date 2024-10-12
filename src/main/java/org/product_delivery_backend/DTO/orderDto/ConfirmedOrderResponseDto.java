package org.product_delivery_backend.dto.orderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.product_delivery_backend.dto.OrderProduct.OrderProductResponseDto;
import org.product_delivery_backend.entity.OrderProduct;
import org.product_delivery_backend.entity.OrderStatus;
import org.product_delivery_backend.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ConfirmedOrderResponseDto {

    private Long id;
    private Long userId;
    private LocalDateTime orderTime;
    private String address;
    private LocalDateTime deliveryTime;
    private List<OrderProduct> orderProducts;
    private BigDecimal totalSum;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;

}
