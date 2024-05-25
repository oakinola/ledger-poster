package com.ts.ledgerposter.validators;

import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import com.ts.ledgerposter.exceptions.InvalidLedgerPostingDataException;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class LedgerPostingValidator {

    public void validateGetBalanceRequest(String accountNumber, String timestamp) {
        checkNotEmpty(accountNumber);
        checkNotEmpty(timestamp);
        checkValidAccountFormat(accountNumber);
        checkTimeStampFormat(timestamp);
    }

    public void validatePostLedgerEntryRequest(List<LedgerTransactionDTO> transactions) {
        validateTransactionsSize(transactions);
        validateTransactionsAreGood(transactions);
    }

    private void checkNotEmpty(String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            throw new InvalidLedgerPostingDataException("Invalid request data passed");
        }
    }

    private void checkValidAccountFormat(String accountNumber) {
        try {
            Long.parseLong(accountNumber);
        } catch (NumberFormatException nfe) {
            throw new InvalidLedgerPostingDataException("Invalid account number sent");
        }
    }

    private void checkTimeStampFormat(String timestamp) {
        try {
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException dtpe) {
            throw new InvalidLedgerPostingDataException("Invalid timestamp format sent");
        }
    }

    private void validateTransactionsSize(List<LedgerTransactionDTO> transactions) {
        if (transactions == null || transactions.size() != 2) {
            throw new InvalidLedgerPostingDataException("Incomplete ledger transactions. There should be at least 2 transactions");
        }
    }

    private void validateTransactionsAreGood(List<LedgerTransactionDTO> transactions)  {
        checkAccountNumberPresentAndDiffers(transactions);
        checkTransactionTypePresent(transactions);
        checkAmountsSumUpToZero(transactions);
        checkTransactionDatesAreSame(transactions);
    }

    private void checkAccountNumberPresentAndDiffers(List<LedgerTransactionDTO> transactions) {
        String accountNumber1 = transactions.get(0).getAccountNumber();
        String accountNumber2 = transactions.get(1).getAccountNumber();

        if (StringUtils.isEmpty(accountNumber1) || StringUtils.isEmpty(accountNumber2)){
            throw new InvalidLedgerPostingDataException("Missing Account Number!");
        }

        if (accountNumber1.equals(accountNumber2)){
            throw new InvalidLedgerPostingDataException("Accounts to be credited and debited must differ!");
        }
    }

    private void checkTransactionTypePresent(List<LedgerTransactionDTO> transactions) {
        TransactionType transactionType1 = transactions.get(0).getTransactionType();
        TransactionType transactionType2 = transactions.get(1).getTransactionType();

        if (transactionType1 == null || transactionType2 == null){
            throw new InvalidLedgerPostingDataException("Missing transaction type!");
        }
    }

    private void checkTransactionDatesAreSame(List<LedgerTransactionDTO> transactions) {
        String transactionTime1 = transactions.get(0).getTransactionTime();
        String transactionTime2 = transactions.get(1).getTransactionTime();

        if (!transactionTime1.equals(transactionTime2)) {
            throw new InvalidLedgerPostingDataException("Transaction Date must be same for both entries");
        }
    }

    private void checkAmountsSumUpToZero(List<LedgerTransactionDTO> transactions) {
        double transactionsSum = 0;
        for (LedgerTransactionDTO transaction : transactions) {
            transactionsSum += transaction.getTransactionAmount() * transaction.getTransactionType().getValue();
        }
        if ( transactionsSum != 0.0) {
            throw new InvalidLedgerPostingDataException("Amount to be credited and debited must be same");
        }
    }
}
