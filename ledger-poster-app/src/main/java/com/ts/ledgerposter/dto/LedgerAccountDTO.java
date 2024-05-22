package com.ts.ledgerposter.dto;

import java.time.LocalDateTime;

public record LedgerAccountDTO(
        String accountNumber,
        String accountName,
        double accountBalance,
        LocalDateTime lastUpdated) {
}
