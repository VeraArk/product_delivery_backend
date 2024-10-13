package org.product_delivery_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart controller")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Operation(summary = "Add item to cart",
            description = "Add a product to the user's cart by product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added to cart successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping("/{productId}")
    public ResponseEntity<CartProductResponseDto> addItemToCart(@PathVariable Long productId) {
        User user = userService.getUser();
        return ResponseEntity.ok(cartService.addProductToCart(user.getId(), productId));
    }

    @Operation(summary = "Get all products in cart",
            description = "Retrieve a list of all products currently in the user's cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No products found in cart", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<CartProductResponseDto>> getAllProductsInCart() {
        User user = userService.getUser();
        List<CartProductResponseDto> list = cartService.getProductsInCart(user.getId());
        return ResponseEntity.ok(list);
    }

    // удалить ненужный продукт
    @Operation(summary = "Remove product from cart",
            description = "Remove a product from the user's cart by product ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product removed from cart successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Product not found in cart", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteByCartItemId(@PathVariable Long productId) {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        cartService.removeProductFromCart(cartId, productId);
        return ResponseEntity.ok("Product removed");
    }

    // очистить корзину
    @Operation(summary = "Clear cart",
            description = "Remove all products from the user's cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Cart not found for the user", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping()
    public ResponseEntity<?> clearCart() {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        cartService.clearCart(cartId);
        return ResponseEntity.ok("Cart is empty");
    }

    //update корзины
    @Operation(summary = "Update product quantity in cart",
            description = "Update the quantity of a specific product in the user's cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product quantity updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CartProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found in cart", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid product ID or quantity", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PutMapping("/{productId}/{productQuantity}")
    public ResponseEntity<CartProductResponseDto> updateCartProduct(@PathVariable Long productId, @PathVariable Integer productQuantity) {
        User user = userService.getUser();
        Long cartId = cartService.findCartByUserId(user.getId());
        CartProductResponseDto cartProductResponseDto = cartService.updateCartProduct(cartId, productId, productQuantity);
        return ResponseEntity.ok(cartProductResponseDto);
    }


}