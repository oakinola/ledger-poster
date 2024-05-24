package com.ts.ledgerposter.repository;

import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.es.events.LedgerAccountPostedEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface LedgerPostingEventsRepository extends MongoRepository<LedgerAccountPostedEvent, UUID> {

}
