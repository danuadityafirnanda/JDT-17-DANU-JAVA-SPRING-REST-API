package com.indivaragroup.jdt17.spring.rest.api.entity;

import com.indivaragroup.jdt17.spring.rest.api.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "source_account_id")
    private String sourceAccountId;

    @Column(name = "destination_account_id")
    private String destinationAccountId;

    @Column(name = "balance_before")
    private BigDecimal balanceBefore;

    @Column(name = "balance_after")
    private BigDecimal balanceAfter;
}
