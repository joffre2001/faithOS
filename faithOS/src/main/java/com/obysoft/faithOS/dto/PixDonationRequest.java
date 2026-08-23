package com.obysoft.faithOS.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PixDonationRequest(
        @NotNull @DecimalMin(value = "1.00", message = "Donation must be at least R$ 1.00") BigDecimal amount,
        @Size(max = 500) String notes) {}
