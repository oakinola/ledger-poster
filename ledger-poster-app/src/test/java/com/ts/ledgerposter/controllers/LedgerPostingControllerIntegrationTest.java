package com.ts.ledgerposter.controllers;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
class LedgerPostingControllerIntegrationTest {

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
        LedgerAccountBalanceDTO balanceDTO = new LedgerAccountBalanceDTO("1000", 100.0);
        LocalDateTime datetime = LocalDateTime.parse("2024-05-22T23:00:00", DateTimeFormatter.ISO_DATE_TIME);

        when(queryHandler.handle(new GetAccountBalanceQuery("1000", datetime))).thenReturn(balanceDTO);
        mockMvc.perform(get("/v1/account-balance/1000")
                        .param("timestamp", "2024-05-22T23:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", is("1000")))
                .andExpect(jsonPath("$.accountBalance", is(100.0)))
                .andDo(print());
        verify(queryHandler).handle(new GetAccountBalanceQuery("1000", datetime));
    }

    @Test
    public void givenNoneExistingAccountNumber_shouldReturnNotFoundStatus() throws Exception {

        LocalDateTime datetime = LocalDateTime.parse("2024-05-22T23:00:00", DateTimeFormatter.ISO_DATE_TIME);
        when(queryHandler.handle(new GetAccountBalanceQuery("1033", datetime))).thenThrow(new LedgerAccountNotFoundException("1033", datetime));

        mockMvc.perform(get("/v1/account-balance/1033")
                        .param("timestamp", "2024-05-22T23:00:00"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("LPS_0001")))
                .andExpect(jsonPath("$.description", is("Account specified not found: 1033")))
                .andDo(print());
    }

    @Test
    public void givenInvalidParams_shouldReturnBadRequestStatus() throws Exception {

        LocalDateTime datetime = LocalDateTime.parse("2024-05-22T23:00:00", DateTimeFormatter.ISO_DATE_TIME);
        when(queryHandler.handle(new GetAccountBalanceQuery("1033", datetime))).thenThrow(new InvalidLedgerPostingDataException("Invalid data format passed in"));

        mockMvc.perform(get("/v1/account-balance/1033")
                        .param("timestamp", "2024-05-22T23:00:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("LPS_0002")))
                .andExpect(jsonPath("$.description", is("Invalid data format passed in")))
                .andDo(print());
    }


    @Test
    void shouldPostLedgerEntryAndReturnOK() throws Exception {

        mockMvc.perform(post("/v1/post-ledger")
                .contentType("application/json")
                .content("""
                        [
                          {
                            "transactionAccount": {
                              "accountNumber": 1000,
                              "accountName": "Cash A/c"
                            },
                            "transactionAmount":1500,
                            "transactionType": "CR",
                            "description": "Purchase of machinery",
                            "transactionTime":"2024-05-19T23:00:00"
                          },
                          {
                            "transactionAccount": {
                              "accountNumber": 1000,
                              "accountName": "Machinery A/c"
                            },
                            "transactionAmount":1500,
                            "transactionType": "DB",
                            "description": "Purchase of machinery",
                            "transactionTime":"2024-05-19T23:00:00"
                          }
                        ]
                        """))
                .andExpect(status().isOk());
    }
}