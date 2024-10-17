package com.chensoul.ecommerce.order;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService service;

  /**
   * Create an Order
   *
   * @param request
   * @return
   */
  @PostMapping
  public ResponseEntity<Integer> createOrder(@RequestBody @Valid OrderRequest request) {
    return ResponseEntity.ok(this.service.createOrder(request));
  }

  /**
   * Find all Order
   *
   * @return
   */
  @GetMapping
  public ResponseEntity<List<OrderResponse>> findAll() {
    return ResponseEntity.ok(this.service.findAllOrders());
  }

  /**
   * Find an Order
   *
   * @param orderId
   * @return
   */
  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> findById(@PathVariable("orderId") Integer orderId) {
    return ResponseEntity.ok(this.service.findById(orderId));
  }

  /**
   * Delete an Order
   *
   * @param orderId
   * @return
   */
  @DeleteMapping("/{orderId}")
  public ResponseEntity<Valid> delete(@PathVariable("orderId") Integer orderId) {
    this.service.delete(orderId);
    return ResponseEntity.ok().build();
  }
}
