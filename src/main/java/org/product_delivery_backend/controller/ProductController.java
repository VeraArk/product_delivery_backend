package org.product_delivery_backend.controller;

import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.productDTO.AllProductResponseDto;
import org.product_delivery_backend.dto.productDTO.ProductRequestDto;
import org.springframework.web.bind.annotation.*;
import org.product_delivery_backend.dto.productDTO.ProductResponseDto;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<AllProductResponseDto>> findAll() {
        return new ResponseEntity<>(productService.findAllProduct(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productService.addProduct(productRequestDto);
        return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findByID(@PathVariable("id") Long id) {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);

    }
}
