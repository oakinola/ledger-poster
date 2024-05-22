package com.ts.ledgerposter.mappers;

import com.ts.ledgerposter.domain.LedgerAccount;
import com.ts.ledgerposter.dto.LedgerAccountBalanceDTO;
import com.ts.ledgerposter.dto.LedgerAccountDTO;

public final class LedgerAccountMapper {

    private LedgerAccountMapper() {
    }

    public static LedgerAccountDTO buildLedgerAccountDTOFromLedgerAccount(LedgerAccount ledgerAccount) {
        return new LedgerAccountDTO(
                ledgerAccount.getAccountNumber(),
                ledgerAccount.getAccountName(),
                ledgerAccount.getAccountBalance(),
                ledgerAccount.getLastUpdated()
        );
    }

    public static LedgerAccountBalanceDTO buildLedgerAccountBalanceDTOFromLedgerAccount(LedgerAccount ledgerAccount) {
        return new LedgerAccountBalanceDTO(
                ledgerAccount.getAccountNumber(),
                ledgerAccount.getAccountBalance()
        );
    }

}