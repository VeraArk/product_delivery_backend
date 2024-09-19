package org.product_delivery_backend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "storage")

public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull(message = "The Field \"quantity\" should not be empty")
    @Min(value = 0, message = "The quantity must be non-negative.")
    @Max(value = 10000, message = "The quantity should not exceed 10000")
    private Integer quantity;

}
