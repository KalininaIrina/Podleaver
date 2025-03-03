package com.podlive.tracker.transaction.dto;

import com.podlive.tracker.account.dto.AccountResponseDto;
import com.podlive.tracker.category.dto.CategoryResponseDto;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseDto {
    private Integer id;
    private AccountResponseDto account;
    private Float amount;
    private LocalDate timestamp;
    private CategoryResponseDto category;
}
