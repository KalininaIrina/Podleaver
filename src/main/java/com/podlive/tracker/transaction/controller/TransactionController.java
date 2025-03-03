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

@RestController
@RequestMapping(value = "api/v1/transaction")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper responseMapper;

    @Operation(summary = "Get all transactions")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll(){
        return ResponseEntity.ok(transactionService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get transaction")
    @GetMapping("/{id}")
    private ResponseEntity<TransactionResponseDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(responseMapper.map(transactionService.getById(id)));
    }

    @Operation(summary = "Add transaction")
    @PostMapping
    private ResponseEntity<TransactionResponseDto> create(@RequestBody TransactionRequestDto requestDto){
        Transaction transaction = transactionService.create(requestDto);
        return ResponseEntity.ok(responseMapper.map(transaction));
    }

    @Operation(summary = "Update transaction")
    @PutMapping("/{id}")
    private ResponseEntity<TransactionResponseDto> update(@PathVariable Integer id, @RequestBody TransactionRequestDto requestDto){
        Transaction transaction = transactionService.update(id, requestDto);
        return ResponseEntity.ok(responseMapper.map(transaction));
    }

    @Operation(summary = "Delete transaction")
    @DeleteMapping("/{id}")
    private ResponseEntity<TransactionResponseDto> delete(@PathVariable Integer id){
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
