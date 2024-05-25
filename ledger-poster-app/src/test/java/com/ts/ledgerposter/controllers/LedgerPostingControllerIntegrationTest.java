package com.ts.ledgerposter.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.dto.LedgerAccountBalanceResponseDTO;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import com.ts.ledgerposter.exceptions.InvalidLedgerPostingDataException;
import com.ts.ledgerposter.exceptions.LedgerAccountNotFoundException;
import com.ts.ledgerposter.service.LedgerPostingCommandHandler;
import com.ts.ledgerposter.service.LedgerPostingQueryHandler;
import com.ts.ledgerposter.validators.LedgerPostingValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
class LedgerPostingControllerIntegrationTest {
    public static final String ACCOUNT_NUMBER_3100 = "3100";
    public static final String ACCOUNT_NUMBER_3200 = "3200";

    public static final String NONE_EXISTING_ACCOUNT_NUMBER_5100 = "5100";
    public static final String INVALID_ACCOUNT_NUMBER_50A0 = "50A0";
    public static final String INVALID_DATETIME_STRING = "2024-15-22T26:00:00";
    public static final String TRANSACTION_BALANCE_DATETIME_STRING = "2024-05-22T23:00:00";

    public static final LocalDateTime TRANSACTION_BALANCE_DATETIME = LocalDateTime.parse(TRANSACTION_BALANCE_DATETIME_STRING, DateTimeFormatter.ISO_DATE_TIME);
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LedgerPostingCommandHandler commandHandler;
    @MockBean
    private LedgerPostingQueryHandler queryHandler;
    @MockBean
    private LedgerPostingValidator validator;

    @Test
    void shouldGetTheAccountBalance() throws Exception {
        LedgerAccountBalanceResponseDTO balanceDTO = new LedgerAccountBalanceResponseDTO(ACCOUNT_NUMBER_3100, 100.0);
        doNothing().when(validator).validateGetBalanceRequest(ACCOUNT_NUMBER_3100, TRANSACTION_BALANCE_DATETIME_STRING);
        when(queryHandler.handle(new GetAccountBalanceQuery(ACCOUNT_NUMBER_3100, TRANSACTION_BALANCE_DATETIME))).thenReturn(balanceDTO);
        mockMvc.perform(get("/v1/account-balance/3100")
                        .param("timestamp", "2024-05-22T23:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is(ACCOUNT_NUMBER_3100)))
                .andExpect(jsonPath("$.accountBalance", is(100.0)))
                .andDo(print());
        verify(validator).validateGetBalanceRequest(ACCOUNT_NUMBER_3100, TRANSACTION_BALANCE_DATETIME_STRING);
        verify(queryHandler).handle(new GetAccountBalanceQuery(ACCOUNT_NUMBER_3100, TRANSACTION_BALANCE_DATETIME));
    }

    @Test
    public void givenInvalidParams_whenGetBalanceCalled_shouldReturnBadRequestStatus() throws Exception {
        doThrow(new InvalidLedgerPostingDataException("Invalid data format passed in")).when(validator).validateGetBalanceRequest(INVALID_ACCOUNT_NUMBER_50A0, INVALID_DATETIME_STRING);
        mockMvc.perform(get("/v1/account-balance/50A0")
                        .param("timestamp", INVALID_DATETIME_STRING))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Invalid data format passed in")))
                .andDo(print());
        verify(validator).validateGetBalanceRequest(INVALID_ACCOUNT_NUMBER_50A0, INVALID_DATETIME_STRING);
    }

    @Test
    public void givenNoneExistingAccountNumber_whenGetBalanceCalled_shouldReturnNotFoundStatus() throws Exception {
        doNothing().when(validator).validateGetBalanceRequest(NONE_EXISTING_ACCOUNT_NUMBER_5100, TRANSACTION_BALANCE_DATETIME_STRING);
        when(queryHandler.handle(new GetAccountBalanceQuery(NONE_EXISTING_ACCOUNT_NUMBER_5100, TRANSACTION_BALANCE_DATETIME)))
                .thenThrow(new LedgerAccountNotFoundException(NONE_EXISTING_ACCOUNT_NUMBER_5100, TRANSACTION_BALANCE_DATETIME));

        mockMvc.perform(get("/v1/account-balance/5100")
                        .param("timestamp", TRANSACTION_BALANCE_DATETIME_STRING))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("LPS_0001")))
                .andExpect(jsonPath("$.description", is("Account specified not found: 5100")))
                .andDo(print());

        verify(validator).validateGetBalanceRequest(NONE_EXISTING_ACCOUNT_NUMBER_5100, TRANSACTION_BALANCE_DATETIME_STRING);
        verify(queryHandler).handle(new GetAccountBalanceQuery(NONE_EXISTING_ACCOUNT_NUMBER_5100, TRANSACTION_BALANCE_DATETIME));
    }


    @Test
    void givenValidData_whenPostLedgerEntry_thenReturnOK() throws Exception {
        List<LedgerTransactionDTO> transactions = List.of(
                new LedgerTransactionDTO(ACCOUNT_NUMBER_3100,"test",1000.0,  TransactionType.CR, "Some Desc", TRANSACTION_BALANCE_DATETIME_STRING),
                new LedgerTransactionDTO(ACCOUNT_NUMBER_3200,"test2",1000.0,  TransactionType.DB, "Some Desc", TRANSACTION_BALANCE_DATETIME_STRING)
        );

        doNothing().when(validator).validatePostLedgerEntryRequest(transactions);
        doNothing().when(commandHandler).handle(new PostLedgerEntryCommand(transactions));
        mockMvc.perform(post("/v1/post-ledger")
                .contentType("application/json")
                .content(toJson(transactions)))
                .andExpect(status().isOk());

        verify(validator).validatePostLedgerEntryRequest(transactions);
        verify(commandHandler).handle(new PostLedgerEntryCommand(transactions));
    }

    @Test
    void givenAnyInvalidData_whenPostLedgerEntry_thenBadRequest() throws Exception {
        List<LedgerTransactionDTO> invalidTransactions = List.of(
                new LedgerTransactionDTO(ACCOUNT_NUMBER_3200,"test",1000.0,  TransactionType.CR, "Some Desc", INVALID_DATETIME_STRING),
                new LedgerTransactionDTO(ACCOUNT_NUMBER_3200,"test2",1700.0,  null, "Some Desc", INVALID_DATETIME_STRING)
        );

        doThrow(new InvalidLedgerPostingDataException("Some data pass in is invalid")).when(validator).validatePostLedgerEntryRequest(invalidTransactions);
        mockMvc.perform(post("/v1/post-ledger")
                        .contentType("application/json")
                        .content(toJson(invalidTransactions)))
                .andExpect(status().isBadRequest());

        verify(validator).validatePostLedgerEntryRequest(invalidTransactions);
    }

    private String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}