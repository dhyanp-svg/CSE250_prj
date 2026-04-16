package com.canteen.management.dto;

import com.canteen.management.entity.MenuCategory;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        String itemName,
        BigDecimal price,
        MenuCategory category,
        boolean available,
        String description
) {
}
