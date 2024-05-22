package com.ts.ledgerposter.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "transactions")
public class Transaction {

    private String id;
    private LocalDateTime transactionDate;
    private List<LedgerEntry> transactions;

}
