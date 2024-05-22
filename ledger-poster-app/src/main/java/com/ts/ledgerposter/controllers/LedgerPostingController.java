package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.dto.LedgerAccountDTO;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class LedgerPostingController {

    private final LedgerPostingCommandHandler commandHandler;
    private final LedgerPostingQueryHandler queryHandler;

    @GetMapping(value = "/account-balance/{accountNumber}")
    public ResponseEntity<LedgerAccountBalanceDTO> getAccountBalance(@PathVariable String accountNumber, @RequestParam String timestamp) {
        LocalDateTime datetime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        final var result = queryHandler.handle(new GetAccountBalanceQuery(accountNumber, datetime));
        log.info("Get account balance result: '{}'", result);
        return ResponseEntity.ok(result);
    }


    @PostMapping(value = "/post-ledger")
    public ResponseEntity<Void> postLedgerEntry(@RequestBody List<LedgerEntry> ledgerEntries) {
        commandHandler.handle(new PostLedgerEntryCommand(ledgerEntries));
        log.info("The entries '{}' have been posted to the ledger successfully", ledgerEntries);
        return ResponseEntity.ok().build();
    }
}
