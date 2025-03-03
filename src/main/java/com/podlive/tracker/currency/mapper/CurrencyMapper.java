package com.podlive.tracker.currency.mapper;

import com.podlive.tracker.common.service.GenericMapper;
import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import com.podlive.tracker.currency.model.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper extends GenericMapper<Currency, CurrencyResponseDto> {
}
