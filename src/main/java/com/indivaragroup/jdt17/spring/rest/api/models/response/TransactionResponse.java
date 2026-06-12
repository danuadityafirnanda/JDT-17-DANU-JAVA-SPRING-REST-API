package com.indivaragroup.jdt17.spring.rest.api.models.response;

import com.indivaragroup.jdt17.spring.rest.api.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private String transactionId;
    private TransactionType transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String sourceAccountId;
    private String destinationAccountId;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
}
