package com.podlive.tracker.recurring_transaction.repository;

import com.podlive.tracker.recurring_transaction.model.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Integer> {
}
