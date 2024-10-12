package org.product_delivery_backend.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;
import org.product_delivery_backend.entity.Cart;
import org.product_delivery_backend.entity.CartProduct;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.mapper.CartProductMapper;
import org.product_delivery_backend.repository.CartProductRepository;
import org.product_delivery_backend.repository.CartRepository;
import org.product_delivery_backend.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartProductRepository cartProductRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartProductMapper cartProductMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartProduct cartProduct;
    private CartProductResponseDto cartProductResponseDto;
    private Product product;
    private User user;

    @BeforeEach
    public void setUp() {

        product = Product.builder()
                .id(5L)
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .minQuantity("250g")
                .photoLink("wwww.anysite.com")
                .build();


        user = User.builder()
                .id(7L)
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

        cartProduct = CartProduct.builder()
                .id(6L)
                .cart(cart)
                .product(product)
                .build();

        cartProductResponseDto = CartProductResponseDto.builder()
                .id(1L)
                .cartId(7L)
                .productId(5L)
                .productQuantity(1)
                .sum(BigDecimal.valueOf(1.99))
                .build();

    }

    @Test
    void addProductToCartTest() {

        when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);  // Возвращаем корзину после сохранения

        when(productService.findProductByIdInCart(product.getId())).thenReturn(product);

        when(cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());

        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

        // Используем any(), чтобы избежать конкретных значений полей в CartProduct
        when(cartProductMapper.toCartProductResponseDto(any(CartProduct.class))).thenReturn(cartProductResponseDto);

        CartProductResponseDto result = cartService.addProductToCart(user.getId(), product.getId());

        assertNotNull(result);

        verify(cartRepository).findCartByUserId(user.getId());
        verify(cartRepository).save(any(Cart.class));
        verify(productService).findProductByIdInCart(product.getId());
        verify(cartProductRepository).save(any(CartProduct.class));
        verify(cartProductMapper).toCartProductResponseDto(any(CartProduct.class));
    }

    @Test
    void removeProductFromCart() {

        when(cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.of(cartProduct));

        cartService.removeProductFromCart(cart.getId(), product.getId());

        verify(cartProductRepository).deleteByCartIdAndProductId(cart.getId(), product.getId());
    }

    @Test
    void getProductsInCart_cartExist() {

        when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartProductRepository.findByCartId(cart.getId())).thenReturn(List.of(cartProduct));
        when(cartProductMapper.toCartProductResponseDto(cartProduct)).thenReturn(cartProductResponseDto);

        List<CartProductResponseDto> result = cartService.getProductsInCart(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());  // Убеждаемся, что один продукт в корзине
        verify(cartRepository).findCartByUserId(user.getId());
        verify(cartProductRepository).findByCartId(cart.getId());
        verify(cartProductMapper).toCartProductResponseDto(cartProduct);
    }

    @Test
    void getProductsInCart_cartNotFound_noProducts() {

        when(cartRepository.findCartByUserId(user.getId())).thenReturn(Optional.empty());
        when(cartProductRepository.findByCartId(0L)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> cartService.getProductsInCart(user.getId()));

        verify(cartRepository).findCartByUserId(user.getId());
        verify(cartProductRepository).findByCartId(0L);
    }

    @Test
    void ClearCart() {

        cartService.clearCart(cart.getId());

        verify(cartProductRepository).deleteAllByCartId(cart.getId());
    }

    @Test
    void updateCartProduct() {

        int productQuantity = 2;

        when(cartProductRepository.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.of(cartProduct));
        when(productService.findProductByIdInCart(product.getId())).thenReturn(product);
        when(cartProductMapper.toCartProductResponseDto(cartProduct)).thenReturn(cartProductResponseDto);

        CartProductResponseDto result = cartService.updateCartProduct(cart.getId(), product.getId(), productQuantity);

        assertNotNull(result);
        assertEquals(productQuantity, cartProduct.getProductQuantity());
        assertEquals(product.getPrice().multiply(BigDecimal.valueOf(productQuantity)), cartProduct.getSum());

        verify(cartProductRepository).findByCartIdAndProductId(cart.getId(), product.getId());
        verify(cartProductMapper).toCartProductResponseDto(cartProduct);
    }
}


