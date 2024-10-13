package org.product_delivery_backend.service;

import lombok.Data;
import org.product_delivery_backend.dto.OrderProduct.OrderProductResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.entity.*;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.exceptions.PaymentException;
import org.product_delivery_backend.mapper.CartProductMapper;
import org.product_delivery_backend.mapper.OrderMapper;
import org.product_delivery_backend.mapper.OrderProductMapper;
import org.product_delivery_backend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartProductRepository cartProductRepository;
    private final UserService userService;


    public OrderResponseDto createOrder(Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));

       Optional<Cart> cart = cartRepository.findCartByUserId(userId);
       Cart exsistCart = cart.orElseThrow(()->new NotFoundException("Cart not found"));

        Order order = Order.builder()
                .user(user)
                .orderTime(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .totalSum(BigDecimal.ZERO) // Временно ставим 0, потом обновим
                .build();

        orderRepository.save(order); // сохраняем заказ
        List <CartProduct> cartProductList = cartProductRepository.findByCartId(exsistCart.getId());

        List<OrderProduct> orderProducts = cartProductList.stream()
                .map(cartProduct -> {
                    Product product = productRepository.findById(cartProduct.getProduct().getId())
                            .orElseThrow(() -> new NotFoundException("Product not found for ID: " + cartProduct.getProduct().getId()));

                    OrderProduct orderProduct = orderProductMapper.toOrderProduct(cartProduct);
                    orderProduct.setOrder(order); // связываем с заказом
                    orderProduct.setProduct(product); // связываем с продуктом
                    return orderProduct;
                })
                .collect(Collectors.toList());

        orderProductRepository.saveAll(orderProducts);

        order.setOrderProducts(orderProducts);

        BigDecimal totalSum = orderProducts.stream()
                .map(OrderProduct::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalSum(totalSum);

        orderRepository.save(order);

        Optional<Cart> optionalCart = cartRepository.findCartByUserId(userId);
        Cart existCart = optionalCart.orElseThrow(() -> new NotFoundException("Cart not found for user ID: " + userId));
        cartService.clearCart(existCart.getId());

        List<OrderProductResponseDto> orderProductResponseDto = orderProducts.stream().
                map(orderProductMapper::toOrderProductResponseDto).toList();

        OrderResponseDto orderResponseDto = orderMapper.toOrderResponseDto(order);
        orderResponseDto.setOrderProducts(orderProductResponseDto);
        return orderResponseDto;
    }

    public UpdateStatusOrderResponseDto confirmOrder(OrderRequestDto orderRequestDto) {
        Optional<Order> optionalOrder = orderRepository.findById(orderRequestDto.getId());// orderId
        Order existOrder = optionalOrder.orElseThrow(() -> new NotFoundException("Order with ID: " + orderRequestDto.getId()+ " is not found"));

        String deliveryTimeString =  orderRequestDto.getDeliveryTime();
        LocalDateTime deliveryTime = LocalDateTime.parse(deliveryTimeString);
        existOrder.setDeliveryTime(deliveryTime);

        existOrder.setAddress(orderRequestDto.getAddress());
        existOrder.setOrderStatus(OrderStatus.CONFIRMED);
        existOrder.setPaymentMethod(orderRequestDto.getPaymentMethod());

        orderRepository.save(existOrder);
        return orderMapper.toUpdateStatusOrderResponseDto(existOrder);
    }

    public UpdateStatusOrderResponseDto payForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for ID: " + orderId));
        PaymentMethod paymentMethod = order.getPaymentMethod();
        if (validatePaymentMethod(paymentMethod)) {
            order.setOrderStatus(OrderStatus.PAID);
            order.setPaymentMethod(paymentMethod);
            orderRepository.save(order);
        } else {
            throw new PaymentException("Invalid payment method");
        }
        return orderMapper.toUpdateStatusOrderResponseDto(order);
    }

    public UpdateStatusOrderResponseDto cancelOrder(Long orderId) throws OrderException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found for ID: " + orderId));

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new OrderException("Cannot cancel a paid order");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return orderMapper.toUpdateStatusOrderResponseDto(order);
    }

    public void clearOrder(Long orderId) {

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order existOrder = optionalOrder.orElseThrow(() -> new NotFoundException("Order with ID: " + orderId + " not found"));

        orderProductRepository.deleteByOrder(existOrder);
        orderRepository.deleteById(orderId);
    }



    // внутренніе методы

    private boolean validatePaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CREDIT_CARD ||
                paymentMethod == PaymentMethod.PAYPAL ||
                paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

}





