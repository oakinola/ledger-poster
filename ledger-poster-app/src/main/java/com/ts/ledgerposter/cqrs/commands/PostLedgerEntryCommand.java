package com.ts.ledgerposter.cqrs.commands;

import com.ts.ledgerposter.domain.LedgerEntry;

import java.util.List;

public record PostLedgerEntryCommand(List<LedgerEntry> entries) {
}
