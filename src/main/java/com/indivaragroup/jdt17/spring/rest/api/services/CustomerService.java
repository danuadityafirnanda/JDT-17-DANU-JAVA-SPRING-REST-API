package com.indivaragroup.jdt17.spring.rest.api.services;

import com.indivaragroup.jdt17.spring.rest.api.entity.Customer;
import com.indivaragroup.jdt17.spring.rest.api.models.request.CustomerRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.CustomerResponse;
import com.indivaragroup.jdt17.spring.rest.api.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getCustomerId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .build();
    }

    @Transactional
    public CustomerResponse addCustomer(CustomerRequest request) {

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(customer);
        return toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomer(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public CustomerResponse updateCustomer(String id, CustomerRequest request) {
        Customer customerToUpdate = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customerToUpdate.setName(request.getName());
        customerToUpdate.setEmail(request.getEmail());
        customerToUpdate.setPhone(request.getPhone());
        customerToUpdate.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(customerToUpdate);
        return toResponse(updatedCustomer);
    }

    @Transactional
    public void deleteCustomer(String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        customerRepository.delete(customer);
    }
}