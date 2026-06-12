package com.indivaragroup.jdt17.spring.rest.api.services;

import com.indivaragroup.jdt17.spring.rest.api.entity.Account;
import com.indivaragroup.jdt17.spring.rest.api.entity.Customer;
import com.indivaragroup.jdt17.spring.rest.api.models.request.AccountRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.AccountResponse;
import com.indivaragroup.jdt17.spring.rest.api.models.response.CustomerResponse;
import com.indivaragroup.jdt17.spring.rest.api.repositories.AccountRepository;
import com.indivaragroup.jdt17.spring.rest.api.repositories.CustomerRepository;
import com.indivaragroup.jdt17.spring.rest.api.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    private AccountResponse toResponse(Account account) {
        CustomerResponse customerDto = CustomerResponse.builder()
                .id(account.getCustomer().getCustomerId())
                .name(account.getCustomer().getName())
                .email(account.getCustomer().getEmail())
                .phone(account.getCustomer().getPhone())
                .address(account.getCustomer().getAddress())
                .build();

        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .customer(customerDto)
                .build();
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Account newAccount = new Account();
        newAccount.setAccountNumber(IdGenerator.generateAccountNumber());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCustomer(customer);

        Account savedAccount = accountRepository.save(newAccount);
        return toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public Page<AccountResponse> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountRepository.findAll(pageable);
        return accounts.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(String id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return toResponse(account);
    }
}