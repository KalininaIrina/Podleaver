package com.podlive.tracker.currency.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionRequestDto {
    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String fromCode;

    @NotBlank
    private String toCode;
}
