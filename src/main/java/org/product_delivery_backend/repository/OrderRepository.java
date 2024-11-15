package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByUserId(Long userId);
    List<Order> findAllByUserId(Long userId);
}
