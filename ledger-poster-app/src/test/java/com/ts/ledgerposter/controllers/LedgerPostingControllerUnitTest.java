package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.domain.TransactionType;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import com.ts.ledgerposter.validators.LedgerPostingValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class LedgerPostingControllerUnitTest {

    private final String timestamp = "2024-05-21T00:00:00";
    LocalDateTime test_datetime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);

    private final LedgerPostingCommandHandler commandHandler = Mockito.mock(LedgerPostingCommandHandler.class);
    private final LedgerPostingQueryHandler queryHandler = Mockito.mock(LedgerPostingQueryHandler.class);
    private final LedgerPostingValidator validator = Mockito.mock(LedgerPostingValidator.class);

    private final LedgerPostingController controller = new LedgerPostingController(commandHandler, queryHandler, validator);

    @Test
    void shouldReturnAccountBalance() {
        LedgerAccountBalanceDTO balanceDTO = new LedgerAccountBalanceDTO("1000", 100);
        when(queryHandler.handle(new GetAccountBalanceQuery("1000", test_datetime))).thenReturn(balanceDTO);
        ResponseEntity<LedgerAccountBalanceDTO> result = controller.getAccountBalance("1000", timestamp);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(balanceDTO);
    }

    @Test
    void shouldPostLedgerAndReturnOK() {
        List<LedgerEntry> ledgerEntries = List.of(
                new LedgerEntry(new LedgerAccount(
                        UUID.randomUUID(),
                        "1000",
                        "test",
                        0.0,
                        null),
                        100,  TransactionType.CR, "Something", test_datetime),
                new LedgerEntry(new LedgerAccount(
                        UUID.randomUUID(),
                        "1100",
                        "test2",
                        0.0,
                        null),
                        100,  TransactionType.CR, "Something", test_datetime));

        ResponseEntity<Void> result = controller.postLedgerEntry(ledgerEntries);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}