package com.shop.entity;

import com.shop.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}
