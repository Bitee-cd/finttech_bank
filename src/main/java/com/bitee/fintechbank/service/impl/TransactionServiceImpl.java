package com.bitee.fintechbank.service.impl;

import com.bitee.fintechbank.dto.TransactionDto;
import com.bitee.fintechbank.entity.Transaction;
import com.bitee.fintechbank.enums.TransactionStatus;
import com.bitee.fintechbank.repository.TransactionRepository;
import com.bitee.fintechbank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status(TransactionStatus.SUCCESS)
                .build();
        transactionRepository.save(transaction);
        System.out.println("transaction saved successfully");
    }
}
