package com.canteen.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WalletTopUpRequest(
        @NotNull @DecimalMin(value = "1.00") BigDecimal amount
) {
}
