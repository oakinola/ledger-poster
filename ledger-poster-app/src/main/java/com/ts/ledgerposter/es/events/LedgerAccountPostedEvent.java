package com.ts.ledgerposter.es.events;

import com.ts.ledgerposter.dto.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "ledger_post_events")
public class LedgerAccountPostedEvent {
    @Id
    private final UUID id;
    private String accountNumber;
    private String accountName;
    private double accountBalance;
    private TransactionType transactionType;
    private final LocalDateTime lastUpdated;
}
