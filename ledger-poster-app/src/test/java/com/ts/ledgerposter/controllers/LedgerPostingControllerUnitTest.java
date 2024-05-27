package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.dto.LedgerAccountBalanceResponseDTO;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import com.ts.ledgerposter.validators.LedgerPostingValidator;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

class LedgerPostingControllerUnitTest {

    private final String STRING_TEST_TRANSACTION_TIME = "2024-05-21T00:00:00";

    private final LedgerPostingCommandHandler commandHandler = mock(LedgerPostingCommandHandler.class);
    private final LedgerPostingQueryHandler queryHandler = mock(LedgerPostingQueryHandler.class);
    private final LedgerPostingValidator validator = mock(LedgerPostingValidator.class);

    private final LedgerPostingController controller = new LedgerPostingController(commandHandler, queryHandler, validator);

    @Test
    void shouldReturnAccountBalance() {
        LedgerAccountBalanceResponseDTO balanceDTO = new LedgerAccountBalanceResponseDTO("1000", 100);
        when(queryHandler.handle(new GetAccountBalanceQuery("1000", STRING_TEST_TRANSACTION_TIME))).thenReturn(balanceDTO);
        ResponseEntity<LedgerAccountBalanceResponseDTO> result = controller.getAccountBalance("1000", STRING_TEST_TRANSACTION_TIME);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(balanceDTO);
    }

    @Test
    void shouldPostLedgerAndReturnOK() {
        List<LedgerTransactionDTO> ledgerEntries = List.of(
                new LedgerTransactionDTO(null, "1000","test",100.0,  TransactionType.CR, "Something", STRING_TEST_TRANSACTION_TIME),
                new LedgerTransactionDTO(null, "1100","test2",100.0,  TransactionType.DB, "Something", STRING_TEST_TRANSACTION_TIME)
        );
        ResponseEntity<Void> result = controller.postLedgerEntry(ledgerEntries);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}