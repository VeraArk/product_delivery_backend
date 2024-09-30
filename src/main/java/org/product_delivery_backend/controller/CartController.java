package org.product_delivery_backend.controller;


import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
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
    public ResponseEntity<String> addItemToCart(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        cartService.addProductToCart(userId, productId);
        return ResponseEntity.ok("Product added to cart successfully.");
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<ProductResponseDto>> getAllProductsInCart(@PathVariable Long cartId) {
        List<ProductResponseDto> list = cartService.getProductsInCart(cartId);
        return ResponseEntity.ok(list);
    }

    // удалить ненужный продукт
    @DeleteMapping("/{cartId}/{productId}")
    public ResponseEntity<?> deleteByCartItemId(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok("Product removed");
    }

    // очистить корзину
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart is empty");
    }
}

