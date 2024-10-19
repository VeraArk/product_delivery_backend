package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {


    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);
    List<CartProduct> findByCartId(Long cartId); ;
    void deleteAllByCartId(Long cartId);
    void deleteByCartIdAndProductId (Long cartId, Long productId);
}
