package com.ts.ledgerposter.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LedgerEntry {
    private LedgerAccount transactionAccount;
    private double amount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime recordTimestamp;

}
