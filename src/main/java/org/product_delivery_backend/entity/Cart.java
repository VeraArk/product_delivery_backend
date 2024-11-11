package org.product_delivery_backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "carts")
@Schema(description = "Shopping cart associated with a user.")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Cart unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @OneToOne()
    @NotNull
    @JoinColumn (name = "user_id", referencedColumnName = "id")
    @Schema(description = "User associated with the cart.", required = true)
    private User user;
}
