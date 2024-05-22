package com.ts.ledgerposter.repository;

import com.ts.ledgerposter.domain.LedgerAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LedgerAccountRepository extends MongoRepository<LedgerAccount, String> {
    Optional<LedgerAccount> findByAccountNumberAndLastUpdated(String accountNumber, LocalDateTime timestamp);
}
