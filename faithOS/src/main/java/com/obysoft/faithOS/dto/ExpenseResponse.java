package com.obysoft.faithOS.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, String description, BigDecimal amount, LocalDate expenseDate,
        String category, String payee, String notes) {}
