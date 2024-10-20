package org.product_delivery_backend.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table (name= "orders")
@Schema(description = "Order entity representing a customer's order.")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Order unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "The user who placed the order.", required = true)
    private User user;

    @Column(name = "order_time")
    @Schema(description = "The time when the order was placed.", example = "2024-10-17T10:00:00")
    private LocalDateTime orderTime;

    @Column(name = "address")
    @Schema(description = "The delivery address for the order.", example = " Eisenstr 1, Berlin, DE")
    private String address;

    @Column(name = "delivery_time")
    @Schema(description = "The scheduled time for the order delivery.", example = "2024-10-18T14:00:00")
    private LocalDateTime deliveryTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "The list of products included in the order.")
    private List<OrderProduct> orderProducts;

    @Column(name = "total_sum")
    @Schema(description = "The total amount of the order.", example = "150.00")
    private BigDecimal totalSum;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    @Schema(description = "The current status of the order.")
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    @Schema(description = "The payment method used for the order.", example = "Pay Pal")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_url", columnDefinition="TEXT", length = 2000)
    private String paymentUrl;
}
