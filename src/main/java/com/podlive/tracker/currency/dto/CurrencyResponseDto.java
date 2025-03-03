package com.podlive.tracker.currency.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponseDto {
    private Integer id;
    private String name;
    private String code;
}
