package com.chensoul.ecommerce.order;

import com.chensoul.ecommerce.payment.PaymentMethod;
import com.chensoul.ecommerce.product.ProductPurchaseRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@JsonInclude(Include.NON_EMPTY)
public record OrderRequest(
        Integer id,
        @Positive(message = "Order amount should be positive")
        BigDecimal totalAmount,
        @NotNull(message = "Payment method should be precised")
        PaymentMethod paymentMethod,
        @NotBlank(message = "Customer should be present")
        String customerId,
        @NotEmpty(message = "You should at least purchase one product")
        List<ProductPurchaseRequest> products
) {

}
