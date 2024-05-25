package com.ts.ledgerposter.dto;

import com.ts.ledgerposter.domain.LedgerAccount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LedgerTransactionDTO {
    private String accountNumber;
    private String accountName;
    private double transactionAmount;
    private TransactionType transactionType;
    private String description;
    private String transactionTime;

}
