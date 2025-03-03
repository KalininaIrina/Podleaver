package com.podlive.tracker.account.controller;

import com.podlive.tracker.account.dto.AccountRequestDto;
import com.podlive.tracker.account.dto.AccountResponseDto;
import com.podlive.tracker.account.mapper.AccountMapper;
import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "ape/v1/account")
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper responseMapper;

    @Operation(summary = "Get all accounts")
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getALl(){
        return ResponseEntity.ok(accountService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get account")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(responseMapper.map(accountService.getById(id)));
    }

    @Operation(summary = "Update account")
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> update(@PathVariable Integer id, @RequestBody AccountRequestDto accountRequestDto){
        Account account = accountService.update(id, accountRequestDto);
        return ResponseEntity.ok(responseMapper.map(account));
    }

    @Operation(summary = "Add account")
    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@RequestBody AccountRequestDto accountRequestDto){
        Account account = accountService.create(accountRequestDto);
        return ResponseEntity.ok(responseMapper.map(account));
    }

    @Operation(summary = "Delete account")
    @DeleteMapping("/{id}")
    public ResponseEntity<AccountResponseDto> delete(@PathVariable Integer id){
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

