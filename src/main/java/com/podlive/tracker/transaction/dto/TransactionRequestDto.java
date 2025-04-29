package com.podlive.tracker.transaction.dto;

import com.podlive.tracker.account.dto.AccountRequestDto;
import com.podlive.tracker.category.dto.CategoryRequestDto;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDto {
    private Integer id;
    private AccountRequestDto account;
    private Float amount;
    private LocalDateTime timestamp;
    private CategoryRequestDto category;
}
