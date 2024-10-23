package com.chensoul.ecommerce.client;

import com.chensoul.ecommerce.product.ProductPurchaseRequest;
import com.chensoul.ecommerce.product.ProductPurchaseResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductClient {
  @PostMapping("/api/v1/products/purchase")
  List<ProductPurchaseResponse> purchaseProducts(@RequestBody List<ProductPurchaseRequest> requestBody);
}
