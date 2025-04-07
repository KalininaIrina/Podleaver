package com.podlive.tracker.recurring_transaction.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RecurringTransactionResponseDto {
    private Integer id;
    private Integer accountId;
    private Integer categoryId;
    private BigDecimal amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer frequencyDays;
}
