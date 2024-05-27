package com.ts.ledgerposter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import io.micrometer.common.util.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class LedgerPostingControllerAcceptanceTest {
    public static final String INVALID_DATETIME_STRING = "2024-15-22T26:00:00";
    public static final String VALID_DATETIME_STRING = "2024-05-22T23:00:00";
    public static final String DIFFERENT_VALID_DATETIME_STRING = "2024-05-25T23:00:00";
    public static final String ACCOUNT_NUMBER_3100 = "3100";
    public static final String ACCOUNT_NUMBER_3200 = "3200";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    LedgerAccountRepository ledgerAccountRepository;

    @Test
    void shouldGetTheAccountBalance() throws Exception {
        String transactionTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        double currentBalance = createIfNotExistsAccountData(ACCOUNT_NUMBER_3100, transactionTime);

        mockMvc.perform(get("/v1/account-balance/3100")
                        .param("timestamp", transactionTime))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is(ACCOUNT_NUMBER_3100)))
                .andExpect(jsonPath("$.accountBalance", is(currentBalance)))
                .andDo(print());
    }

    @Test
    public void givenInvalidAccountNumber_whenGetBalanceCalled_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/v1/account-balance/50A0")
                        .param("timestamp", VALID_DATETIME_STRING))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Invalid account number sent")))
                .andDo(print());
    }

    @Test
    public void givenInvalidTransactionTime_whenGetBalanceCalled_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/v1/account-balance/3200")
                        .param("timestamp", INVALID_DATETIME_STRING))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Invalid timestamp format sent")))
                .andDo(print());
    }

    @Test
    public void givenMissingAccountNumber_whenGetBalanceCalled_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/v1/account-balance/")
                        .param("timestamp", VALID_DATETIME_STRING))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenMissingTransactionTime_whenGetBalanceCalled_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/v1/account-balance/3200"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidData_whenPostLedgerEntry_thenReturnOK() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3100,"test",1000.0,  TransactionType.CR, "Some Desc", VALID_DATETIME_STRING),
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",1000.0,  TransactionType.DB, "Some Desc", VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isOk());
    }

    @Test
    void givenMissingTransaction_whenPostLedgerEntry_thenReturnBadRequest() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",1000.0,  TransactionType.DB, "Some Desc", VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Incomplete ledger transactions. There should be at least 2 transactions")))
                .andDo(print());
    }

    @Test
    void givenSameAccountNumber_whenPostLedgerEntry_thenReturnBadRequest() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test",1000.0,  TransactionType.CR, "Some Desc", VALID_DATETIME_STRING),
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",1000.0,  TransactionType.DB, "Some Desc", VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Accounts to be credited and debited must differ!")))
                .andDo(print());
    }

    @Test
    void givenDifferentTransactionAmount_whenPostLedgerEntry_thenReturnBadRequest() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3100,"test",5000.0,  TransactionType.CR, "Some Desc", VALID_DATETIME_STRING),
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",6000.0,  TransactionType.DB, "Some Desc", VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Amount to be credited and debited must be same")))
                .andDo(print());
    }

    @Test
    void givenDifferentTransactionDates_whenPostLedgerEntry_thenReturnBadRequest() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3100,"test",5000.0,  TransactionType.CR, "Some Desc", VALID_DATETIME_STRING),
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",5000.0,  TransactionType.DB, "Some Desc", DIFFERENT_VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Transaction Date must be same for both entries")))
                .andDo(print());
    }

    @Test
    void givenMissingTransactionType_whenPostLedgerEntry_thenReturnBadRequest() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3100,"test",5000.0,  null, "Some Desc", VALID_DATETIME_STRING),
                new LedgerTransactionDTO(null, ACCOUNT_NUMBER_3200,"test2",5000.0,  TransactionType.DB, "Some Desc", DIFFERENT_VALID_DATETIME_STRING)
        );

        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Missing transaction type!")))
                .andDo(print());
    }

    private double createIfNotExistsAccountData(String accountNumber, String transactionTime) {
        LedgerAccount ledgerAccount = ledgerAccountRepository.findByAccountNumberAndLastUpdated(accountNumber, LocalDateTime.parse(transactionTime, DateTimeFormatter.ISO_DATE_TIME))
                .orElse(new LedgerAccount());
        if (StringUtils.isNotEmpty(ledgerAccount.getAccountNumber())) {
            return ledgerAccount.getAccountBalance();
        }

        ledgerAccount.setId(UUID.randomUUID());
        ledgerAccount.setAccountNumber(accountNumber);
        ledgerAccount.setAccountName("Test Data Account");
        ledgerAccount.setAccountBalance(11000.0);
        ledgerAccount.setLastUpdated(LocalDateTime.parse(transactionTime, DateTimeFormatter.ISO_DATE_TIME));
        ledgerAccountRepository.saveAndFlush(ledgerAccount);
        return ledgerAccount.getAccountBalance();
    }

    private String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}