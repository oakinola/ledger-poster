package com.ts.ledgerposter.service;

import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LedgerPostingCommandHandlerAcceptanceTest {

    @Autowired
    LedgerPostingCommandHandler ledgerPostingCommandHandler;

    @Test
    void verifyHandleSuccessfullyCalled() {

        List<LedgerTransactionDTO> transactionEntries = List.of(
                new LedgerTransactionDTO(null, "1100","test2",100.0,  TransactionType.DB, "Something", "2024-05-21T00:00:00")
        );
        PostLedgerEntryCommand postLedgerEntryCommand = new PostLedgerEntryCommand(transactionEntries);
        ledgerPostingCommandHandler.handle(postLedgerEntryCommand);
    }
}