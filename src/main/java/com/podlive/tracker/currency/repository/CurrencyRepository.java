package com.podlive.tracker.currency.repository;

import com.podlive.tracker.currency.model.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
}
