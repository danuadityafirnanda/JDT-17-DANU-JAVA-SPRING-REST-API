package com.indivaragroup.jdt17.spring.rest.api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "mst_account")
public class Account {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String accountId;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
