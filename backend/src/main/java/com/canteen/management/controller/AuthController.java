package com.canteen.management.controller;

import com.canteen.management.dto.AuthRequest;
import com.canteen.management.dto.RegisterRequest;
import com.canteen.management.dto.UserResponse;
import com.canteen.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
