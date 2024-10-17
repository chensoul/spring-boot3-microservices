package com.chensoul.ecommerce.customer;

import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public Customer toCustomer(CustomerRequest request) {
    if (request == null) {
      return null;
    }
    return Customer.builder()
            .customerId(request.customerId())
            .firstname(request.firstname())
            .lastname(request.lastname())
            .email(request.email())
            .street(request.street())
            .zipCode(request.zipCode())
            .build();
  }

  public CustomerResponse fromCustomer(Customer customer) {
    if (customer == null) {
      return null;
    }
    return new CustomerResponse(
            customer.getCustomerId(),
            customer.getFirstname(),
            customer.getLastname(),
            customer.getEmail(),
            customer.getStreet(),
            customer.getZipCode()
    );
  }
}
