package com.podlive.tracker.currency.validator;

import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CurrencyValidator {

    public void validate(CurrencyRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationException("Currency name is required.");
        }
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new ValidationException("Currency code is required.");
        }
        if (dto.getRateToBase() == null || dto.getRateToBase().doubleValue() <= 0) {
            throw new ValidationException("Rate must be greater than zero.");
        }
    }
}
