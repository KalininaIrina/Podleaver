package com.podlive.tracker.transaction.controller;

import com.podlive.tracker.transaction.dto.TransactionRequestDto;
import com.podlive.tracker.transaction.dto.TransactionResponseDto;
import com.podlive.tracker.transaction.mapper.TransactionMapper;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/transaction")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper responseMapper;

    @Operation(summary = "Get all transactions")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll() {
        return ResponseEntity.ok(transactionService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get transaction")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(responseMapper.map(transactionService.getById(id)));
    }

    @Operation(summary = "Add transaction")
    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@RequestBody TransactionRequestDto requestDto) {
        Transaction transaction = transactionService.create(requestDto);
        return ResponseEntity.ok(responseMapper.map(transaction));
    }

    @Operation(summary = "Update transaction")
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> update(@PathVariable Integer id, @RequestBody TransactionRequestDto requestDto) {
        Transaction transaction = transactionService.update(id, requestDto);
        return ResponseEntity.ok(responseMapper.map(transaction));
    }

    @Operation(summary = "Delete transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics/category")
    public Map<String, Float> getCategoryStats() {
        return transactionService.getSpendingByCategory();
    }

    @GetMapping("/analytics/month")
    public Map<String, Float> getMonthlyStats() {
        return transactionService.getSpendingByMonth();
    }

    @GetMapping("/analytics/week")
    public Map<String, Float> getWeeklyStats() {
        return transactionService.getSpendingByWeek();
    }

    @GetMapping("/analytics/top-category")
    public String getTopCategory() {
        return transactionService.getTopSpendingCategory();
    }

    @GetMapping("/analytics/top-month")
    public String getTopMonth() {
        return transactionService.getTopSpendingMonth();
    }

    @GetMapping("/spending/total")
    public Float getTotalSpending() {
        return transactionService.getTotalSpending();
    }

    /*@Operation(summary = "Get total spending")
    @GetMapping("/{id}")
    public Float getTotalSpending() {
        return transactionService.getTotalSpending();
    }*/


}
