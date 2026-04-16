package com.canteen.management.service;

import com.canteen.management.dto.UserResponse;
import com.canteen.management.dto.WalletTransactionResponse;
import com.canteen.management.entity.AppUser;
import com.canteen.management.entity.TransactionType;
import com.canteen.management.entity.WalletTransaction;
import com.canteen.management.exception.ApiException;
import com.canteen.management.repository.AppUserRepository;
import com.canteen.management.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final AppUserRepository appUserRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final MapperService mapperService;

    public WalletService(AppUserRepository appUserRepository,
                         WalletTransactionRepository walletTransactionRepository,
                         MapperService mapperService) {
        this.appUserRepository = appUserRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.mapperService = mapperService;
    }

    @Transactional
    public UserResponse topUp(Long userId, BigDecimal amount) {
        AppUser user = findUser(userId);
        user.setWalletBalance(user.getWalletBalance().add(amount));
        recordTransaction(user, TransactionType.CREDIT, amount, "Wallet top-up");
        return mapperService.toUserResponse(appUserRepository.save(user));
    }

    public List<WalletTransactionResponse> getHistory(Long userId) {
        findUser(userId);
        return walletTransactionRepository.findByUserIdOrderByTransactionDateDesc(userId).stream()
                .map(mapperService::toWalletResponse)
                .toList();
    }

    @Transactional
    public void debitForOrder(AppUser user, BigDecimal amount, String description) {
        if (user.getWalletBalance().compareTo(amount) < 0) {
            throw new ApiException("Insufficient wallet balance");
        }
        user.setWalletBalance(user.getWalletBalance().subtract(amount));
        recordTransaction(user, TransactionType.DEBIT, amount, description);
        appUserRepository.save(user);
    }

    public AppUser findUser(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    private void recordTransaction(AppUser user, TransactionType type, BigDecimal amount, String description) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(user.getWalletBalance());
        transaction.setDescription(description);
        walletTransactionRepository.save(transaction);
    }
}
