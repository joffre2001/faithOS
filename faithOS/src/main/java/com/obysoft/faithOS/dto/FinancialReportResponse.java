package com.obysoft.faithOS.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record FinancialReportResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal netBalance,
        Map<String, BigDecimal> incomeByType,
        Map<String, BigDecimal> expensesByCategory) {}
