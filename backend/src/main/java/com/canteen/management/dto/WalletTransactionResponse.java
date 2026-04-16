package com.canteen.management.dto;

import com.canteen.management.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletTransactionResponse(
        Long id,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        LocalDateTime transactionDate
) {
}
