package com.chensoul.ecommerce.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CustomerRequest(
    @NotNull(message = "Customer id is required")
    Integer customerId,
    @NotBlank(message = "Customer firstname is required")
    String firstname,
    @NotBlank(message = "Customer firstname is required")
    String lastname,
    @NotBlank(message = "Customer Email is required")
    @Email(message = "Customer Email is not a valid email address")
    String email,
    String street,
    String zipCode
) {

}
