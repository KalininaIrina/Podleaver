package com.podlive.tracker.currency.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyConversionResponseDto {
    private BigDecimal originalAmount;
    private String fromCode;
    private String toCode;
    private BigDecimal convertedAmount;
    private BigDecimal rate;
}
