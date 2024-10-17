package com.chensoul.ecommerce.order;

import org.springframework.stereotype.Service;

@Service
public class OrderMapper {


  public Order toOrder(OrderRequest request) {
    if (request == null) {
      return null;
    }
    Order order = Order.builder()
            .id(request.id())
            .totalAmount(request.totalAmount())
            .paymentMethod(request.paymentMethod())
            .customerId(request.customerId())
            .build();
    return order;
  }

  public OrderResponse fromOrder(Order order) {
    return new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getPaymentMethod(),
            order.getCustomerId()
    );
  }
}
