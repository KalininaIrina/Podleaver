package com.podlive.tracker.account.repository;

import com.podlive.tracker.account.model.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {
}
