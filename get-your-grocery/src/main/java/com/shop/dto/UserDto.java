package com.shop.dto;

import com.shop.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    private String password;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String contactNumber;

    private Role role;
}
