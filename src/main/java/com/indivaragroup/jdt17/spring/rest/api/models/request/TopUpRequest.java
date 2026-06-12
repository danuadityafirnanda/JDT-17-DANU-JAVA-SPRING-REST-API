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
public class TopUpRequest {
    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "10000.00", message = "Minimum Top Up is 10.000")
    private BigDecimal amount;
}
