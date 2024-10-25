package com.chensoul.ecommerce.payment;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Timed("application.payment")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService service;

  /**
   * Create a Payment
   *
   * @param request
   * @return
   */
  @PostMapping
  public ResponseEntity<Integer> createPayment(@RequestBody @Valid PaymentRequest request) {
    return ResponseEntity.ok(this.service.createPayment(request));
  }
}
