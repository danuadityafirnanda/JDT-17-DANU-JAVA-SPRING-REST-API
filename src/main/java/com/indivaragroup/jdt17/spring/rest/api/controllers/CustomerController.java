package com.indivaragroup.jdt17.spring.rest.api.controllers;

import com.indivaragroup.jdt17.spring.rest.api.models.request.CustomerRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.CustomerResponse;
import com.indivaragroup.jdt17.spring.rest.api.models.response.WebResponse;
import com.indivaragroup.jdt17.spring.rest.api.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CustomerResponse> addCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.addCustomer(request);
        return WebResponse.<CustomerResponse>builder()
                .code(HttpStatus.CREATED.value())
                .status("CREATED")
                .data(response)
                .build();
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Page<CustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CustomerResponse> responses = customerService.getAllCustomer(page, size);
        return WebResponse.<Page<CustomerResponse>>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(responses)
                .build();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<CustomerResponse> getCustomer(@PathVariable String id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return WebResponse.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CustomerResponse> update(@PathVariable String id, @Valid @RequestBody CustomerRequest request) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return WebResponse.<CustomerResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> delete(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return WebResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data("Success delete customer")
                .build();
    }
}