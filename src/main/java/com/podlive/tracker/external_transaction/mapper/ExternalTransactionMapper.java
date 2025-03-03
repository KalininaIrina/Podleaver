package com.podlive.tracker.external_transaction.mapper;

import com.podlive.tracker.common.service.GenericMapper;
import com.podlive.tracker.external_transaction.dto.ExternalTransactionResponseDto;
import com.podlive.tracker.external_transaction.model.ExternalTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalTransactionMapper extends GenericMapper<ExternalTransaction, ExternalTransactionResponseDto> {
}
