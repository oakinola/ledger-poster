package com.ts.ledgerposter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.cqrs.commands.PostLedgerEntryCommand;
import com.ts.ledgerposter.dto.LedgerAccountPostedEventDTO;
import com.ts.ledgerposter.mappers.LedgerAccountMapper;
import com.ts.ledgerposter.repository.LedgerPostingEventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LedgerPostingCommandHandler {

    private final LedgerPostingEventsRepository ledgerEventsRepository;
    private final KafkaTemplate<UUID, String> kafkaTemplate;

    public void handle(PostLedgerEntryCommand command) {

        command.entries().forEach(transactionEntry -> {
                final LedgerAccountPostedEventDTO eventDTO =  new LedgerAccountPostedEventDTO(
                        transactionEntry.getTransactionEntryId() == null ? UUID.randomUUID() : transactionEntry.getTransactionEntryId(),
                        transactionEntry.getAccountNumber(),
                        transactionEntry.getAccountName(),
                        transactionEntry.getTransactionAmount(),
                        transactionEntry.getTransactionType(),
                        transactionEntry.getTransactionTime());

                saveEvent(eventDTO);
                raiseEvent(eventDTO);
            });
    }

    private void saveEvent(LedgerAccountPostedEventDTO eventDTO){
        ledgerEventsRepository.save(LedgerAccountMapper.buildLedgerAccountPostedEventFromLedgerAccountPostedEventDTO(eventDTO));
    }

    private void raiseEvent(LedgerAccountPostedEventDTO eventDTO) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            String value = objectMapper.writeValueAsString(eventDTO);
            this.kafkaTemplate.sendDefault(eventDTO.id(), value);
        } catch (JsonProcessingException jpe) {
            log.error("Unable to write message to topic: {} from kafka", eventDTO);
            jpe.printStackTrace();
        }
    }
}
