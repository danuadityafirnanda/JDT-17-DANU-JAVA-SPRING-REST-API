package com.indivaragroup.jdt17.spring.rest.api.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;


}
