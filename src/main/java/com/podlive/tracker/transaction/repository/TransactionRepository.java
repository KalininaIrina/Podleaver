package com.podlive.tracker.transaction.repository;

import com.podlive.tracker.transaction.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
