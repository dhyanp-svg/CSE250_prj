package com.canteen.management.service;

import com.canteen.management.dto.DailySalesResponse;
import com.canteen.management.dto.OrderResponse;
import com.canteen.management.dto.PlaceOrderRequest;
import com.canteen.management.entity.*;
import com.canteen.management.exception.ApiException;
import com.canteen.management.repository.AppUserRepository;
import com.canteen.management.repository.CanteenOrderRepository;
import com.canteen.management.repository.MenuItemRepository;
import com.canteen.management.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final AppUserRepository appUserRepository;
    private final MenuItemRepository menuItemRepository;
    private final CanteenOrderRepository canteenOrderRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletService walletService;
    private final MapperService mapperService;

    public OrderService(AppUserRepository appUserRepository,
                        MenuItemRepository menuItemRepository,
                        CanteenOrderRepository canteenOrderRepository,
                        WalletTransactionRepository walletTransactionRepository,
                        WalletService walletService,
                        MapperService mapperService) {
        this.appUserRepository = appUserRepository;
        this.menuItemRepository = menuItemRepository;
        this.canteenOrderRepository = canteenOrderRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.walletService = walletService;
        this.mapperService = mapperService;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        CanteenOrder order = new CanteenOrder();
        order.setUser(user);

        List<OrderDetail> details = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PlaceOrderRequest.OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.itemId())
                    .orElseThrow(() -> new ApiException("Menu item not found: " + itemRequest.itemId()));
            if (!menuItem.isAvailable()) {
                throw new ApiException(menuItem.getItemName() + " is currently unavailable");
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setItem(menuItem);
            detail.setQuantity(itemRequest.quantity());
            detail.setUnitPrice(menuItem.getPrice());
            detail.setLineTotal(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            total = total.add(detail.getLineTotal());
            details.add(detail);
        }

        walletService.debitForOrder(user, total, "Order payment");
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);
        order.getOrderDetails().addAll(details);

        return mapperService.toOrderResponse(canteenOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return canteenOrderRepository.findByUserIdOrderByOrderDateDesc(userId).stream()
                .map(mapperService::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return canteenOrderRepository.findAll().stream()
                .map(mapperService::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailySalesResponse getDailySales(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);
        List<CanteenOrder> orders = canteenOrderRepository.findByOrderDateBetween(start, end);

        BigDecimal totalSales = orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .map(CanteenOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalTransactions = walletTransactionRepository.findAll().stream()
                .filter(tx -> tx.getTransactionDate().toLocalDate().equals(date))
                .count();

        return new DailySalesResponse(date, totalSales, orders.size(), totalTransactions);
    }
}
