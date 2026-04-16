package com.canteen.management.dto;

import com.canteen.management.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long userId,
        String userName,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        OrderStatus status,
        List<OrderLineResponse> items
) {
    public record OrderLineResponse(
            Long itemId,
            String itemName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }
}
