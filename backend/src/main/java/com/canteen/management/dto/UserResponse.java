package com.canteen.management.dto;

import com.canteen.management.entity.Role;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        BigDecimal walletBalance,
        LocalDateTime createdAt
) {
}
