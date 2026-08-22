package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetConfirmRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 12, message = "Password must contain at least 12 characters") String newPassword) {}
