package com.podlive.tracker.account.dto;

import com.podlive.tracker.currency.dto.CurrencyRequestDto;
import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {
    private Integer id;
    private String name;
    private CurrencyRequestDto currency;
    private String startBalance;
}
