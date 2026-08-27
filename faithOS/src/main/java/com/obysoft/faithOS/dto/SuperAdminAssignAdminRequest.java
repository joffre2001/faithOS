package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SuperAdminAssignAdminRequest(
        @NotNull Long userId,
        @NotBlank @Size(max = 500) String reason) {}
