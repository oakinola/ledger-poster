package com.ts.ledgerposter.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Transaction {

    private String id;
    private LocalDateTime transactionDate;
    private List<LedgerEntry> transactions;

}
