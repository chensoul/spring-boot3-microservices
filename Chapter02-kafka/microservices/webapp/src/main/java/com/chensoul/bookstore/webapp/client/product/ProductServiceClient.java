package com.chensoul.bookstore.webapp.client.product;

import com.chensoul.bookstore.common.PagedResult;
import com.chensoul.bookstore.product.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductServiceClient {

    @GetExchange("/api/products")
    PagedResult<Product> getProducts(@RequestParam int page);

    @GetExchange("/api/products/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code);
}
