package com.ts.ledgerposter.dto;

import com.ts.ledgerposter.domain.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record LedgerAccountTransactionDTO(
        UUID id,
        String accountNumber,
        String accountName,
        double transactionAmount,
        TransactionType transactionType,
        LocalDateTime lastUpdated) {
}
