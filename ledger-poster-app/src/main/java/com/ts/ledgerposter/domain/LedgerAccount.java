package com.ts.ledgerposter.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ledger_accounts")
public class LedgerAccount {

    @Id
    private UUID id;
    private String accountNumber;
    private String accountName;
    private double accountBalance;
    private LocalDateTime lastUpdated;
}
