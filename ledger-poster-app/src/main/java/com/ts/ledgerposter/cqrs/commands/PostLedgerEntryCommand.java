package com.ts.ledgerposter.cqrs.commands;

import com.ts.ledgerposter.dto.LedgerTransactionDTO;

import java.util.List;

public record PostLedgerEntryCommand(List<LedgerTransactionDTO> entries) {
}
