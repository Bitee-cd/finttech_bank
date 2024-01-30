package com.bitee.fintechbank.service;

import com.bitee.fintechbank.dto.TransactionDto;
import com.bitee.fintechbank.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
