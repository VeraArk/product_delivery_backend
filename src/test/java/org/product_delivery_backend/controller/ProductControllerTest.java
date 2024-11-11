
package org.product_delivery_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.product_delivery_backend.dto.productDto.AllProductResponseDto;
import org.product_delivery_backend.dto.productDto.ProductRequestDto;
import org.product_delivery_backend.dto.productDto.ProductResponseDto;
import org.product_delivery_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
     private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private AllProductResponseDto allProductResponseDto;

    private AllProductResponseDto allProductResponseDto2;



    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        allProductResponseDto = new AllProductResponseDto();
        allProductResponseDto.setId(1L);
        allProductResponseDto.setTitle("Product1");
        allProductResponseDto.setPrice(new BigDecimal("10.00"));
        allProductResponseDto.setMinQuantity("2");
        allProductResponseDto.setPhotoLink("www.foto.by");

        allProductResponseDto2=   new AllProductResponseDto();
        allProductResponseDto2.setId(2L);
        allProductResponseDto2.setTitle("Product2");
        allProductResponseDto2.setPrice(new BigDecimal("20.00"));
        allProductResponseDto2.setMinQuantity("3");
        allProductResponseDto2.setPhotoLink("www.foto.by");
    }

    @Test
    void findAll_Success() throws Exception {
        // Given
        List<AllProductResponseDto> productList = Arrays.asList(allProductResponseDto, allProductResponseDto2);

        when(productService.findAllProduct()).thenReturn(productList);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value (2))
                .andExpect(jsonPath("$[0].title").value("Product1"))
                .andExpect(jsonPath("$[1].title").value("Product2"));

        verify(productService, times(1)).findAllProduct();
    }

    @Test
    void findAll_ProductsNotFound_ThrowsNotFoundException() throws Exception {

        when(productService.findAllProduct()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findAllProduct();
    }

    @Test
    void findAllPage_Success() throws Exception {

        List<AllProductResponseDto> products = List.of(allProductResponseDto, allProductResponseDto2);
        Page<AllProductResponseDto> productPage = new PageImpl<>(products, PageRequest.of(0, 2), products.size());

        when(productService.findAllProductPage(any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/products/page")
                        .param("page", "0")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("Product1"))
                .andExpect(jsonPath("$.content[1].title").value("Product2"));

        verify(productService, Mockito.times(1)).findAllProductPage(any(Pageable.class));
    }

    @Test
    void findAllPage_ByCategory_Success() throws Exception {

        List<AllProductResponseDto> products = List.of(allProductResponseDto);
        Page<AllProductResponseDto> productPage = new PageImpl<>(products, PageRequest.of(0, 1), products.size());

        when(productService.findProductsByCategory(eq("DAIRY"), any(Pageable.class))).thenReturn(productPage);

        mockMvc.perform(get("/api/products/page")
                        .param("page", "0")
                        .param("size", "1")
                        .param("category", "DAIRY")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Product1"));

        verify(productService, Mockito.times(1)).findProductsByCategory(eq("DAIRY"), any(Pageable.class));
    }

    @Test
    void findAllPage_InvalidPaginationParams_ThrowsInvalidDataException() throws Exception {
        mockMvc.perform(get("/api/products/page")
                        .param("page", "-1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllPage_EmptyPage_ThrowsNotFoundException() throws Exception {
        Page<AllProductResponseDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 2), 0);

        when(productService.findAllProductPage(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/products/page")
                        .param("page", "1")
                        .param("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findAllProductPage(any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProduct_success() throws Exception {
        ProductRequestDto requestDto = new ProductRequestDto("ProductTitle", new BigDecimal("15.00"), "200-Mi876543", "2", "Description", "www.poto.com");
        ProductResponseDto responseDto = new ProductResponseDto(1L, "ProductTitle", new BigDecimal("15.00"), "200-Mi876543", "2", "Description", "www.poto.com");

        when(productService.addProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("ProductTitle"))
                .andExpect(jsonPath("$.price").value(15.00))
                .andExpect(jsonPath("$.description").value("Description"));

        verify(productService, times(1)).addProduct(any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void addProduct_throwException() throws Exception {

        ProductRequestDto requestDto = new ProductRequestDto();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product title cannot be empty."))
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(productService, times(0)).addProduct(any(ProductRequestDto.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_success() throws Exception {
        Long productId = 1L;

        ProductResponseDto product = new ProductResponseDto(productId, "ProductTitle", new BigDecimal("15.00"), "200-Mi876543", "2", "Description", "www.photo.com");
        when(productService.findProductById(productId)).thenReturn(product);

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).findProductById(productId);
        verify(productService, times(1)).deleteProduct(productId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProduct_notFound() throws Exception {
        Long productId = 1L;

        when(productService.findProductById(productId)).thenReturn(null);

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found."))
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(productService, times(1)).findProductById(productId);
        verify(productService, times(0)).deleteProduct(productId);
    }

}