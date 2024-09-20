package org.product_delivery_backend.service;

import lombok.Data;
import org.product_delivery_backend.DTO.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.mapper.ProductMapper;
import org.product_delivery_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ProductService {

   private final ProductRepository productRepository;
   private final ProductMapper productMapper;

    public List<ProductResponseDto> findAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toProductResponseDTO)
                .collect(Collectors.toList());
    }
}
