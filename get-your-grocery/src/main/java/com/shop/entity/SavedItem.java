package com.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
