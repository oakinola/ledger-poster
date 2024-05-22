package com.ts.ledgerposter.dto;

import java.time.LocalDateTime;

public record LedgerAccountBalanceDTO(
        String accountNumber,
        double accountBalance) {
}
