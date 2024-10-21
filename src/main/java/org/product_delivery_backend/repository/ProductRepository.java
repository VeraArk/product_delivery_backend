package org.product_delivery_backend.repository;

import org.product_delivery_backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByProductCodeLike(String productCodePattern, Pageable pageable);
}
