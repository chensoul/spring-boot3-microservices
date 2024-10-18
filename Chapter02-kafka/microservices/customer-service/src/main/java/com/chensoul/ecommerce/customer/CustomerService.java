package com.chensoul.ecommerce.customer;

import com.chensoul.exception.NotFoundException;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer(CustomerRequest request) {
        Customer customer = this.repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(CustomerRequest request) {
        Customer customer = this.repository.findByCustomerId(request.customerId())
            .orElseThrow(() -> new NotFoundException(
                String.format("Cannot update customer:: No customer found with the provided ID: %s", request.customerId())
            ));
        mergeCustomer(customer, request);
        this.repository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstname())) {
            customer.setFirstname(request.firstname());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
        if (StringUtils.isNotBlank(request.street())) {
            customer.setStreet(request.street());
        }
        if (StringUtils.isNotBlank(request.zipCode())) {
            customer.setZipCode(request.zipCode());
        }
    }

    public List<CustomerResponse> findAllCustomers() {
        return this.repository.findAll()
            .stream()
            .map(this.mapper::fromCustomer)
            .toList();
    }

    public CustomerResponse findByCustomerId(Integer customerId) {
        return this.repository.findByCustomerId(customerId)
            .map(mapper::fromCustomer)
            .orElseThrow(() -> new NotFoundException(String.format("No customer found with the provided ID: %s", customerId)));
    }

    public boolean existsByCustomerId(Integer customerId) {
        return this.repository.findByCustomerId(customerId)
            .isPresent();
    }

    public void deleteByCustomerId(Integer customerId) {
        this.repository.findByCustomerId(customerId)
            .ifPresent(this.repository::delete);
    }
}
