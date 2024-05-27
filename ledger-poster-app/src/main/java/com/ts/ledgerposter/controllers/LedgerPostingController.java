package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.dto.LedgerAccountBalanceResponseDTO;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import com.ts.ledgerposter.validators.LedgerPostingValidator;
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

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/v1")
public class LedgerPostingController {

    private final LedgerPostingCommandHandler commandHandler;
    private final LedgerPostingQueryHandler queryHandler;
    private final LedgerPostingValidator validator;

    @GetMapping(value = "/account-balance/{accountNumber}")
    public ResponseEntity<LedgerAccountBalanceResponseDTO> getAccountBalance(@PathVariable String accountNumber, @RequestParam String timestamp) {
        validator.validateGetBalanceRequest(accountNumber, timestamp);
        final var result = queryHandler.handle(new GetAccountBalanceQuery(accountNumber, timestamp));
        log.info("Get account balance result: '{}'", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/post-ledger")
    public ResponseEntity<Void> postLedgerEntry(@RequestBody List<LedgerTransactionDTO> transactions) {
        validator.validatePostLedgerEntryRequest(transactions);
        commandHandler.handle(new PostLedgerEntryCommand(transactions));
        log.info("The transactions '{}' have been posted to the ledger successfully", transactions);
        return ResponseEntity.ok().build();
    }
}
