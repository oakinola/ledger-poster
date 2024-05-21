package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.domain.Transaction;
import com.ts.ledgerposter.service.LedgerPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1")
public class LedgerPostingController {

    private final LedgerPostingService ledgerPosterService;

    @GetMapping(value = "/account-balance/{accountNumber}")
    public double getAccountBalance(@PathVariable String accountNumber, @RequestParam String timestamp) {
        LocalDateTime datetime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        return ledgerPosterService.getAccountBalance(accountNumber, datetime);
    }


    @PostMapping(value = "/post-ledger")
    public ResponseEntity<Transaction> postLedgerEntry(@RequestBody List<LedgerEntry> ledgerEntries) {

        Transaction transaction = ledgerPosterService.postLedgerEntry(ledgerEntries);
        return ResponseEntity.ok(transaction);
    }
}
