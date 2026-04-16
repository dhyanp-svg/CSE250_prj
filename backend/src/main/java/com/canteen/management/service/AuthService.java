package com.canteen.management.service;

import com.canteen.management.dto.AuthRequest;
import com.canteen.management.dto.RegisterRequest;
import com.canteen.management.dto.UserResponse;
import com.canteen.management.entity.AppUser;
import com.canteen.management.entity.Role;
import com.canteen.management.exception.ApiException;
import com.canteen.management.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final MapperService mapperService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, MapperService mapperService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapperService = mapperService;
    }

    public UserResponse register(RegisterRequest request) {
        appUserRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            throw new ApiException("Email is already registered");
        });

        AppUser user = new AppUser();
        user.setName(request.name());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        return mapperService.toUserResponse(appUserRepository.save(user));
    }

    public UserResponse login(AuthRequest request) {
        AppUser user = appUserRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException("Invalid email or password"));

        boolean passwordMatches = user.getPassword().startsWith("$2")
                ? passwordEncoder.matches(request.password(), user.getPassword())
                : request.password().equals(user.getPassword());

        if (!passwordMatches) {
            throw new ApiException("Invalid email or password");
        }

        if (!user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(request.password()));
            appUserRepository.save(user);
        }
        return mapperService.toUserResponse(user);
    }
}
