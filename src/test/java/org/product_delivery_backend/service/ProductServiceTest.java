package org.product_delivery_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.productDto.AllProductResponseDto;
import org.product_delivery_backend.dto.productDto.ProductRequestDto;
import org.product_delivery_backend.dto.productDto.ProductResponseDto;
import org.product_delivery_backend.entity.Product;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.product_delivery_backend.mapper.ProductMapper;
import org.product_delivery_backend.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private AllProductResponseDto allProductResponseDto;
    private AllProductResponseDto allProductResponseDto2;
    private Product product;
    private Product product2;
    private ProductRequestDto productRequestDto;
    private ProductResponseDto productResponseDto;


    @BeforeEach
    void setUp() {
        allProductResponseDto = AllProductResponseDto.builder()
                .id(1L)
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .minQuantity("250g")
                .photoLink("wwww.anysite.com")
                .build();

        allProductResponseDto2 = AllProductResponseDto.builder()
                .id(2L)
                .title("Product2")
                .price(BigDecimal.valueOf(0.99))
                .minQuantity("550g")
                .photoLink("wwww.anysite.com")
                .build();

        product = Product.builder()
                .id(1L)
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .minQuantity("250g")
                .photoLink("wwww.anysite.com")
                .build();

        product2 = Product.builder()
                .id(2L)
                .title("Product2")
                .price(BigDecimal.valueOf(0.99))
                .minQuantity("550g")
                .photoLink("wwww.anysite.com")
                .build();

        productRequestDto = ProductRequestDto.builder()
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .productCode("200-PR76544")
                .minQuantity("250g")
                .description("very good product")
                .photoLink("wwww.anysite.com")
                .build();

        productResponseDto = ProductResponseDto.builder()
                .id(1L)
                .title("Product1")
                .price(BigDecimal.valueOf(1.99))
                .productCode("200-PR76544")
                .minQuantity("250g")
                .description("very good product")
                .photoLink("wwww.anysite.com")
                .build();

    }

    @Test
    void getListProduct_success() {

        when(productRepository.findAll()).thenReturn(List.of(product, product2));
        when(productMapper.toAllProductResponseDTO(product)).thenReturn(allProductResponseDto);
        when(productMapper.toAllProductResponseDTO(product2)).thenReturn(allProductResponseDto2);

        List<AllProductResponseDto> result = productService.findAllProduct();

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(allProductResponseDto, result.get(0));
        assertEquals(allProductResponseDto2, result.get(1));

        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toAllProductResponseDTO(product);
        verify(productMapper, times(1)).toAllProductResponseDTO(product2);
    }

    @Test
    void findAllProduct_emptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> productService.findAllProduct());;
        verify(productRepository).findAll();
    }

    @Test
    void findAllProductPage_success() {
        Page<Product> productPage = new PageImpl<>(List.of(product, product2));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);
        when(productMapper.toAllProductResponseDTO(product)).thenReturn(allProductResponseDto);
        when(productMapper.toAllProductResponseDTO(product2)).thenReturn(allProductResponseDto2);

        Page<AllProductResponseDto> result = productService.findAllProductPage(PageRequest.of(0, 2));
        // 0-номер первой страницы при пагинации. 2- количество элементов на странице.
        //PageRequest.of(0, 2) создает запрос на первую страницу (с индексом 0), где будет содержаться 2 продукта.

        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void findAllProductPage_emptyPage() {
        Page<Product> productPage = new PageImpl<>(List.of());
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        Page<AllProductResponseDto> result = productService.findAllProductPage(PageRequest.of(0, 2));

        assertTrue(result.isEmpty());
    }

    @Test
    void addProduct_success() {
        when(productMapper.toProduct(productRequestDto)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toProductResponseDTO(product)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.addProduct(productRequestDto);

        assertEquals(result, productResponseDto);

        verify(productRepository, times(1)).save(product);

    }

    @Test
    void deleteProduct_success() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_productNotFound() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
    }
    @Test
    void findProductById_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toProductResponseDTO(product)).thenReturn(productResponseDto);

        ProductResponseDto result = productService.findProductById(1L);

        assertEquals(productResponseDto, result);
    }

    @Test
    void findProductById_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.findProductById(1L));
    }

    @Test
    void findProductByIdInCart_success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findProductByIdInCart(1L);

        assertEquals(product, result);
    }

    @Test
    void findProductByIdInCart_notFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.findProductByIdInCart(1L));
    }
}