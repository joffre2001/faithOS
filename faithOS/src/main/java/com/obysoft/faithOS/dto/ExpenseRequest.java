package com.obysoft.faithOS.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
        @NotBlank String description,
        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive") BigDecimal amount,
        @NotNull LocalDate expenseDate,
        @NotBlank String category,
        String payee,
        String notes) {}
