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
import org.product_delivery_backend.mapper.CartProductMapper;
import org.product_delivery_backend.mapper.ProductMapper;
import org.product_delivery_backend.repository.CartProductRepository;
import org.product_delivery_backend.repository.CartRepository;
import org.product_delivery_backend.repository.ProductRepository;
import org.product_delivery_backend.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Arrays;
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
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartProduct cartProduct;
    private CartProduct cartProduct2;
    private CartProductResponseDto cartProductResponseDto;
    private CartProductResponseDto cartProductResponseDto2;
    private Product product;
    private Product product2;
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

        product2 = Product.builder()
                .id(9L)
                .title("Product2")
                .price(BigDecimal.valueOf(3.99))
                .minQuantity("1l")
                .photoLink("wwww.anysite.com")
                .build();


        user = User.builder()
                .id(6L)
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

        cartProduct2 = CartProduct.builder()
                .id(5L)
                .cart(cart)
                .product(product2)
                .build();

        cartProductResponseDto = CartProductResponseDto.builder()
                .id(1L)
                .cartId(7L)
                .productId(5L)
                .productQuantity(1)
                .sum(BigDecimal.valueOf(1.99))
                .build();

        cartProductResponseDto2 = CartProductResponseDto.builder()
                .id(5L)
                .cartId(7L)
                .productId(9L)
                .productQuantity(1)
                .sum(BigDecimal.valueOf(3.99))
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

        // Используем any() для игнорирования конкретных значений полей в CartProduct
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
    void getProductsInCart() {

        List<CartProduct> cartProducts = Arrays.asList(cartProduct, cartProduct2);

        when(cartProductRepository.findByCartId(cart.getId())).thenReturn(cartProducts);
        when(cartProductMapper.toCartProductResponseDto(cartProducts.get(0))).thenReturn(cartProductResponseDto);
        when(cartProductMapper.toCartProductResponseDto(cartProducts.get(1))).thenReturn(cartProductResponseDto2);

        List<CartProductResponseDto> result = cartService.getProductsInCart(cart.getId());

        assertEquals(2, result.size());
        verify(cartProductRepository).findByCartId(cart.getId());
        verify(cartProductMapper).toCartProductResponseDto(cartProducts.get(0));
        verify(cartProductMapper).toCartProductResponseDto(cartProducts.get(1));
    }

    @Test
    void ClearCart() {

        cartService.clearCart(cart.getId());

        verify(cartProductRepository).deleteAllByCartId(cart.getId());
    }

    @Test
    void updateCartProduct() {

        int productQuantity =2;

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


