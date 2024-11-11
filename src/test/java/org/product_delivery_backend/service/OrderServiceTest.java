package org.product_delivery_backend.service;

import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.orderDto.OrderRequestDto;
import org.product_delivery_backend.dto.orderDto.OrderResponseDto;
import org.product_delivery_backend.dto.orderDto.UpdateStatusOrderResponseDto;
import org.product_delivery_backend.entity.*;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.exceptions.OrderException;
import org.product_delivery_backend.exceptions.PaymentException;
import org.product_delivery_backend.mapper.OrderMapper;
import org.product_delivery_backend.mapper.OrderProductMapper;
import org.product_delivery_backend.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartService cartService;
    @Mock
    private OrderProductMapper orderProductMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderProductRepository orderProductRepository;
    @Mock
    private CartProductRepository cartProductRepository;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;

    User user;
    Cart cart;
    CartProduct cartProduct;
    Product product;
    OrderProduct orderProduct;
    Order order;
    OrderResponseDto orderResponseDto;
    OrderRequestDto orderRequestDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .email("ggggg@mail.ru")
                .password("hhhhhhh")
                .phone("9989887655")
                .build();

        cart = Cart.builder()
                .id(7L)
                .user(user)
                .build();

        product = Product.builder()
                .id(5L)
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .minQuantity("250g")
                .photoLink("wwww.anysite.com")
                .build();


        cartProduct = CartProduct.builder()
                .id(2L)
                .cart(cart)
                .product(product)
                .productQuantity(2)
                .sum(BigDecimal.valueOf(3.98))
                .build();

        order = new Order();
        order.setId(1L);

        orderProduct = OrderProduct.builder()
                .id(3L)
                .order(order)
                .product(product)
                .productQuantity(2)
                .sum(BigDecimal.valueOf(3.98))
                .build();

        order.setOrderProducts(List.of(orderProduct));

        orderRequestDto = new OrderRequestDto();

        orderResponseDto = new OrderResponseDto();

    }

    @Test
    void createOrder_Success() {
        List<CartProduct> cartProducts = new ArrayList<>();
        cartProducts.add(cartProduct);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartProductRepository.findByCartId(cart.getId())).thenReturn(cartProducts);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderProductMapper.toOrderProduct(cartProduct)).thenReturn(orderProduct);
        when(orderMapper.toOrderResponseDto(any(Order.class))).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.createOrder(user.getId());
        assertNotNull(result);

        verify(orderRepository, times(2)).save(any(Order.class));

        verify(cartService).clearCart(cart.getId());
    }
    @Test
    void createOrder_UserNotFound_ThrowsNotFoundException() {

        Long userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(userId));
    }

    @Test
    void createOrder_CartNotFound_ThrowsNotFoundException() {

        Long userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findCartByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(userId));
    }

    @Test
    void confirmOrder_Success() throws StripeException {

        orderRequestDto.setId(1L);
        orderRequestDto.setDeliveryTime(LocalDateTime.now().toString());
        orderRequestDto.setAddress("Test Address");
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toUpdateStatusOrderResponseDto(order)).thenReturn(new UpdateStatusOrderResponseDto());
        when(paymentService.createPayment(order)).thenReturn("payment_url");

        UpdateStatusOrderResponseDto result = orderService.confirmOrder(orderRequestDto);

        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void confirmOrder_OrderNotFound_ThrowsNotFoundException() {

        orderRequestDto.setId(1L);
        when(orderRepository.findById(orderRequestDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.confirmOrder(orderRequestDto));
    }

    @Test
    void payForOrder_Success() {

        Long orderId = order.getId();
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toUpdateStatusOrderResponseDto(order)).thenReturn(new UpdateStatusOrderResponseDto());

        UpdateStatusOrderResponseDto result = orderService.payForOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void payForOrder_InvalidPaymentMethod_ThrowsPaymentException() {

        Long orderId = order.getId();
        order.setPaymentMethod(PaymentMethod.UNKNOWN);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(PaymentException.class, () -> orderService.payForOrder(orderId));
    }

    @Test
    void cancelOrder_Success() throws OrderException {
        Long orderId = order.getId();
        order.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toUpdateStatusOrderResponseDto(order)).thenReturn(new UpdateStatusOrderResponseDto());

        UpdateStatusOrderResponseDto result = orderService.cancelOrder(orderId);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_PaidOrder_ThrowsOrderException() {

        Long orderId = order.getId();
        order.setOrderStatus(OrderStatus.PAID);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(OrderException.class, () -> orderService.cancelOrder(orderId));
    }

    @Test
    void clearOrder_Success() {

        Long orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.clearOrder(orderId);

        verify(orderProductRepository).deleteByOrder(order);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void findOrderById_Success() {

        Long orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderResponseDto(order)).thenReturn(new OrderResponseDto());

        OrderResponseDto result = orderService.findOrderById(orderId);

        assertNotNull(result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void findOrderById_OrderNotFound_ThrowsNoSuchElementException() {

        Long orderId = 1L;  // Пример ID заказа

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> orderService.findOrderById(orderId));
    }
}
