package com.podlive.tracker.account.dto;

import com.podlive.tracker.currency.dto.CurrencyResponseDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
    private Integer id;
    private String name;
    private CurrencyResponseDto currency;
    private String startBalance;
}
