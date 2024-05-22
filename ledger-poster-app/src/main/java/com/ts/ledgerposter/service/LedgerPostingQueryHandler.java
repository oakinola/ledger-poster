package com.ts.ledgerposter.service;

import com.ts.ledgerposter.cqrs.queries.GetAccountBalanceQuery;
import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.exceptions.LedgerAccountNotFoundException;
import com.ts.ledgerposter.mappers.LedgerAccountMapper;
import com.ts.ledgerposter.repository.LedgerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LedgerPostingQueryHandler {

    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountBalanceDTO handle(GetAccountBalanceQuery command) {
        Optional<LedgerAccount> optionalLedgerAccount = ledgerAccountRepository.findByAccountNumberAndLastUpdated(command.accountNumber(), command.timestamp());
        if (optionalLedgerAccount.isPresent()) {
            return LedgerAccountMapper.buildLedgerAccountBalanceDTOFromLedgerAccount(optionalLedgerAccount.get());
        } else {
            throw new LedgerAccountNotFoundException(command.accountNumber(), command.timestamp());
        }
    }
}
