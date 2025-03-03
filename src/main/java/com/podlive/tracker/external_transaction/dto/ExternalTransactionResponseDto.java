package com.podlive.tracker.external_transaction.dto;

import com.podlive.tracker.transaction.dto.TransactionResponseDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalTransactionResponseDto {
    private Integer id;
    private TransactionResponseDto transaction;
    private String externalTransaction;
    private String sourceId;
}
