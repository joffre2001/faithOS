package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 2000) String message,
        @NotBlank @Size(max = 60) String type) {}
