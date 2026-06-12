package com.indivaragroup.jdt17.spring.rest.api.repositories;

import com.indivaragroup.jdt17.spring.rest.api.entity.Account;
import com.indivaragroup.jdt17.spring.rest.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
}
