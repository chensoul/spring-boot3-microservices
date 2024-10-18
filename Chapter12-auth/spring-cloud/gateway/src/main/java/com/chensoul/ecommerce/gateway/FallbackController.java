package com.chensoul.ecommerce.gateway;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

  @RequestMapping("/fallback")
  public ResponseEntity<String> fallback() {
    return ResponseEntity.status(HttpStatus.SC_SERVICE_UNAVAILABLE)
            .body("Service is currently unavailable. Please try again later.");
  }
}
