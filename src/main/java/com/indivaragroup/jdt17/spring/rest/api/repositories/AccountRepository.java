package com.indivaragroup.jdt17.spring.rest.api.repositories;

import com.indivaragroup.jdt17.spring.rest.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountNumber(String accountNumber);
}
