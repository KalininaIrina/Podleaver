package com.podlive.tracker.transaction.repository;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.transaction.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findByAccount(Account account);
}
