package com.bitee.fintechbank.dto;

import com.bitee.fintechbank.enums.TransactionStatus;
import com.bitee.fintechbank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private TransactionType transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private TransactionStatus status;
}
