package org.product_delivery_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.product_delivery_backend.dto.productDto.*;
import org.product_delivery_backend.exceptions.InvalidDataException;
import org.product_delivery_backend.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.product_delivery_backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Product controller")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Find all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = AllProductResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Products not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<AllProductResponseDto>> findAll() {
        List<AllProductResponseDto> products = productService.findAllProduct();
        if (products.isEmpty()) {
            throw new NotFoundException("No products found.");
        }
        return new ResponseEntity<>(productService.findAllProduct(), HttpStatus.OK);
    }

    @Operation(summary = "Find all products with pagination",
            description = "Retrieve a paginated list of all available products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Products not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })

    @GetMapping("/page")
    public ResponseEntity<Page<AllProductResponseDto>> findAllPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam (required = false) String category) {

        if (page < 0 || size <= 0) {
            throw new InvalidDataException("Invalid pagination parameters.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<AllProductResponseDto> productPage;

        if (category != null || !category.isEmpty()) {
            productPage = productService.findProductsByCategory(category, pageable);
            System.out.println(productPage);
        } else {
            productPage = productService.findAllProductPage(pageable);
        }

        if (productPage.isEmpty()) {
            throw new NotFoundException("No products found on this page.");
        }

        return new ResponseEntity<>(productPage, HttpStatus.OK);
    }


    @Operation(summary = "Add a new product",
            description = "Create a new product with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid product request",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Product already exists", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })

    @PostMapping
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequestDto productRequestDto) {
        if (productRequestDto.getTitle() == null || productRequestDto.getTitle().isEmpty()) {
            throw new InvalidDataException("Product title cannot be empty.");
        }
        ProductResponseDto productResponseDto = productService.addProduct(productRequestDto);
        return new ResponseEntity<>(productResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete a product",
            description = "Remove a product from the system by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable("id") Long id) {
        ProductResponseDto product = productService.findProductById(id);
        if (product == null) {
            throw new NotFoundException("Product not found.");
        }
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Find a product by ID",
            description = "Retrieve a product from the system using its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findByID(@PathVariable("id") Long id) {
        ProductResponseDto product = productService.findProductById(id);
        if (product == null) {
            throw new NotFoundException("Product not found.");
        }
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);

    }
}
