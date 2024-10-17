package com.chensoul.ecommerce.order;

import com.chensoul.ecommerce.customer.CustomerResponse;
import com.chensoul.ecommerce.payment.PaymentMethod;
import com.chensoul.ecommerce.product.ProductPurchaseResponse;
import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        Integer orderId,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customerResponse,
        List<ProductPurchaseResponse> productResponses
) {
}
