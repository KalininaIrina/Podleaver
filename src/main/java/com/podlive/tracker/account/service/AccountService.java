package com.podlive.tracker.account.service;

import com.podlive.tracker.account.dto.AccountRequestDto;
import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.common.service.CrudService;
import com.podlive.tracker.currency.model.Currency;
import com.podlive.tracker.currency.repository.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AccountService extends CrudService<Account, Integer> {
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;

    public AccountService(AccountRepository accountRepository, CurrencyRepository currencyRepository){
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.currencyRepository = currencyRepository;
    }


    public Account create(AccountRequestDto requestDto) {
        // Преобразование строки в BigDecimal
        BigDecimal startBalance = new BigDecimal(requestDto.getStartBalance());

        // Найдем валюту по id
        Currency currency = currencyRepository.findById(requestDto.getCurrency().getId())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));

        // Создаем новый аккаунт
        Account account = Account.builder()
                .name(requestDto.getName())
                .currency(currency)
                .startBalance(startBalance) // Передаем BigDecimal
                .build();

        // Сохраняем аккаунт в базе данных
        return save(account);
    }


    public Account update(Integer id, AccountRequestDto requestDto) {
        // Ищем валюту по ID, который передается в requestDto
        Currency currency = currencyRepository.findById(requestDto.getCurrency().getId())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));

        // Ищем аккаунт по ID
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Обновляем поля аккаунта
        account.setName(requestDto.getName());
        account.setCurrency(currency);

        // Преобразуем строку startBalance в BigDecimal
        BigDecimal startBalance;
        try {
            startBalance = new BigDecimal(requestDto.getStartBalance());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение для startBalance");
        }
        account.setStartBalance(startBalance); // Обновляем startBalance

        return save(account);
    }

}
