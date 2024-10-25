package com.chensoul.bookstore.webapp.client.order;

import com.chensoul.bookstore.order.CreateOrderRequest;
import com.chensoul.bookstore.order.CreateOrderResponse;
import com.chensoul.bookstore.order.OrderDTO;
import com.chensoul.bookstore.order.OrderSummary;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface OrderServiceClient {
    @PostExchange("/api/orders")
    CreateOrderResponse createOrder(
            @RequestHeader Map<String, ?> headers, @RequestBody CreateOrderRequest orderRequest);

    @GetExchange("/api/orders")
    List<OrderSummary> getOrders(@RequestHeader Map<String, ?> headers);

    @GetExchange("/api/orders/{orderNumber}")
    OrderDTO getOrder(@RequestHeader Map<String, ?> headers, @PathVariable String orderNumber);
}
