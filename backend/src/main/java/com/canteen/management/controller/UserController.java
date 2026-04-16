package com.canteen.management.controller;

import com.canteen.management.dto.UserResponse;
import com.canteen.management.dto.WalletTopUpRequest;
import com.canteen.management.dto.WalletTransactionResponse;
import com.canteen.management.exception.ApiException;
import com.canteen.management.repository.AppUserRepository;
import com.canteen.management.service.MapperService;
import com.canteen.management.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final WalletService walletService;
    private final AppUserRepository appUserRepository;
    private final MapperService mapperService;

    public UserController(WalletService walletService, AppUserRepository appUserRepository, MapperService mapperService) {
        this.walletService = walletService;
        this.appUserRepository = appUserRepository;
        this.mapperService = mapperService;
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return appUserRepository.findById(userId)
                .map(mapperService::toUserResponse)
                .orElseThrow(() -> new ApiException("User not found"));
    }

    @PostMapping("/{userId}/wallet/top-up")
    public UserResponse topUp(@PathVariable Long userId, @Valid @RequestBody WalletTopUpRequest request) {
        return walletService.topUp(userId, request.amount());
    }

    @GetMapping("/{userId}/wallet/transactions")
    public List<WalletTransactionResponse> getTransactions(@PathVariable Long userId) {
        return walletService.getHistory(userId);
    }
}
