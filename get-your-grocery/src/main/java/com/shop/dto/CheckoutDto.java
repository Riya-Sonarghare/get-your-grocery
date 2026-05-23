package com.shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutDto {

    @NotNull(message = "Product ID is required")
    private Long productId;

    private String customerUsername;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
