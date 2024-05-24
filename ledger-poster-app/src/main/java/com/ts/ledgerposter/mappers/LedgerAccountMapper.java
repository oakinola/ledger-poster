package com.ts.ledgerposter.mappers;

import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.dto.LedgerAccountPostedEventDTO;
import com.ts.ledgerposter.dto.LedgerAccountTransactionDTO;
import com.ts.ledgerposter.es.events.LedgerAccountPostedEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LedgerAccountMapper {

    private LedgerAccountMapper() {
    }

    public static LedgerAccountPostedEventDTO buildLedgerAccountPostedEventDTOFromLedgerAccountPostedEvent(LedgerAccountPostedEvent event){
        return new LedgerAccountPostedEventDTO(
                event.getId(),
                event.getAccountNumber(),
                event.getAccountName(),
                event.getAccountBalance(),
                event.getTransactionType(),
                event.getLastUpdated().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    public static LedgerAccountPostedEvent buildLedgerAccountPostedEventFromLedgerAccountPostedEventDTO(LedgerAccountPostedEventDTO eventDTO){
        return new LedgerAccountPostedEvent(
                eventDTO.id(),
                eventDTO.accountNumber(),
                eventDTO.accountName(),
                eventDTO.accountBalance(),
                eventDTO.transactionType(),
                LocalDateTime.parse(eventDTO.lastUpdated(), DateTimeFormatter.ISO_DATE_TIME));
    }

    public static LedgerAccountBalanceDTO buildLedgerAccountBalanceDTOFromLedgerAccount(LedgerAccount ledgerAccount) {
        return new LedgerAccountBalanceDTO(
                ledgerAccount.getAccountNumber(),
                ledgerAccount.getAccountBalance()
        );
    }

    public static LedgerAccountTransactionDTO buildLedgerAccountTransactionDTOFromLedgerAccountPostedEvent(LedgerAccountPostedEvent ledgerAccountEvent) {
        return new LedgerAccountTransactionDTO(
                ledgerAccountEvent.getId(),
                ledgerAccountEvent.getAccountNumber(),
                ledgerAccountEvent.getAccountName(),
                ledgerAccountEvent.getAccountBalance(),
                ledgerAccountEvent.getTransactionType(),
                ledgerAccountEvent.getLastUpdated());
    }

    public static LedgerAccountTransactionDTO buildLedgerAccountTransactionDTOFromLedgerAccountPostedEventDTO(LedgerAccountPostedEventDTO eventDTO) {
        return new LedgerAccountTransactionDTO(
                eventDTO.id(),
                eventDTO.accountNumber(),
                eventDTO.accountName(),
                eventDTO.accountBalance(),
                eventDTO.transactionType(),
                LocalDateTime.parse(eventDTO.lastUpdated(), DateTimeFormatter.ISO_DATE_TIME));
    }
}