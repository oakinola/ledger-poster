package com.ts.ledgerposter.repository;

import com.ts.ledgerposter.domain.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, UUID> {
    Optional<LedgerAccount> findByAccountNumberAndLastUpdated(String accountNumber, LocalDateTime timestamp);
}
