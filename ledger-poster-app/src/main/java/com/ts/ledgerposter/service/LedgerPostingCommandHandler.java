package com.ts.ledgerposter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.dto.LedgerAccountPostedEventDTO;
import com.ts.ledgerposter.es.events.LedgerAccountPostedEvent;
import com.ts.ledgerposter.mappers.LedgerAccountMapper;
import com.ts.ledgerposter.repository.LedgerPostingEventsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LedgerPostingCommandHandler {

    private final LedgerPostingEventsRepository ledgerEventsRepository;

    @Autowired
    private KafkaTemplate<UUID, String> kafkaTemplate;

    public void handle(PostLedgerEntryCommand command) {

        command.entries().forEach(entry -> {
                final LedgerAccountPostedEvent event =  LedgerAccountPostedEvent.builder()
                        .id(UUID.randomUUID())
                        .accountNumber(entry.getTransactionAccount().getAccountNumber())
                        .accountName(entry.getTransactionAccount().getAccountName())
                        .accountBalance(entry.getTransactionAmount())
                        .lastUpdated(entry.getTransactionTime())
                        .transactionType(entry.getTransactionType())
                        .build();

                saveEvent(event);
                raiseEvent(event);
            });
    }

    private void saveEvent(LedgerAccountPostedEvent event){
        ledgerEventsRepository.save(event);
    }

    private void raiseEvent(LedgerAccountPostedEvent event) {
        LedgerAccountPostedEventDTO eventDTO =
                LedgerAccountMapper.buildLedgerAccountPostedEventDTOFromLedgerAccountPostedEvent(event);
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(eventDTO);
            this.kafkaTemplate.sendDefault(event.getId(), value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
