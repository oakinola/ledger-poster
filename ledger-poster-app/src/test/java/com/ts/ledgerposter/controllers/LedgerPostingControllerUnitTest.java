package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.domain.TransactionType;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import com.ts.ledgerposter.service.LedgerPostingService;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LedgerPostingControllerUnitTest {

    private final String timestamp = "2024-05-21T00:00:00";
    LocalDateTime datetime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);

    private LedgerPostingCommandHandler commandHandler = Mockito.mock(LedgerPostingCommandHandler.class);
    private LedgerPostingQueryHandler queryHandler = Mockito.mock(LedgerPostingQueryHandler.class);

    private LedgerPostingController controller = new LedgerPostingController(commandHandler, queryHandler);

    @Test
    void shouldReturnAccountBalance() {
        LedgerAccountBalanceDTO balanceDTO = new LedgerAccountBalanceDTO("1000", 100);
        when(queryHandler.handle(new GetAccountBalanceQuery("1000", datetime))).thenReturn(balanceDTO);
        ResponseEntity<LedgerAccountBalanceDTO> result = controller.getAccountBalance("1000", timestamp);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(balanceDTO);
    }

    @Test
    void shouldPostLedgerAndReturnOK() {
        List<LedgerEntry> ledgerEntries = List.of(
                new LedgerEntry(LedgerAccount.builder()
                        .accountNumber("1000")
                        .accountName("test")
                        .build(),
                100,  TransactionType.CR, "Something", LocalDateTime.now()),
                new LedgerEntry(LedgerAccount.builder()
                        .accountNumber("1100")
                        .accountName("test2")
                        .build(),
                100, TransactionType.CR, "description", LocalDateTime.now()));

        ResponseEntity<Void> result = controller.postLedgerEntry(ledgerEntries);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}