package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(length = 50)
    private String category;

    @Column(nullable = false)
    private boolean available = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}
