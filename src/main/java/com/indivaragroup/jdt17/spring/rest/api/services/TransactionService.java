package com.indivaragroup.jdt17.spring.rest.api.services;

import com.indivaragroup.jdt17.spring.rest.api.entity.Account;
import com.indivaragroup.jdt17.spring.rest.api.entity.Transaction;
import com.indivaragroup.jdt17.spring.rest.api.enums.TransactionType;
import com.indivaragroup.jdt17.spring.rest.api.models.request.TopUpRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.request.TransferRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.request.WithDrawRequest;
import com.indivaragroup.jdt17.spring.rest.api.models.response.TransactionResponse;
import com.indivaragroup.jdt17.spring.rest.api.repositories.AccountRepository;
import com.indivaragroup.jdt17.spring.rest.api.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Value("${bank.transfer.admin-fee}")
    private BigDecimal adminFee;

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .sourceAccountId(transaction.getSourceAccountId())
                .destinationAccountId(transaction.getDestinationAccountId())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .build();
    }

    @Transactional
    public TransactionResponse topUp(TopUpRequest request) {
        if (request.getAmount().compareTo(new BigDecimal("10000")) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum Top Up is 10.000");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.add(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.TOP_UP);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDestinationAccountId(account.getAccountId());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());

        Transaction savedTx = transactionRepository.save(transaction);
        return toResponse(savedTx);
    }

    @Transactional
    public void transfer(TransferRequest request) {
        if (request.getSourceAccountId().equals(request.getDestinationAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source and destination account cannot be the same");
        }
        if (request.getAmount().compareTo(new BigDecimal("10000")) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum transfer is 10.000");
        }

        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found"));
        Account destinationAccount = accountRepository.findById(request.getDestinationAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found"));

        BigDecimal totalDeduction = request.getAmount().add(adminFee);
        if (sourceAccount.getBalance().compareTo(totalDeduction) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        BigDecimal sourceBalanceBefore = sourceAccount.getBalance();
        sourceAccount.setBalance(sourceBalanceBefore.subtract(totalDeduction));
        accountRepository.save(sourceAccount);

        Transaction transactionOut = new Transaction();
        transactionOut.setTransactionType(TransactionType.TRANSFER_SENDER);
        transactionOut.setAmount(totalDeduction);
        transactionOut.setTransactionDate(LocalDateTime.now());
        transactionOut.setSourceAccountId(sourceAccount.getAccountId());
        transactionOut.setDestinationAccountId(destinationAccount.getAccountId());
        transactionOut.setBalanceBefore(sourceBalanceBefore);
        transactionOut.setBalanceAfter(sourceAccount.getBalance());
        transactionRepository.save(transactionOut);

        BigDecimal destinationBalanceBefore = destinationAccount.getBalance();
        destinationAccount.setBalance(destinationBalanceBefore.add(request.getAmount()));
        accountRepository.save(destinationAccount);

        Transaction transactionIn = new Transaction();
        transactionIn.setTransactionType(TransactionType.TRANSFER_RECEIVER);
        transactionIn.setAmount(request.getAmount());
        transactionIn.setTransactionDate(LocalDateTime.now());
        transactionIn.setSourceAccountId(sourceAccount.getAccountId());
        transactionIn.setDestinationAccountId(destinationAccount.getAccountId());
        transactionIn.setBalanceBefore(destinationBalanceBefore);
        transactionIn.setBalanceAfter(destinationAccount.getBalance());
        transactionRepository.save(transactionIn);
    }

    @Transactional
    public TransactionResponse withdraw(WithDrawRequest request) {
        if (request.getAmount().compareTo(new BigDecimal("50000")) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum withdrawal is 50.000");
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        BigDecimal balanceBefore = account.getBalance();
        account.setBalance(balanceBefore.subtract(request.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSourceAccountId(account.getAccountId());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(account.getBalance());

        Transaction savedTx = transactionRepository.save(transaction);
        return toResponse(savedTx);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        return transactionRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(String id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        return toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccount(String accountId, String type, int page, int size) {
        if (!accountRepository.existsById(accountId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions;

        if (type == null || type.equalsIgnoreCase("ALL")) {
            transactions = transactionRepository.findAllByAccountId(accountId, pageable);
        } else {
            TransactionType transactionType;
            try {
                if (type.equalsIgnoreCase("TRANSFER_IN")) {
                    transactionType = TransactionType.TRANSFER_RECEIVER;
                } else if (type.equalsIgnoreCase("TRANSFER_OUT")) {
                    transactionType = TransactionType.TRANSFER_SENDER;
                } else {
                    transactionType = TransactionType.valueOf(type.toUpperCase());
                }
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction type filter");
            }
            transactions = transactionRepository.findAllByAccountIdAndType(accountId, transactionType, pageable);
        }

        return transactions.map(this::toResponse);
    }
}