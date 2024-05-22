package com.ts.ledgerposter.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class LedgerAccountNotFoundException extends RuntimeException {
    private final String accountNumber;
    private LocalDateTime timestamp;

    public LedgerAccountNotFoundException(String accountNumber) {
        super("Ledger Account not found for accountNumber:" + accountNumber);
        this.accountNumber = accountNumber;
    }

    public LedgerAccountNotFoundException(String accountNumber, LocalDateTime timestamp) {
        super(String.format("Ledger Account not found for accountNumber: %s and timestamp: %s", accountNumber, timestamp));
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
    }
}
