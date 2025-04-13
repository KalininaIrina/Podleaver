package com.podlive.tracker.currency.controller;

import com.podlive.tracker.currency.dto.CurrencyConversionRequestDto;
import com.podlive.tracker.currency.dto.CurrencyConversionResponseDto;
import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import com.podlive.tracker.currency.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currency", description = "Управление валютами")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    @Operation(summary = "Получить список всех валют")
    public List<CurrencyResponseDto> getAll() {
        return currencyService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить валюту по ID")
    public CurrencyResponseDto getById(@PathVariable Integer id) {
        return currencyService.getById(id);
    }

    @PostMapping
    @Operation(summary = "Создать валюту")
    public CurrencyResponseDto create(@RequestBody @Valid CurrencyRequestDto dto) {
        return currencyService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить валюту по ID")
    public CurrencyResponseDto update(@PathVariable Integer id, @RequestBody @Valid CurrencyRequestDto dto) {
        return currencyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить валюту по ID")
    public void delete(@PathVariable Integer id) {
        currencyService.delete(id);
    }

    @PostMapping("/convert")
    public BigDecimal convert(@RequestBody @Valid CurrencyConversionRequestDto dto) {
        return currencyService.convert(
                dto.getAmount(),
                dto.getFromCode(),
                dto.getToCode()
        );
    }

}
