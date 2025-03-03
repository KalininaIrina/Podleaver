package com.podlive.tracker.external_transaction.dto;

import com.podlive.tracker.transaction.dto.TransactionRequestDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalTransactionRequestDto {
    private Integer id;
    private TransactionRequestDto transaction;
    private String externalTransaction;
    private String sourceId;
}
