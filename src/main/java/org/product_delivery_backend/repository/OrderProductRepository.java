package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.Order;
import org.product_delivery_backend.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
   void deleteByOrder (Order order);
}
