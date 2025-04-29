package com.podlive.tracker.recurring_transaction.service;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.repository.CategoryRepository;
import com.podlive.tracker.recurring_transaction.model.RecurringTransaction;
import com.podlive.tracker.recurring_transaction.repository.RecurringTransactionRepository;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionService {

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionService transactionService;

    //Сохранение новой записи
    public RecurringTransaction save(RecurringTransaction recurringTransaction) {
        return recurringTransactionRepository.save(recurringTransaction);
    }

    //Генерация транзакций
    public void generateRecurringTransactions() {
        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAll();
        for (RecurringTransaction recurringTransaction : recurringTransactions) {
            if (shouldGenerateTransaction(recurringTransaction)) {
                createTransactionFromRecurring(recurringTransaction);

                // Обновление даты следующего выполнения
                recurringTransaction.setNextExecutionDate(recurringTransaction.getNextExecutionDate().plusDays(30)); // Пример для monthly
                recurringTransactionRepository.save(recurringTransaction);
            }
        }
    }

    //Проверка даты
    private boolean shouldGenerateTransaction(RecurringTransaction recurringTransaction) {
        return !recurringTransaction.getNextExecutionDate().isAfter(LocalDate.now());
    }

    //Создание транзакции
    private void createTransactionFromRecurring(RecurringTransaction recurringTransaction) {
        Transaction transaction = Transaction.builder()
                .account(recurringTransaction.getAccount())
                .category(recurringTransaction.getCategory())
                .amount(recurringTransaction.getAmount().floatValue())
                .timestamp(LocalDateTime.from(recurringTransaction.getNextExecutionDate()))
                .build();

        transactionService.save(transaction); // Просто сохраняем как есть
    }

    //Привязка сущностей по ID (если нужно использовать в контроллере)
    public RecurringTransaction prepareAndSave(RecurringTransaction recurringTransaction, Integer accountId, Integer categoryId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        recurringTransaction.setAccount(account);
        recurringTransaction.setCategory(category);

        return save(recurringTransaction);
    }

    public List<RecurringTransaction> getAllRecurringTransactions() {
        return recurringTransactionRepository.findAll();
    }

    public RecurringTransaction getRecurringTransactionById(Integer id) {
        return recurringTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurring Transaction not found"));
    }

    public List<RecurringTransaction> getAll() {
        return recurringTransactionRepository.findAll();
    }

    public Optional<RecurringTransaction> getById(Integer id) {
        return recurringTransactionRepository.findById(id);
    }

    public void delete(RecurringTransaction recurringTransaction) {
        recurringTransactionRepository.delete(recurringTransaction);
    }

}
