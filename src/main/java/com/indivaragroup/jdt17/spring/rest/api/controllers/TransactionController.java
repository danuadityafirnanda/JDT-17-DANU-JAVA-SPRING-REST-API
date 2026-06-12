package com.indivaragroup.jdt17.spring.rest.api.controllers;

import com.indivaragroup.jdt17.spring.rest.api.models.request.TopUpRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.request.TransferRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.request.WithDrawRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.TransactionResponse;
import com.indivaragroup.jdt17.spring.rest.api.models.response.WebResponse;
import com.indivaragroup.jdt17.spring.rest.api.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping(
            path = "/topup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionResponse> topUp(@Valid @RequestBody TopUpRequest request) {
        TransactionResponse response = transactionService.topUp(request);
        return WebResponse.<TransactionResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @PostMapping(
            path = "/withdraw",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionResponse> withdraw(@Valid @RequestBody WithDrawRequest request) {
        TransactionResponse response = transactionService.withdraw(request);
        return WebResponse.<TransactionResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @PostMapping(
            path = "/transfer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> transfer(@Valid @RequestBody TransferRequest request) {
        transactionService.transfer(request);
        return WebResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data("Transfer Success")
                .build();
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TransactionResponse> responses = transactionService.getTransactions(page, size);
        return WebResponse.<Page<TransactionResponse>>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(responses)
                .build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionResponse> getTransactionById(@PathVariable String id) {
        TransactionResponse response = transactionService.getTransactionById(id);

        return WebResponse.<TransactionResponse>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/account/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<Page<TransactionResponse>> getTransactionsByAccount(
            @PathVariable String accountId,
            @RequestParam(required = false, defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TransactionResponse> responses = transactionService.getTransactionsByAccount(accountId, type, page, size);
        return WebResponse.<Page<TransactionResponse>>builder()
                .code(HttpStatus.OK.value())
                .status("OK")
                .data(responses)
                .build();
    }
}