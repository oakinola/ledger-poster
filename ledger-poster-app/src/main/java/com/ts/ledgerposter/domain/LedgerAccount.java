package com.ts.ledgerposter.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "ledger_accounts")
public class LedgerAccount {
    @Id
    private String accountNumber;
    private String accountName;
    private double accountBalance;
    private LocalDateTime lastUpdated;
}
