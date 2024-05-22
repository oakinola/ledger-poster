package com.ts.ledgerposter.cqrs.queries;

import java.time.LocalDateTime;

public record GetAccountBalanceQuery(String accountNumber, LocalDateTime timestamp) {
}
