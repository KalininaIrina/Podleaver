package com.podlive.tracker.currency.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRequestDto {
    private Integer id;
    private String name;
    private String code;
    private BigDecimal rateToBase;
}
