package com.canteen.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlaceOrderRequest(
        @NotEmpty List<@Valid OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull Long itemId,
            @NotNull @Min(1) Integer quantity
    ) {
    }
}
