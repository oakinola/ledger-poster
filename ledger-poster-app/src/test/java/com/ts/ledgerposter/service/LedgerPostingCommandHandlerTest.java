package com.ts.ledgerposter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.dto.LedgerAccountPostedEventDTO;
import com.ts.ledgerposter.dto.LedgerTransactionDTO;
import com.ts.ledgerposter.dto.TransactionType;
import com.ts.ledgerposter.es.events.LedgerAccountPostedEvent;
import com.ts.ledgerposter.repository.LedgerPostingEventsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.ts.ledgerposter.mappers.LedgerAccountMapper.buildLedgerAccountPostedEventDTOFromLedgerAccountPostedEvent;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LedgerPostingCommandHandlerTest {
    LedgerPostingEventsRepository ledgerEventsRepositoryMock = mock(LedgerPostingEventsRepository.class);
    KafkaTemplate<UUID, String> kafkaTemplateMock = mock(KafkaTemplate.class);
    LedgerPostingCommandHandler ledgerPostingCommandHandler = new LedgerPostingCommandHandler(ledgerEventsRepositoryMock, kafkaTemplateMock);

    @Test
    void verifyHandleSuccessfullyCalled() {
        UUID entryId = UUID.randomUUID();
        LedgerAccountPostedEvent event = LedgerAccountPostedEvent.builder()
                .id(entryId)
                .accountNumber("1100").accountName("test2").accountBalance(100.0)
                .lastUpdated(LocalDateTime.parse("2024-05-21T00:00:00", DateTimeFormatter.ISO_DATE_TIME))
                .transactionType(TransactionType.DB).build();

        LedgerAccountPostedEventDTO eventDTO = buildLedgerAccountPostedEventDTOFromLedgerAccountPostedEvent(event);
        List<LedgerTransactionDTO> transactionEntries = List.of(
               new LedgerTransactionDTO(entryId, "1100","test2",100.0,  TransactionType.DB, "Something", "2024-05-21T00:00:00")
        );
        PostLedgerEntryCommand postLedgerEntryCommand = new PostLedgerEntryCommand(transactionEntries);

        ledgerPostingCommandHandler.handle(postLedgerEntryCommand);
        verify(ledgerEventsRepositoryMock, times(1)).save(event);
        verify(kafkaTemplateMock, times(1))
                .sendDefault(eventDTO.id(), toJson(eventDTO));
    }

    private String toJson(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}