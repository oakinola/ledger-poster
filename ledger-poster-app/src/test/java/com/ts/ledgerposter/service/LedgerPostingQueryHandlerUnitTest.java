package com.ts.ledgerposter.service;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountBalanceResponseDTO;
import com.ts.ledgerposter.exceptions.LedgerAccountNotFoundException;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LedgerPostingQueryHandlerUnitTest {

    private final LedgerAccountRepository ledgerAccountRepositoryMock = mock(LedgerAccountRepository.class);
    private final LedgerPostingQueryHandler ledgerPostingQueryHandler = new LedgerPostingQueryHandler(ledgerAccountRepositoryMock);

    @Test
    void whenHandle_shouldReturnSuccessfulResult() {
        LedgerAccount ledgerAccount = new LedgerAccount(null, "1000", null, 1000.0, null);
        LedgerAccountBalanceResponseDTO expectedResult = new LedgerAccountBalanceResponseDTO("1000", 1000.0);
        GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQuery("1000", "2024-05-21T00:00:00");

        when(ledgerAccountRepositoryMock.findByAccountNumberAndLastUpdated(getAccountBalanceQuery.accountNumber(),
                LocalDateTime.parse(getAccountBalanceQuery.timestamp(), DateTimeFormatter.ISO_DATE_TIME))).thenReturn(Optional.of(ledgerAccount));

        LedgerAccountBalanceResponseDTO actualResult = ledgerPostingQueryHandler.handle(getAccountBalanceQuery);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void givenWhenHandleCalled_whenAccountNotFound_shouldThrowException() {
        GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQuery("1000", "2024-05-21T00:00:00");
        String expectedMessage = String.format("Ledger Account not found for accountNumber: %s and timestamp: %s", "1000", "2024-05-21T00:00:00");
        when(ledgerAccountRepositoryMock.findByAccountNumberAndLastUpdated(getAccountBalanceQuery.accountNumber(),
                LocalDateTime.parse(getAccountBalanceQuery.timestamp(), DateTimeFormatter.ISO_DATE_TIME))).thenReturn(Optional.empty());
        LedgerAccountNotFoundException ledgerAccountNotFoundException = assertThrows(LedgerAccountNotFoundException.class, () -> ledgerPostingQueryHandler.handle(getAccountBalanceQuery));
        assertEquals(expectedMessage, ledgerAccountNotFoundException.getMessage());
    }
}