package com.canteen.management.service;

import com.canteen.management.dto.*;
import com.canteen.management.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapperService {

    public UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getWalletBalance(),
                user.getCreatedAt()
        );
    }

    public MenuItemResponse toMenuResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getItemName(),
                item.getPrice(),
                item.getCategory(),
                item.isAvailable(),
                item.getDescription()
        );
    }

    public WalletTransactionResponse toWalletResponse(WalletTransaction transaction) {
        return new WalletTransactionResponse(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getDescription(),
                transaction.getTransactionDate()
        );
    }

    public OrderResponse toOrderResponse(CanteenOrder order) {
        List<OrderResponse.OrderLineResponse> lines = order.getOrderDetails().stream()
                .map(detail -> new OrderResponse.OrderLineResponse(
                        detail.getItem().getId(),
                        detail.getItem().getItemName(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getLineTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                lines
        );
    }
}
