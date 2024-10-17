package com.chensoul.ecommerce.client;

import com.chensoul.ecommerce.customer.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerClient {

  @GetMapping("/api/v1/customers/{customerId}")
  CustomerResponse findCustomerById(@PathVariable String customerId);

}
