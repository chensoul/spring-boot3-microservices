package com.chensoul.ecommerce.order;

import com.chensoul.ecommerce.client.CustomerClient;
import com.chensoul.ecommerce.client.PaymentClient;
import com.chensoul.ecommerce.client.ProductClient;
import com.chensoul.ecommerce.customer.CustomerResponse;
import com.chensoul.ecommerce.exception.BusinessException;
import com.chensoul.ecommerce.orderline.OrderLineRepository;
import com.chensoul.ecommerce.orderline.OrderLineRequest;
import com.chensoul.ecommerce.orderline.OrderLineService;
import com.chensoul.ecommerce.payment.PaymentRequest;
import com.chensoul.ecommerce.product.OrderProducer;
import com.chensoul.ecommerce.product.ProductPurchaseRequest;
import com.chensoul.ecommerce.product.ProductPurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository repository;
  private final OrderLineRepository orderLineRepository;
  private final OrderMapper mapper;
  private final CustomerClient customerClient;
  private final PaymentClient paymentClient;
  private final ProductClient productClient;
  private final OrderLineService orderLineService;
  private final OrderProducer orderProducer;

  @Transactional
  public Integer createOrder(OrderRequest request) {
    CustomerResponse customerResponse = this.customerClient.findCustomerById(request.customerId())
            .orElseThrow(() -> new BusinessException("Cannot create order:: No customer exists with the provided ID"));

    List<ProductPurchaseResponse> purchasedProductResponses = productClient.purchaseProducts(request.products());

    Order order = this.repository.save(mapper.toOrder(request));

    for (ProductPurchaseRequest productPurchaseRequest : request.products()) {
      orderLineService.saveOrderLine(
              new OrderLineRequest(
                      null,
                      order.getId(),
                      productPurchaseRequest.productId(),
                      productPurchaseRequest.quantity()
              )
      );
    }
    PaymentRequest paymentRequest = new PaymentRequest(null,
            order.getId(),
            request.totalAmount(),
            request.paymentMethod(),
            customerResponse
    );
    paymentClient.requestOrderPayment(paymentRequest);

    orderProducer.sendNotification(
            new OrderConfirmation(
                    order.getId(),
                    order.getTotalAmount(),
                    order.getPaymentMethod(),
                    customerResponse,
                    purchasedProductResponses
            )
    );

    return order.getId();
  }

  public List<OrderResponse> findAllOrders() {
    return this.repository.findAll()
            .stream()
            .map(this.mapper::fromOrder)
            .collect(Collectors.toList());
  }

  public OrderResponse findById(Integer id) {
    return this.repository.findById(id)
            .map(this.mapper::fromOrder)
            .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", id)));
  }

  public void delete(Integer id) {
    this.repository.findById(id).ifPresent(order -> {
      orderLineRepository.findAllByOrderId(id).forEach(orderLineRepository::delete);
      this.repository.delete(order);
    });
  }
}
