package com.ts.ledgerposter.cqrs.queries;

public record GetAccountBalanceQuery(String accountNumber, String timestamp) {
}
