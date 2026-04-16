package com.canteen.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank String name,
        @Email
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@ahduni\\.edu\\.in$", message = "Only @ahduni.edu.in email addresses are allowed")
        String email,
        @NotBlank String password
) {
}
