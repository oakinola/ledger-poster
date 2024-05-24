package com.ts.ledgerposter.validators;

import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.domain.LedgerEntry;
import com.ts.ledgerposter.exceptions.InvalidLedgerPostingDataException;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class LedgerPostingValidator {

    public void validateGetBalanceRequest(String accountNumber, String timestamp) {
        checkNotEmpty(accountNumber);
        checkValidAccountFormat(accountNumber);
        checkNotEmpty(timestamp);
        checkTimeStampFormat(timestamp);
    }

    public void validatePostLedgerEntryRequest(List<LedgerEntry> ledgerEntries) {
        validateLedgerEntries(ledgerEntries);
        validateEntriesAreGood(ledgerEntries);
    }

    private void checkNotEmpty(String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            throw new InvalidLedgerPostingDataException("Invalid request data passed");
        }
    }

    private void checkValidAccountFormat(String accountNumber) {
        try{
            Long.parseLong(accountNumber);
        } catch (NumberFormatException nfe) {
            throw new InvalidLedgerPostingDataException("Invalid account number sent");
        }
    }

    private void checkTimeStampFormat(String timestamp) {
        try{
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException dtpe) {
            throw new InvalidLedgerPostingDataException("Invalid timestamp format sent");
        }
    }

    private void validateLedgerEntries(List<LedgerEntry> ledgerEntries) {
        if (ledgerEntries == null || ledgerEntries.size() != 2) {
            throw new InvalidLedgerPostingDataException("Incomplete ledger entries. There should be at least 2 ledger entries");
        }
    }

    private void validateEntriesAreGood(List<LedgerEntry> ledgerEntries)  {
        checkAccountNumberPresentAndDiffers(ledgerEntries);
        checkAmountsSumUpToZero(ledgerEntries);
        checkTransactionDatesAreSame(ledgerEntries);
    }

    private void checkAccountNumberPresentAndDiffers(List<LedgerEntry> ledgerEntries) {
        LedgerAccount account1 = ledgerEntries.get(0).getTransactionAccount();
        LedgerAccount account2 = ledgerEntries.get(1).getTransactionAccount();

        if (StringUtils.isEmpty(account1.getAccountNumber()) || StringUtils.isEmpty(account2.getAccountNumber())){
            throw new InvalidLedgerPostingDataException("Missing Account Number! ");
        }

        if (account1.getAccountNumber().equals(account2.getAccountNumber())){
            throw new InvalidLedgerPostingDataException("Accounts to be credited and debited must differ! ");
        }
    }

    private void checkTransactionDatesAreSame(List<LedgerEntry> ledgerEntries) {
        LocalDateTime transactionTime1 = ledgerEntries.get(0).getTransactionTime();
        LocalDateTime transactionTime2 = ledgerEntries.get(1).getTransactionTime();

        if (!transactionTime1.equals(transactionTime2)) {
            throw new InvalidLedgerPostingDataException("Transaction Date must be same for both entries");
        }
    }

    private void checkAmountsSumUpToZero(List<LedgerEntry> ledgerEntries) {
        double sum = 0;
        for (LedgerEntry entry : ledgerEntries) {
            sum = sum + entry.getTransactionAmount() * entry.getTransactionType().getValue();
        }
        if ( sum != 0.0) {
            throw new InvalidLedgerPostingDataException("Amount to be credited and debited ");
        }
    }
}
