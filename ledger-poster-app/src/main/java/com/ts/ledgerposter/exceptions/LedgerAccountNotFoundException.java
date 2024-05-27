package com.ts.ledgerposter.exceptions;

import lombok.Getter;

@Getter
public class LedgerAccountNotFoundException extends RuntimeException {
    private final String accountNumber;
    private String timestamp;

    public LedgerAccountNotFoundException(String accountNumber) {
        super("Ledger Account not found for accountNumber:" + accountNumber);
        this.accountNumber = accountNumber;
    }

    public LedgerAccountNotFoundException(String accountNumber, String timestamp) {
        super(String.format("Ledger Account not found for accountNumber: %s and timestamp: %s", accountNumber, timestamp));
        this.accountNumber = accountNumber;
        this.timestamp = timestamp;
    }
}
