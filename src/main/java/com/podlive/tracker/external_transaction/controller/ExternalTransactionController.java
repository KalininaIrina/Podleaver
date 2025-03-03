package com.podlive.tracker.external_transaction.controller;

import com.podlive.tracker.external_transaction.dto.ExternalTransactionRequestDto;
import com.podlive.tracker.external_transaction.dto.ExternalTransactionResponseDto;
import com.podlive.tracker.external_transaction.mapper.ExternalTransactionMapper;
import com.podlive.tracker.external_transaction.model.ExternalTransaction;
import com.podlive.tracker.external_transaction.service.ExternalTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/externaltransaction")
@AllArgsConstructor
public class ExternalTransactionController {
    private final ExternalTransactionService externalTransactionService;
    private final ExternalTransactionMapper responseMapper;

    @Operation(summary = "Get all external transaction")
    @GetMapping
    public ResponseEntity<List<ExternalTransactionResponseDto>> getAll(){
        return ResponseEntity.ok(externalTransactionService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get external transaction")
    @GetMapping("/{id}")
    public ResponseEntity<ExternalTransactionResponseDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(responseMapper.map(externalTransactionService.getById(id)));
    }

    @Operation(summary = "Add external transaction")
    @PostMapping
    public ResponseEntity<ExternalTransactionResponseDto> create(@RequestBody ExternalTransactionRequestDto externalTransactionRequestDto){
        ExternalTransaction externalTransaction = externalTransactionService.create(externalTransactionRequestDto);
        return ResponseEntity.ok(responseMapper.map(externalTransaction));
    }

    @Operation(summary = "Update external transaction")
    @PutMapping("/{id}")
    public ResponseEntity<ExternalTransactionResponseDto> update(@PathVariable Integer id, @RequestBody ExternalTransactionRequestDto externalTransactionRequestDto){
        ExternalTransaction externalTransaction = externalTransactionService.update(id, externalTransactionRequestDto);
        return ResponseEntity.ok(responseMapper.map(externalTransaction));
    }

    @Operation(summary = "delete external transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<ExternalTransactionResponseDto> delete(@PathVariable Integer id){
        externalTransactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
