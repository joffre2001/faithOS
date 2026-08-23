package com.obysoft.faithOS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportRequest(
        @NotBlank @Size(max = 120) String page,
        @NotBlank @Size(max = 2000) String expected,
        @NotBlank @Size(max = 4000) String error) {}
