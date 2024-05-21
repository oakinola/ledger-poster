package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.service.LedgerPostingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LedgerPostingControllerUnitTest {

    private final String timestamp1 = "2024-05-21T00:00:00";
    LocalDateTime datetime = LocalDateTime.parse(timestamp1, DateTimeFormatter.ISO_DATE_TIME);

    private LedgerPostingService ledgerService = Mockito.mock(LedgerPostingService.class);
    private LedgerPostingController ledgerPostingController = new LedgerPostingController(ledgerService);

    @Test
    void shouldReturnAccountBalance() {
        when(ledgerService.getAccountBalance("1000", datetime)).thenReturn(100d);
        assertEquals(100, ledgerPostingController.getAccountBalance("1000", timestamp1));
    }

    @Test
    void shouldReturnReturnAccountBalance() {
        when(ledgerService.getAccountBalance("1000", datetime)).thenReturn(100d);
        assertEquals(100, ledgerPostingController.getAccountBalance("1000", timestamp1));
    }

}