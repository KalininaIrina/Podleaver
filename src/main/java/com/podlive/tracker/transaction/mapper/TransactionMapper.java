package com.podlive.tracker.transaction.mapper;

import com.podlive.tracker.common.service.GenericMapper;
import com.podlive.tracker.transaction.dto.TransactionResponseDto;
import com.podlive.tracker.transaction.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends GenericMapper<Transaction, TransactionResponseDto> {
}
