package com.ts.ledgerposter.service;

import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.domain.Transaction;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerPostingService {

    List<LedgerEntry> ledgerEntries = new ArrayList<>();

    public Transaction postLedgerEntry(List<LedgerEntry> entries) {
        String transactionId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now();

        ledgerEntries.addAll(entries);

        Transaction transaction = new Transaction();
        transaction.setTransactionDate(timestamp);
        transaction.setTransactions(entries);

        return transaction;
    }


    public double getAccountBalance(String accountNumber, LocalDateTime timestamp) {

            return ledgerEntries.stream()
                    .filter(entry -> entry.getRecordTimestamp().equals(timestamp)
                        && entry.getTransactionAccount().getAccountNo().equals(accountNumber))
                    .mapToDouble(entry -> entry.getTransactionType().getValue() * entry.getAmount())
                    .sum();
    }
}
