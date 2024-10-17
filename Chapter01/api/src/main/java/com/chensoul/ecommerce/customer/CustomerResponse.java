package com.chensoul.ecommerce.customer;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CustomerResponse(
        Integer customerId,
        String firstname,
        String lastname,
        String email,
        String street,
        String zipCode
) {

}
