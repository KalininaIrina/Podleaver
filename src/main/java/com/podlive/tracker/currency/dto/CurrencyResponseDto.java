package com.podlive.tracker.currency.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyResponseDto {
    private Integer id;
    private String name;
    private String code;
    private BigDecimal rateToBase;
}
