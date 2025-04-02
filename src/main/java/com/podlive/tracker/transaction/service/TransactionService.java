package com.podlive.tracker.transaction.service;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.repository.CategoryRepository;
import com.podlive.tracker.transaction.dto.TransactionRequestDto;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository, AccountRepository accountRepository){
        //super(transactionRepository);
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> getAll() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    @Transactional
    public Transaction create(TransactionRequestDto requestDto) {
        Account account = getAccountById(requestDto.getAccount().getId());
        Category category = getCategoryById(requestDto.getCategory().getId());

        // Создание транзакции
        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(requestDto.getAmount())
                .category(category)
                .timestamp(requestDto.getTimestamp())
                .build();

        // Обновление баланса счета
        account.setStartBalance(account.getStartBalance().add(BigDecimal.valueOf(requestDto.getAmount()))); // add() работает с BigDecimal
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Integer id, TransactionRequestDto requestDto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Account oldAccount = transaction.getAccount();
        Account newAccount = getAccountById(requestDto.getAccount().getId());

        Category newCategory = getCategoryById(requestDto.getCategory().getId());

        // Корректировка баланса старого счета
        oldAccount.setStartBalance(oldAccount.getStartBalance().subtract(BigDecimal.valueOf(transaction.getAmount()))); // subtract() с BigDecimal
        accountRepository.save(oldAccount);

        // Обновляем данные транзакции
        transaction.setAccount(newAccount);
        transaction.setCategory(newCategory);
        transaction.setAmount(requestDto.getAmount());
        transaction.setTimestamp(requestDto.getTimestamp());

        // Корректировка баланса нового счета
        newAccount.setStartBalance(newAccount.getStartBalance().add(BigDecimal.valueOf(requestDto.getAmount()))); // add() с BigDecimal
        accountRepository.save(newAccount);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        Account account = transaction.getAccount();
        account.setStartBalance(account.getStartBalance().subtract(BigDecimal.valueOf(transaction.getAmount()))); // subtract() с BigDecimal
        accountRepository.save(account);

        transactionRepository.delete(transaction);
    }

    private Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    private Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Transaction getById(Integer id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
