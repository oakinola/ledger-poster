package com.ts.ledgerposter.cqrs.projector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountPostedEventDTO;
import com.ts.ledgerposter.dto.LedgerAccountTransactionDTO;
import com.ts.ledgerposter.es.events.LedgerAccountPostedEvent;
import com.ts.ledgerposter.mappers.LedgerAccountMapper;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class LedgerAccountProjector {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final LedgerAccountRepository ledgerAccountRepository;


    @KafkaListener(topics = "ledger-posting-topic")
    public void consumeEvents(String ledgerAccountEventStr){
        try {
            LedgerAccountPostedEventDTO postedEventDTO = OBJECT_MAPPER.readValue(ledgerAccountEventStr, LedgerAccountPostedEventDTO.class);
            processEvent(postedEventDTO);
            log.info("Event Processed Details: {}", postedEventDTO);
        } catch (JsonProcessingException e) {
           log.error("LedgerAccountProjector: Error occurred trying to read event from: {} from kafka", ledgerAccountEventStr);
        }
    }

    private void processEvent(LedgerAccountPostedEventDTO eventDTO) {
        LedgerAccountTransactionDTO ledgerAccountTransactionDTO = LedgerAccountMapper.buildLedgerAccountTransactionDTOFromLedgerAccountPostedEventDTO(eventDTO);

        Optional<LedgerAccount> optionalLedgerAccount = ledgerAccountRepository.findByAccountNumber(ledgerAccountTransactionDTO.accountNumber());
        //Optional<LedgerAccount> optionalLedgerAccount = ledgerAccountRepository.findById(ledgerAccountTransactionDTO.id());
        if (optionalLedgerAccount.isPresent()) {
            LedgerAccount transactionAccount = optionalLedgerAccount.get();
            transactionAccount.setAccountName(ledgerAccountTransactionDTO.accountName());
            transactionAccount.setAccountBalance(transactionAccount.getAccountBalance() + (ledgerAccountTransactionDTO.transactionType().getValue() * ledgerAccountTransactionDTO.transactionAmount()));
            transactionAccount.setLastUpdated(ledgerAccountTransactionDTO.lastUpdated());
            this.ledgerAccountRepository.save(transactionAccount);
        } else {
            LedgerAccount newTransactionAccount = new LedgerAccount(
                    ledgerAccountTransactionDTO.id(),
                    ledgerAccountTransactionDTO.accountNumber(),
                    ledgerAccountTransactionDTO.accountName(),
                    ledgerAccountTransactionDTO.transactionType().getValue() * ledgerAccountTransactionDTO.transactionAmount(),
                    ledgerAccountTransactionDTO.lastUpdated());

            this.ledgerAccountRepository.save(newTransactionAccount);
        }
    }

}
