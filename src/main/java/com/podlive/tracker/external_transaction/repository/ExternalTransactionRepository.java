package com.podlive.tracker.external_transaction.repository;

import com.podlive.tracker.external_transaction.model.ExternalTransaction;
import org.springframework.data.repository.CrudRepository;

public interface ExternalTransactionRepository extends CrudRepository<ExternalTransaction, Integer> {
}
