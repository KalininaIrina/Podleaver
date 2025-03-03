package com.podlive.tracker.transaction.service;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.repository.CategoryRepository;
import com.podlive.tracker.common.service.CrudService;
import com.podlive.tracker.transaction.dto.TransactionRequestDto;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService extends CrudService<Transaction, Integer> {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, AccountRepository accountRepository){
        super(transactionRepository);
        this.accountRepository= accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public Transaction create(TransactionRequestDto requestDto){
        Account account = accountRepository.findById(requestDto.getAccount().getId()).orElseThrow(EntityExistsException::new);
        Category category = categoryRepository.findById(requestDto.getCategory().getId()).orElseThrow(EntityNotFoundException::new);
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(requestDto.getAmount())
                .category(category)
                .timestamp(requestDto.getTimestamp())
                .build();
        return save(transaction);
    }

    public Transaction update(Integer id, TransactionRequestDto requestDto){
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(requestDto.getAmount());
        transaction.setTimestamp(requestDto.getTimestamp());
        return save(transaction);
    }

}
