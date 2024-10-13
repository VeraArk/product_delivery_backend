package org.product_delivery_backend.controller;

import lombok.RequiredArgsConstructor;

import org.product_delivery_backend.dto.cartProductDto.CartProductResponseDto;
import org.product_delivery_backend.entity.User;
import org.product_delivery_backend.service.CartService;
import org.product_delivery_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @PostMapping("/{productId}")
    public ResponseEntity<CartProductResponseDto> addItemToCart( @PathVariable Long productId) {
        User user = userService.getUser();
        return ResponseEntity.ok(cartService.addProductToCart(user.getId(), productId));
    }

    @GetMapping()
    public ResponseEntity<List<CartProductResponseDto>> getAllProductsInCart() {
        User user = userService.getUser();
        List<CartProductResponseDto> list = cartService.getProductsInCart(user.getId());
        return ResponseEntity.ok(list);
    }

    // удалить ненужный продукт
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteByCartItemId(@PathVariable Long productId) {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok("Product removed");
    }

    // очистить корзину
    @DeleteMapping()
    public ResponseEntity<?> clearCart() {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart is empty");
    }

    //update корзіны
    @PutMapping("/{productId}/{productQuantity}")
    public ResponseEntity<CartProductResponseDto> updateCartProduct(@PathVariable Long productId, @PathVariable Integer productQuantity) {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        CartProductResponseDto cartProductResponseDto = cartService.updateCartProduct(cartId, productId, productQuantity);
        return ResponseEntity.ok(cartProductResponseDto);
    }


}