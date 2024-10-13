package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.Cart;
import org.product_delivery_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepsitory extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByUserId(Long userId);
}
