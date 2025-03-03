package com.podlive.tracker.external_transaction.service;

import com.podlive.tracker.common.service.CrudService;
import com.podlive.tracker.external_transaction.dto.ExternalTransactionRequestDto;
import com.podlive.tracker.external_transaction.model.ExternalTransaction;
import com.podlive.tracker.external_transaction.repository.ExternalTransactionRepository;
import com.podlive.tracker.transaction.model.Transaction;
import com.podlive.tracker.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ExternalTransactionService extends CrudService<ExternalTransaction, Integer> {
    private final ExternalTransactionRepository externalTransactionRepository;
    private final TransactionRepository transactionRepository;

    public ExternalTransactionService(ExternalTransactionRepository externalTransactionRepository, TransactionRepository transactionRepository){
        super(externalTransactionRepository);
        this.externalTransactionRepository = externalTransactionRepository;
        this.transactionRepository = transactionRepository;
    }

    public ExternalTransaction create(ExternalTransactionRequestDto requestDto){
        Transaction transaction = transactionRepository.findById(requestDto.getTransaction().getId()).orElseThrow(EntityNotFoundException::new);
        ExternalTransaction externalTransaction = ExternalTransaction.builder()
                .transaction(transaction)
                .externalTransaction(requestDto.getExternalTransaction())
                .sourceId(requestDto.getSourceId())
                .build();
        return save(externalTransaction);
    }

    public ExternalTransaction update (Integer id, ExternalTransactionRequestDto requestDto){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ExternalTransaction externalTransaction = externalTransactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        externalTransaction.setExternalTransaction(requestDto.getExternalTransaction());
        externalTransaction.setTransaction(transaction);
        externalTransaction.setSourceId(requestDto.getSourceId());
        return externalTransactionRepository.save(externalTransaction);
    }
}
