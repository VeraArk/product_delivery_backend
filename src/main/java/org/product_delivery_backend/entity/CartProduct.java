package org.product_delivery_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cart_products")
@AllArgsConstructor
@NoArgsConstructor
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    @NotNull
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
//    @NotNull
    private Product product;

    @Column(name = "product_quantity")
//    @NotNull
    @Min(1)
    private Integer productQuantity;

    @DecimalMin(value = "0.01", message = "The price must be bigger than 0")
    @Digits(integer = 10, fraction = 2, message = "The sum must have up to 10 digits before the decimal point and up to 2 after it.")
    private BigDecimal sum;

    public CartProduct(Cart cart, Product product, Integer productQuantity) {
        this.cart = cart;
        this.product = product;
        this.productQuantity = productQuantity;
    }
}
