package com.ts.ledgerposter.service;

import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import com.ts.ledgerposter.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LedgerPostingCommandHandler {

    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public void handle(PostLedgerEntryCommand command) {
        for (LedgerEntry entry : command.entries()) {
            Optional<LedgerAccount> optionalLedgerAccount = ledgerAccountRepository.findById(entry.getTransactionAccount().getAccountNumber());
            if (optionalLedgerAccount.isPresent()) {
                LedgerAccount transactionAccount = optionalLedgerAccount.get();
                transactionAccount.setAccountBalance(transactionAccount.getAccountBalance() + (entry.getTransactionType().getValue() * entry.getTransactionAmount()));
                transactionAccount.setLastUpdated(entry.getTransactionTime());
                this.ledgerAccountRepository.save(transactionAccount);
            } else {
                LedgerAccount newTransactionAccount = LedgerAccount.builder()
                        .accountNumber(entry.getTransactionAccount().getAccountNumber())
                        .accountName(entry.getTransactionAccount().getAccountName())
                        .accountBalance(entry.getTransactionType().getValue() * entry.getTransactionAmount())
                        .lastUpdated(entry.getTransactionTime())
                        .build();
                this.ledgerAccountRepository.save(newTransactionAccount);
            }
            this.ledgerEntryRepository.save(entry);
        }
    }
}
