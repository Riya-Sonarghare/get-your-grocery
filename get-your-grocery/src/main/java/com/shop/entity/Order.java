package com.shop.entity;

import com.shop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "grocery_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Snapshot of customer info at order time (so profile changes don't affect old orders)
    @Column(name = "customer_username", nullable = false)
    private String customerUsername;

    @Column(name = "delivery_address", nullable = false, length = 300)
    private String deliveryAddress;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
