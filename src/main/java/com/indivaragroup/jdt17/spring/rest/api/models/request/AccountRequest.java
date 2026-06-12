package com.indivaragroup.jdt17.spring.rest.api.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {
    @NotBlank(message = "Customer ID is required")
    private String customerId;
}
