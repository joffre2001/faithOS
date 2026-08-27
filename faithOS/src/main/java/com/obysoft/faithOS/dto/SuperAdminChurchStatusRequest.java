package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuperAdminChurchStatusRequest(
        @NotNull Boolean active,
        @NotBlank @Size(max = 500) String reason) {}
