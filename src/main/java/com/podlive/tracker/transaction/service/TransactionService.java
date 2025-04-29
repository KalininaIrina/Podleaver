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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.LinkedHashMap;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
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
        account.setStartBalance(account.getStartBalance().add(BigDecimal.valueOf(requestDto.getAmount())));
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

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Map<String, Float> getSpendingByCategory() {
        List<Transaction> transactions = StreamSupport
                .stream(transactionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.summingDouble(Transaction::getAmount)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().floatValue()
                ));
    }


    public Map<String, Float> getSpendingByMonth() {
        List<Transaction> transactions = StreamSupport
                .stream(transactionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return transactions.stream()
                .filter(t -> t.getAmount() < 0)
                .collect(Collectors.groupingBy(
                        t -> t.getTimestamp().getYear() + "-" + String.format("%02d", t.getTimestamp().getMonthValue()),
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().floatValue(),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    public Map<String, Float> getSpendingByWeek() {
        List<Transaction> transactions = StreamSupport
                .stream(transactionRepository.findAll().spliterator(), false)
                .filter(t -> t.getAmount() < 0) // Только траты
                .collect(Collectors.toList());

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> {
                            LocalDateTime date = transaction.getTimestamp();
                            WeekFields weekFields = WeekFields.of(Locale.getDefault());
                            int weekNumber = date.get(weekFields.weekOfYear());
                            return "Week " + weekNumber + " (" + date.getYear() + ")";
                        },
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().floatValue(),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }


    public String getTopSpendingCategory() {
        Map<String, Float> spendingByCategory = getSpendingByCategory();
        return spendingByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No category found");
    }

    public String getTopSpendingMonth() {
        Map<String, Float> spendingByMonth = getSpendingByMonth();
        return spendingByMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No month found");
    }

    public Float getTotalSpending() {
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false)
                .filter(t -> t.getAmount() < 0)
                .map(t -> t.getAmount().floatValue())
                .reduce(0f, Float::sum);
    }



}
