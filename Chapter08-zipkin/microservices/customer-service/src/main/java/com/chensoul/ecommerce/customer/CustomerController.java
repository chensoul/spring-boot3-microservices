package com.chensoul.ecommerce.customer;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Timed("application.customer")
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerService service;

  /**
   * Create a Customer
   *
   * @param request
   * @return
   */
  @PostMapping
  public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest request) {
    return ResponseEntity.ok(this.service.createCustomer(request));
  }

  /**
   * Update a Customer
   *
   * @param request
   * @return
   */
  @PutMapping
  public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerRequest request) {
    this.service.updateCustomer(request);
    return ResponseEntity.accepted().build();
  }

  @GetMapping
  public ResponseEntity<List<CustomerResponse>> findAll() {
    return ResponseEntity.ok(this.service.findAllCustomers());
  }

  /**
   * Check a Customer
   *
   * @param customerId
   * @return
   */
  @GetMapping("/exists/{customerId}")
  public ResponseEntity<Boolean> existsByCustomerId(@PathVariable("customerId") Integer customerId) {
    return ResponseEntity.ok(this.service.existsByCustomerId(customerId));
  }

  /**
   * Find a Customer
   *
   * @param customerId
   * @return
   */
  @GetMapping("/{customerId}")
  public ResponseEntity<CustomerResponse> findByCustomerId(@PathVariable("customerId") Integer customerId) {
    return ResponseEntity.ok(this.service.findByCustomerId(customerId));
  }

  /**
   * Delete a Customer
   *
   * @param customerId
   * @return
   */
  @DeleteMapping("/{customerId}")
  public ResponseEntity<Void> deleteByCustomerId(@PathVariable("customerId") Integer customerId) {
    this.service.deleteByCustomerId(customerId);
    return ResponseEntity.accepted().build();
  }

}
