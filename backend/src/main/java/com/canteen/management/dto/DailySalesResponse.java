package com.canteen.management.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesResponse(
        LocalDate date,
        BigDecimal totalSales,
        long totalOrders,
        long totalTransactions
) {
}
