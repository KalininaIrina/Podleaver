package com.podlive.tracker.currency.mapper;

import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import com.podlive.tracker.currency.model.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public Currency toEntity(CurrencyRequestDto dto) {
        return Currency.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .rateToBase(dto.getRateToBase())
                .build();
    }

    public CurrencyResponseDto toResponseDto(Currency currency) {
        return CurrencyResponseDto.builder()
                .id(currency.getId())
                .name(currency.getName())
                .code(currency.getCode())
                .rateToBase(currency.getRateToBase())
                .build();
    }

    public void updateEntity(Currency currency, CurrencyRequestDto dto) {
        currency.setName(dto.getName());
        currency.setCode(dto.getCode());
        currency.setRateToBase(dto.getRateToBase());
    }
}
