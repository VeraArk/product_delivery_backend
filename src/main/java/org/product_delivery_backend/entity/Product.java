package org.product_delivery_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Pattern(regexp = "^[\\p{L}0-9\\s'\\-()]+$", message = "The title may contain letters, numbers, spaces, apostrophes, hyphens and brackets.")
//    @Size(min = 2, max = 64, message = "The title must be between 2 and 64 characters long.")
    @NotBlank(message = "Field \"title\" must not be empty")
    private String title;

//    @DecimalMin(value = "0.01", message = "The price must be bigger than 0")
//    @Digits(integer = 10, fraction = 2, message = "The price must have up to 10 digits before the decimal point and up to 2 after it.")
    private BigDecimal price;

//    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "The product code can only contain letters, numbers, hyphens and underscores.")
    @NotBlank(message = "The Field \"article\" must not be empty")
//    @Size(min = 2, max = 32, message = "The product code must contain from 2 to 32 characters")
    @Column(name="product_code")
    private String productCode;

//    @Pattern(regexp = "^[0-9.]+(\\s?(g|kg|ml|l))$", message = "The weight/volume must contain a number followed by a valid unit (g, kg, ml, l).")
//    @Size(min = 2, max = 10, message = "The weight/volume must be between 2 and 10 characters long.")
    @Column(name="min-quantity")
    private String minQuantity;

//    @Pattern(regexp = "^[A-Za-z0-9.,-:;()?!\\s]+$", message = "The product code can only contain letters, numbers.")
    @NotBlank(message = "The Field \"article\" must not be empty")
//    @Size(min = 10, max = 255, message = "The product code must contain from 10 to 255 characters")
    private String description;

//    @URL(message = "Invalid URL format")
    // на случай если ссылка будет хранится в базе
    //@Pattern(regexp = "^[A-Za-z0-9/._-]+$", message = "Invalid file path")
    //@Size(max = 255, message = "File path is too long")
    @Column(name = "photo-link")
    private String photoLink;
}


