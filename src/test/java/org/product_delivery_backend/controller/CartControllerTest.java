package org.product_delivery_backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.service.CartService;
import org.product_delivery_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "test@example.com")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;


       private User user;


    private CartProductResponseDto cartProductResponseDto;


    @BeforeEach
    void setUp(){

        user= new User();
        user.setId(1L);
        user.setEmail("test@example.com");


        cartProductResponseDto = CartProductResponseDto.builder()
                .id(2L)
                .cartId(1L)
                .productId(3L)
                .productQuantity(2)
                .sum(new BigDecimal("10.00"))
                .build();

    }

    @Test
    void addItemToCart_ShouldReturnOk() throws Exception {
        Long userId= 1L;
        Long productId =3L;

        when(userService.getUser()).thenReturn(user);

        when(cartService.addProductToCart(userId, productId)).thenReturn(cartProductResponseDto);

        mockMvc.perform(post("/api/cart/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":2,\"cartId\":1,\"productId\":3,\"productQuantity\":2,\"sum\":10.00}"));

        verify(cartService, times(1)).addProductToCart(1L, 3L);
    }

    @Test
    void addItemToCart_UserNotFound_ShouldThrowNotFoundException() throws Exception {
        when(userService.getUser()).thenThrow(new NotFoundException("User not found."));

        mockMvc.perform(post("/api/cart/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found."));
    }

    @Test
    void getAllProductsInCart_ShouldReturnProductList() throws Exception {
        when(userService.getUser()).thenReturn(user);

        when(cartService.getProductsInCart(1L)).thenReturn(List.of(cartProductResponseDto));

        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(cartService, times(1)).getProductsInCart(1L);
    }

    @Test
    void getAllProductsInCart_EmptyCart_ShouldThrowNotFoundException() throws Exception {
        when(userService.getUser()).thenReturn(user);

        when(cartService.getProductsInCart(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart is empty."));
    }

    @Test
    void deleteByCartItemId_ShouldReturnOk() throws Exception {
        when(userService.getUser()).thenReturn(user);
        when(cartService.findCartByUserId(1L)).thenReturn(1L);

        mockMvc.perform(delete("/api/cart/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Product removed"));

        verify(cartService, times(1)).removeProductFromCart(1L, 3L);
    }

    @Test
    void clearCart_ShouldReturnOk() throws Exception {
        when(userService.getUser()).thenReturn(user);
        when(cartService.findCartByUserId(1L)).thenReturn(1L);

        mockMvc.perform(delete("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart is empty"));

        verify(cartService, times(1)).clearCart(1L);
    }

    @Test
    void updateCartProduct_ShouldReturnUpdatedProduct() throws Exception {
        when(userService.getUser()).thenReturn(user);
        when(cartService.findCartByUserId(1L)).thenReturn(1L);

        cartProductResponseDto.setProductQuantity(5);

        when(cartService.updateCartProduct(1L, 3L, 5)).thenReturn(cartProductResponseDto);

        mockMvc.perform(put("/api/cart/3/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productQuantity").value(5));

        verify(cartService, times(1)).updateCartProduct(1L, 3L, 5);
    }


    @Test
    void updateCartProduct_NotFoundUser_ShouldThrowNotFoundException() throws Exception {
        when(userService.getUser()).thenReturn(null);

        mockMvc.perform(put("/api/cart/3/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found."));

    }


    @Test
    void updateCartProduct_NotFoundCart_ShouldThrowNotFoundException() throws Exception {
        when(userService.getUser()).thenReturn(user);
        when(cartService.findCartByUserId(user.getId())).thenReturn(-1L);

        mockMvc.perform(put("/api/cart/3/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found for user."));

    }

}