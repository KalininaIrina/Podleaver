package com.podlive.tracker.account.service;

import com.podlive.tracker.account.dto.AccountRequestDto;
import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.common.service.CrudService;
import com.podlive.tracker.currency.model.Currency;
import com.podlive.tracker.currency.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService extends CrudService<Account, Integer> {
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;

    public AccountService(AccountRepository accountRepository, CurrencyRepository currencyRepository){
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.currencyRepository = currencyRepository;
    }

    public Account create(AccountRequestDto requestDto){
        Currency currency = currencyRepository.findById(requestDto.getCurrency().getId()).orElseThrow(EntityNotFoundException::new);
        Account account = Account.builder()
                .name(requestDto.getName())
                .currency(currency)
                .startBalance(requestDto.getStartBalance())
                .build();
        return save(account);
    }

    public Account update(Integer id, AccountRequestDto requestDto){
        Currency currency = currencyRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        account.setName(requestDto.getName());
        account.setCurrency(currency);
        account.setStartBalance(requestDto.getStartBalance());
        return save(account);
    }
}
