package org.product_delivery_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[\\p{L}0-9\\s'\\-()]+$", message = "The title may contain letters, numbers, spaces, apostrophes, hyphens and brackets.")
    @Size(min = 2, max = 64, message = "The title must be between 2 and 64 characters long.")
    @NotBlank(message = "Field \"title\" must not be empty")
    private String title;

    @DecimalMin(value = "0.01", message = "The price must be bigger than 0")
    @Digits(integer = 10, fraction = 2, message = "The price must have up to 10 digits before the decimal point and up to 2 after it.")
    private BigDecimal price;

    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "The product code can only contain letters, numbers, hyphens and underscores.")
    @NotBlank(message = "The Field \"article\" must not be empty")
    @Size(min = 2, max = 32, message = "The product code must contain from 2 to 32 characters")
    private String productCode;
}


