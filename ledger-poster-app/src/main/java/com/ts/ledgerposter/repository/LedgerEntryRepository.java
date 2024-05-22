package com.ts.ledgerposter.repository;

import com.ts.ledgerposter.domain.LedgerEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LedgerEntryRepository extends MongoRepository<LedgerEntry, String> {

}
