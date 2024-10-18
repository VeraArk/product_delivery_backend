package org.product_delivery_backend.service;

import lombok.Data;

import org.product_delivery_backend.dto.productDto.*;
import org.product_delivery_backend.entity.Category;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.mapper.ProductMapper;
import org.product_delivery_backend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);


    public List<AllProductResponseDto> findAllProduct() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new NotFoundException("No products found.");
        }
        return products.stream()
                .map(productMapper::toAllProductResponseDTO)
                .collect(Collectors.toList());
    }

    public Page<AllProductResponseDto> findAllProductPage(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::toAllProductResponseDTO);
    }

    public Page<AllProductResponseDto> findProductsByCategory(String categoryName, Pageable pageable) {
        Category category;
        category = Category.fromString(categoryName);

        String categoryCode = Category.valueOf(String.valueOf(category)).getCode();
        String productCodePattern = categoryCode + "%";

        System.out.println("productCodePattern" + productCodePattern);

        Page<Product> products = productRepository.findByProductCodeLike(productCodePattern, pageable);

        log.info("Number of products found: {}", products.getTotalElements());
        products.forEach(product -> log.info("Product details: {}", product));

        return products.map(productMapper::toAllProductResponseDTO);
    }

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        Product product = productMapper.toProduct(productRequestDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponseDTO(savedProduct);
    }

    public void deleteProduct(Long productId) {
        if (productRepository.existsById(productId)) {
            productRepository.deleteById(productId);
        } else {
            throw new NotFoundException("Product with id " + productId + " not found");
        }
    }

    public ProductResponseDto findProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("This product does not exist"));
        return productMapper.toProductResponseDTO(product);
    }

    public Product findProductByIdInCart(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("This product does not exist"));
        return product;
    }
}
