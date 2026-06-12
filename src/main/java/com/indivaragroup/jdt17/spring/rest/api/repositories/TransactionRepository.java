package com.indivaragroup.jdt17.spring.rest.api.repositories;

import com.indivaragroup.jdt17.spring.rest.api.entity.Transaction;
import com.indivaragroup.jdt17.spring.rest.api.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccountId = :accountId OR t.destinationAccountId = :accountId")
    Page<Transaction> findAllByAccountId(@Param("accountId") String accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccountId = :accountId OR t.destinationAccountId = :accountId) AND t.transactionType = :type")
    Page<Transaction> findAllByAccountIdAndType(@Param("accountId") String accountId, @Param("type") TransactionType type, Pageable pageable);
}
