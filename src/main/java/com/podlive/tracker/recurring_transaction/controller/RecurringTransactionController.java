package com.podlive.tracker.recurring_transaction.controller;

import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.repository.AccountRepository;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.repository.CategoryRepository;
import com.podlive.tracker.recurring_transaction.dto.RecurringTransactionRequestDto;
import com.podlive.tracker.recurring_transaction.model.RecurringTransaction;
import com.podlive.tracker.recurring_transaction.service.RecurringTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recurring-transaction")
@Tag(name = "Recurring Transactions", description = "Управление повторяющимися транзакциями")
public class RecurringTransactionController {

    @Autowired
    private RecurringTransactionService recurringTransactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Operation(
            summary = "Создать повторяющуюся транзакцию",
            description = "Создаёт новую повторяющуюся транзакцию, которая будет генерировать обычные транзакции по расписанию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "404", description = "Account или Category не найдены")
    })
    @PostMapping
    public void createRecurringTransaction(@RequestBody RecurringTransactionRequestDto requestDto) {
        RecurringTransaction recurringTransaction = new RecurringTransaction();
        recurringTransaction.setAmount(requestDto.getAmount());

        Account account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        recurringTransaction.setAccount(account);
        recurringTransaction.setCategory(category);
        recurringTransaction.setIntervalType(requestDto.getIntervalType());
        recurringTransaction.setNextExecutionDate(requestDto.getNextExecutionDate());
        recurringTransaction.setDescription(requestDto.getDescription());

        recurringTransactionService.save(recurringTransaction);
    }

    @Operation(
            summary = "Генерировать запланированные транзакции",
            description = "Создаёт реальные транзакции на основе запланированных повторяющихся, если наступила дата исполнения"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакции успешно сгенерированы"),
            @ApiResponse(responseCode = "400", description = "Ошибка генерации транзакций")
    })
    @PostMapping("/generate")
    public void generateRecurringTransactions() {
        recurringTransactionService.generateRecurringTransactions();
    }

    @Operation(
            summary = "Получить все повторяющиеся транзакции",
            description = "Возвращает список всех повторяющихся транзакций"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список транзакций"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса")
    })
    @GetMapping
    public List<RecurringTransaction> getAllRecurringTransactions() {
        return recurringTransactionService.getAllRecurringTransactions();
    }

    @Operation(
            summary = "Получить повторяющуюся транзакцию по ID",
            description = "Возвращает повторяющуюся транзакцию по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция найдена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    @GetMapping("/{id}")
    public RecurringTransaction getRecurringTransactionById(@PathVariable Integer id) {
        return recurringTransactionService.getRecurringTransactionById(id);
    }

    @Operation(
            summary = "Обновить повторяющуюся транзакцию",
            description = "Обновляет существующую повторяющуюся транзакцию"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    @PutMapping("/{id}")
    public void updateRecurringTransaction(@PathVariable Integer id, @RequestBody RecurringTransactionRequestDto requestDto) {
        RecurringTransaction recurringTransaction = recurringTransactionService.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurring transaction not found"));

        recurringTransaction.setAmount(requestDto.getAmount());

        Account account = accountRepository.findById(requestDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        recurringTransaction.setAccount(account);
        recurringTransaction.setCategory(category);
        recurringTransaction.setIntervalType(requestDto.getIntervalType());
        recurringTransaction.setNextExecutionDate(requestDto.getNextExecutionDate());
        recurringTransaction.setDescription(requestDto.getDescription());

        recurringTransactionService.save(recurringTransaction);
    }

    @Operation(
            summary = "Удалить повторяющуюся транзакцию",
            description = "Удаляет повторяющуюся транзакцию по её ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена")
    })
    @DeleteMapping("/{id}")
    public void deleteRecurringTransaction(@PathVariable Integer id) {
        RecurringTransaction recurringTransaction = recurringTransactionService.getById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recurring transaction not found"));

        recurringTransactionService.delete(recurringTransaction);
    }
}
