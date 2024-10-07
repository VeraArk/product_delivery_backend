package org.product_delivery_backend.controller;

import lombok.RequiredArgsConstructor;

import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;
import org.product_delivery_backend.dto.productDto.*;
import org.product_delivery_backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{userId}/{productId}")
    public ResponseEntity<CartProductResponseDto> addItemToCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(cartService.addProductToCart(userId, productId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartProductResponseDto>> getAllProductsInCart(@PathVariable Long userId) {
        List<CartProductResponseDto> list = cartService.getProductsInCart(userId);
        return ResponseEntity.ok(list);
    }

    // удалить ненужный продукт
    @DeleteMapping("/{cartId}/{productId}")
    public ResponseEntity<?> deleteByCartItemId(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok("Product removed");
    }

    // очистить корзину
    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart is empty");
    }

    //update корзіны
    @PutMapping("/{cartId}/{productId}/{productQuantity}")
    public ResponseEntity<CartProductResponseDto> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer productQuantity)
    {
        CartProductResponseDto cartProductResponseDto = cartService.updateCartProduct(cartId, productId, productQuantity);
        return ResponseEntity.ok(cartProductResponseDto);
    }
}
