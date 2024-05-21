package com.ts.ledgerposter.domain;

public enum TransactionType {
    CR(1),
    DB(-1);

    private final int value;

    TransactionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
