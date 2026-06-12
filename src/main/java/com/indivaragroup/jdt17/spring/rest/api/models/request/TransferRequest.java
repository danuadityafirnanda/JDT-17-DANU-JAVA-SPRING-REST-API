package com.indivaragroup.jdt17.spring.rest.api.models.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    @NotBlank(message = "Source Account ID is required")
    private String sourceAccountId;

    @NotBlank(message = "Destination Account ID is required")
    private String destinationAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10000.00", message = "Minimum transfer is 10.000")
    private BigDecimal amount;
}
