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
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.LinkedHashMap;
import java.time.temporal.WeekFields;
import java.util.*;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> getAll() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction getById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    private List<Transaction> getNegativeTransactions() {
        return StreamSupport
                .stream(transactionRepository.findAll().spliterator(), false)
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public Transaction create(TransactionRequestDto requestDto) {
        Account account = getAccountById(requestDto.getAccount().getId());
        Category category = getCategoryById(requestDto.getCategory().getId());

        BigDecimal amount = BigDecimal.valueOf(requestDto.getAmount());

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(amount)
                .category(category)
                .timestamp(requestDto.getTimestamp())
                .build();

        account.setStartBalance(account.getStartBalance().add(amount));
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction update(Integer id, TransactionRequestDto requestDto) {
        Transaction transaction = getById(id);

        Account oldAccount = transaction.getAccount();
        Account newAccount = getAccountById(requestDto.getAccount().getId());
        Category newCategory = getCategoryById(requestDto.getCategory().getId());

        BigDecimal newAmount = BigDecimal.valueOf(requestDto.getAmount());

        // Откат старой суммы
        oldAccount.setStartBalance(oldAccount.getStartBalance().subtract(transaction.getAmount()));
        accountRepository.save(oldAccount);

        // Применение новой суммы
        newAccount.setStartBalance(newAccount.getStartBalance().add(newAmount));
        accountRepository.save(newAccount);

        // Обновление транзакции
        transaction.setAccount(newAccount);
        transaction.setCategory(newCategory);
        transaction.setAmount(newAmount);
        transaction.setTimestamp(requestDto.getTimestamp());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void delete(Integer id) {
        Transaction transaction = getById(id);
        Account account = transaction.getAccount();
        account.setStartBalance(account.getStartBalance().subtract(transaction.getAmount()));
        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }

    // ======================== Аналитика ===========================

    public Map<String, Float> getSpendingByCategory() {
        return groupNegativeTransactions(
                t -> t.getCategory().getName()
        );
    }

    public Map<String, Float> getSpendingByMonth() {
        return groupNegativeTransactions(
                t -> String.format("%d-%02d", t.getTimestamp().getYear(), t.getTimestamp().getMonthValue()),
                true
        );
    }

    public Map<String, Float> getSpendingByWeek() {
        return groupNegativeTransactions(
                t -> {
                    LocalDateTime date = t.getTimestamp();
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int week = date.get(weekFields.weekOfYear());
                    return String.format("Week %d (%d)", week, date.getYear());
                },
                true
        );
    }

    public String getTopSpendingCategory() {
        return getSpendingByCategory().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No category found");
    }

    public String getTopSpendingMonth() {
        return getSpendingByMonth().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No month found");
    }

    public Float getTotalSpending() {
        return getNegativeTransactions().stream()
                .map(t -> t.getAmount().floatValue())
                .reduce(0f, Float::sum);
    }

    // Универсальный метод группировки
    private Map<String, Float> groupNegativeTransactions(Function<Transaction, String> classifier) {
        return groupNegativeTransactions(classifier, false);
    }

    private Map<String, Float> groupNegativeTransactions(Function<Transaction, String> classifier, boolean sortByKey) {
        Stream<Map.Entry<String, Double>> groupedStream = getNegativeTransactions().stream()
                .collect(Collectors.groupingBy(
                        classifier,
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ))
                .entrySet().stream();

        if (sortByKey) {
            groupedStream = groupedStream.sorted(Map.Entry.comparingByKey());
        }

        return groupedStream.collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().floatValue(),
                (a, b) -> b,
                LinkedHashMap::new
        ));
    }

    private Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    private Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }
}
