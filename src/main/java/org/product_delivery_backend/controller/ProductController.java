package org.product_delivery_backend.controller;

import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return new ResponseEntity<>(productService.findAllProduct(), HttpStatus.OK);
    }

    @GetMapping("/findById")
    public ResponseEntity<ProductResponseDto> findByID(@RequestParam(value = "id") Long id) {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }
}
