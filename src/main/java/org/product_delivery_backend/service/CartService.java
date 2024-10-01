package org.product_delivery_backend.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import org.product_delivery_backend.dto.productDto.ProductResponseDto;
import org.product_delivery_backend.entity.*;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.mapper.CartItemMapper;
import org.product_delivery_backend.mapper.ProductMapper;
import org.product_delivery_backend.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;
    private final ProductService productService;

    @Transactional
    public void addProductToCart(Long userId, Long productId) {
        Optional<Cart> optionalCart = cartRepository.findCartByUserId(userId);
        Cart existCart = optionalCart.orElseGet(() -> createNewCartForUser(userId));

        Optional<CartProduct> optionalCartProduct = cartProductRepository.findByCartIdAndProductId(existCart.getId(), productId);
        CartProduct existCartProduct;

        Product product = productService.findProductById2(productId);

        if (optionalCartProduct.isPresent()) {
            // Продукт уже в корзине, обновляем количество и сумму
            existCartProduct = optionalCartProduct.get();
            int newQuantity = existCartProduct.getProductQuantity() + 1;
            existCartProduct.setProductQuantity(newQuantity);
            existCartProduct.setSum(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        } else {
            // Продукта еще нет в корзине, добавляем новый
            existCartProduct = new CartProduct(existCart, product, 1);
            existCartProduct.setSum(product.getPrice());  // Изначально сумма равна цене за один продукт
        }
        cartProductRepository.save(existCartProduct);
    }
    @Transactional
    public void removeProductFromCart(Long cartId, Long productId) {
        Optional<CartProduct> optionalCartProduct = cartProductRepository.findByCartIdAndProductId(cartId, productId);
        if (optionalCartProduct.isEmpty()) {
            throw new NotFoundException("There is no such product in the cart");
        } else {
            cartProductRepository.deleteByCartIdAndProductId(cartId, productId);
        }
    }

    public List<ProductResponseDto> getProductsInCart(Long cartId) {
        // Получаем список всех CartProduct по id корзины
        List<CartProduct> cartProducts = cartProductRepository.findByCartId(cartId);
        if (cartProducts.isEmpty()) {
            throw new NotFoundException("There are no products in the cart");
        }
        // Преобразуем список CartProduct в список ProductResponseDto
        return cartProducts.stream()
                .map(cartProduct -> productMapper.toProductResponseDTO(cartProduct.getProduct())) // Из каждого CartProduct берем Product и маппим в DTO
                .toList();
    }
    @Transactional
    public void clearCart(Long cartId) {
        cartProductRepository.deleteAllByCartId(cartId);
    }

    // доп метод сервіса - внутренній

    public Cart createNewCartForUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new NotFoundException("User not found"));
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}






