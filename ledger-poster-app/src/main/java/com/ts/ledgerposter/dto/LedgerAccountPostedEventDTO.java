package com.ts.ledgerposter.dto;

import java.util.UUID;

public record LedgerAccountPostedEventDTO (
        UUID id,
        String accountNumber,
        String accountName,
        double accountBalance,
        TransactionType transactionType,
        String lastUpdated){
}