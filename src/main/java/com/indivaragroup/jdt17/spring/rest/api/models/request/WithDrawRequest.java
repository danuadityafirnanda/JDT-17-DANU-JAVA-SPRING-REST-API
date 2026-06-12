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
public class WithDrawRequest {
    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "50000.00", message = "Minimum withdrawal is 50.000")
    private BigDecimal amount;
}
