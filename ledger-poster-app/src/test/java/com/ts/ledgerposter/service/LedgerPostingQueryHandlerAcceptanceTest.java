package com.ts.ledgerposter.service;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountBalanceResponseDTO;
import com.ts.ledgerposter.exceptions.LedgerAccountNotFoundException;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LedgerPostingQueryHandlerAcceptanceTest {

    @Autowired
    private LedgerAccountRepository ledgerAccountRepository;
    @Autowired
    private LedgerPostingQueryHandler ledgerPostingQueryHandler;

    @Test
    void givenValidParams_whenHandle_shouldReturnSuccessfulResult() {
        double balanceExpected = createIfNotExistsAccountData("1100", "2024-05-21T00:00:00");
        LedgerAccountBalanceResponseDTO expectedResult = new LedgerAccountBalanceResponseDTO("1100", balanceExpected);
        GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQuery("1100", "2024-05-21T00:00:00");
        LedgerAccountBalanceResponseDTO actualResult = ledgerPostingQueryHandler.handle(getAccountBalanceQuery);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void givenWhenHandleCalled_whenAccountNotFound_shouldThrowException() {
        GetAccountBalanceQuery getAccountBalanceQuery = new GetAccountBalanceQuery("5000", "2024-05-21T00:00:00");
        String expectedMessage = String.format("Ledger Account not found for accountNumber: %s and timestamp: %s", "5000", "2024-05-21T00:00:00");
        LedgerAccountNotFoundException ledgerAccountNotFoundException = assertThrows(LedgerAccountNotFoundException.class, () -> ledgerPostingQueryHandler.handle(getAccountBalanceQuery));
        assertEquals(expectedMessage, ledgerAccountNotFoundException.getMessage());
    }

    private double createIfNotExistsAccountData(String accountNumber, String transactionTime) {

        LedgerAccount ledgerAccount = ledgerAccountRepository.findByAccountNumberAndLastUpdated(accountNumber, LocalDateTime.parse(transactionTime, DateTimeFormatter.ISO_DATE_TIME))
                .orElse(new LedgerAccount());
        if (StringUtils.isNotEmpty(ledgerAccount.getAccountNumber())) {
            return ledgerAccount.getAccountBalance();
        }

        ledgerAccount.setId(UUID.randomUUID());
        ledgerAccount.setAccountNumber(accountNumber);
        ledgerAccount.setAccountName("Test Data Account");
        ledgerAccount.setAccountBalance(21000.0);
        ledgerAccount.setLastUpdated(LocalDateTime.parse(transactionTime, DateTimeFormatter.ISO_DATE_TIME));
        ledgerAccountRepository.saveAndFlush(ledgerAccount);
        return ledgerAccount.getAccountBalance();
    }

}