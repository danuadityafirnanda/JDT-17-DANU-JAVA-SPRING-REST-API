package com.indivaragroup.jdt17.spring.rest.api.controllers;

import com.indivaragroup.jdt17.spring.rest.api.models.request.AccountRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.AccountResponse;
import com.indivaragroup.jdt17.spring.rest.api.models.response.WebResponse;
import com.indivaragroup.jdt17.spring.rest.api.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return WebResponse.<AccountResponse>builder()
                .code(HttpStatus.CREATED.value())
                .status("CREATED")
                .data(response)
                .build();
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Page<AccountResponse>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AccountResponse> response = accountService.getAllAccounts(page, size);
        return WebResponse.<Page<AccountResponse>>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountResponse> getAccountById(@PathVariable String id) {
        AccountResponse response = accountService.getAccountById(id);

        return WebResponse.<AccountResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/account-number/{accountNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountResponse> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByAccountNumber(accountNumber);

        return WebResponse.<AccountResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }
}
