package com.ts.ledgerposter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class LedgerTransactionDTO {
    private UUID transactionEntryId;
    private String accountNumber;
    private String accountName;
    private double transactionAmount;
    private TransactionType transactionType;
    private String description;
    private String transactionTime;

}
