package com.canteen.management.controller;

import com.canteen.management.dto.DailySalesResponse;
import com.canteen.management.dto.UserResponse;
import com.canteen.management.repository.AppUserRepository;
import com.canteen.management.service.MapperService;
import com.canteen.management.service.OrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AppUserRepository appUserRepository;
    private final MapperService mapperService;
    private final OrderService orderService;

    public AdminController(AppUserRepository appUserRepository, MapperService mapperService, OrderService orderService) {
        this.appUserRepository = appUserRepository;
        this.mapperService = mapperService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return appUserRepository.findAll().stream()
                .map(mapperService::toUserResponse)
                .toList();
    }

    @GetMapping("/reports/daily-sales")
    public DailySalesResponse dailySales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return orderService.getDailySales(date);
    }
}
