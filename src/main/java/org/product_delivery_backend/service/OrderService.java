package org.product_delivery_backend.service;

import lombok.Data;
import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;
import org.product_delivery_backend.dto.orderDto.ConfirmedOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.*;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.exceptions.PaymentException;
import org.product_delivery_backend.mapper.CartProductMapper;
import org.product_delivery_backend.mapper.OrderMapper;
import org.product_delivery_backend.mapper.OrderProductMapper;
import org.product_delivery_backend.repository.CartRepository;
import org.product_delivery_backend.repository.OrderRepsitory;
import org.product_delivery_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class OrderService {

    private final OrderRepsitory orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final OrderProductMapper orderProductMapper;
    private final CartProductMapper cartProductMapper;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;


    public OrderResponseDto createOrder(Long userId) {
        List<CartProductResponseDto> listProductDto = cartService.getProductsInCart(userId);

        List<CartProduct> cartProducts = listProductDto.stream()
                .map(cartProductMapper::toCart)
                .toList();

        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));

        List<OrderProduct> orderProducts = cartProducts.stream()
                .map(orderProductMapper::toOrderProduct)
                .toList();

        BigDecimal totalSum = orderProducts.stream()
                .map(OrderProduct::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .orderProducts(orderProducts)
                .totalSum(totalSum)
                .build();

        orderRepository.save(order);

        // очістка корзіны
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(userId);
        Cart existcart = optionalCart.orElseThrow(() -> new NotFoundException("Order not found for user ID: " + userId));
        cartService.clearCart(existcart.getId());

        return orderMapper.toOrderResponseDto(order);
    }

    public ConfirmedOrderResponseDto confirmOrder(OrderRequestDto orderRequestDto) {
        Optional<Order> optionalOrder = orderRepository.findOrderByUserId(orderRequestDto.getUserId());
        Order existOrder = optionalOrder.orElseThrow(() -> new NotFoundException("Order not found for user ID: " + orderRequestDto.getUserId()));

        existOrder.setAddress(orderRequestDto.getAddress());
        existOrder.setDeliveryTime(orderRequestDto.getDeliveryTime());
        existOrder.setOrderStatus(OrderStatus.CONFIRMED);
        existOrder.setPaymentMethod(orderRequestDto.getPaymentMethod());

        orderRepository.save(existOrder);

        return orderMapper.toConfirmedOrderResponseDto(existOrder);
    }

    public OrderResponseDto payForOrder(Long orderId, PaymentMethod paymentMethod) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for ID: " + orderId));

        if (validatePaymentMethod(paymentMethod)) {
            order.setOrderStatus(OrderStatus.PAID);
            order.setPaymentMethod(paymentMethod);
            orderRepository.save(order);
        } else {
            throw new PaymentException("Invalid payment method");
        }
        return orderMapper.toOrderResponseDto(order);
    }

    public OrderStatus cancelOrder(Long orderId, OrderStatus orderStatus) throws OrderException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for ID: " + orderId));

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new OrderException("Cannot cancel a paid order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return OrderStatus.CANCELLED;
    }

    public void clearOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for ID: " + orderId));
        orderRepository.deleteAll();
    }

    // внутренніе методы

    private boolean validatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CREDIT_CARD ||
                paymentMethod == PaymentMethod.PAYPAL ||
                paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

}





