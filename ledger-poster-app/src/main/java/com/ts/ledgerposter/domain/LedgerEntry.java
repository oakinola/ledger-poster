package com.ts.ledgerposter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LedgerEntry {
    private LedgerAccount transactionAccount;
    private double transactionAmount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime transactionTime;

}
