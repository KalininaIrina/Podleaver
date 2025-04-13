package com.podlive.tracker.currency.service;

import com.podlive.tracker.currency.dto.CurrencyConversionRequestDto;
import com.podlive.tracker.currency.dto.CurrencyConversionResponseDto;
import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import com.podlive.tracker.currency.mapper.CurrencyMapper;
import com.podlive.tracker.currency.model.Currency;
import com.podlive.tracker.currency.repository.CurrencyRepository;
import com.podlive.tracker.currency.validator.CurrencyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final CurrencyValidator currencyValidator;

    public List<CurrencyResponseDto> findAll() {
        return currencyRepository.findAll()
                .stream()
                .map(currencyMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CurrencyResponseDto create(CurrencyRequestDto dto) {
        currencyValidator.validate(dto);
        Currency currency = currencyMapper.toEntity(dto);
        return currencyMapper.toResponseDto(currencyRepository.save(currency));
    }

    public CurrencyResponseDto update(Integer id, CurrencyRequestDto dto) {
        currencyValidator.validate(dto);
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));
        currencyMapper.updateEntity(currency, dto);
        return currencyMapper.toResponseDto(currencyRepository.save(currency));
    }

    public CurrencyResponseDto getById(Integer id) {
        return currencyRepository.findById(id)
                .map(currencyMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));
    }

    public void delete(Integer id) {
        currencyRepository.deleteById(id);
    }

    public BigDecimal convert(BigDecimal amount, String fromCode, String toCode) {
        Currency fromCurrency = currencyRepository.findByCode(fromCode)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + fromCode));

        Currency toCurrency = currencyRepository.findByCode(toCode)
                .orElseThrow(() -> new RuntimeException("Currency not found: " + toCode));

        BigDecimal fromRate = fromCurrency.getRateToBase();
        BigDecimal toRate = toCurrency.getRateToBase();

        if (fromRate.compareTo(BigDecimal.ZERO) == 0 || toRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Conversion rate cannot be zero.");
        }

        return amount.multiply(fromRate).divide(toRate, 4, RoundingMode.HALF_UP);
    }


}
