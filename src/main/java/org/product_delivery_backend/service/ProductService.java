package org.product_delivery_backend.service;

import lombok.Data;

import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.exceptions.NotFoundException;
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

    public List<Product> findAllProduct() {
        return productRepository.findAll();

    }

    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("This product does not exist"));
        return productMapper.toProductResponseDTO(product);
    }

}
