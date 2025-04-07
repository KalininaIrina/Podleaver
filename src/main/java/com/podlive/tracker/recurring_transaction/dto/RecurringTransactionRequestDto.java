package com.podlive.tracker.recurring_transaction.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class RecurringTransactionRequestDto {
    private Integer accountId;
    private Integer categoryId;
    private BigDecimal amount;
    private String intervalType; // DAILY, WEEKLY, MONTHLY
    private LocalDate nextExecutionDate;
    private LocalDate endDate; // Если нужно добавлять дату окончания
    private String description;
}
