package com.obysoft.faithOS.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.obysoft.faithOS.dto.FinancialReportResponse;
import com.obysoft.faithOS.repository.ContributionRepository;
import com.obysoft.faithOS.repository.ExpenseRepository;

@Service
public class FinancialReportService {
    private final ContributionRepository contributions;
    private final ExpenseRepository expenses;
    private final CurrentChurchService current;

    public FinancialReportService(ContributionRepository contributions, ExpenseRepository expenses, CurrentChurchService current) {
        this.contributions = contributions;
        this.expenses = expenses;
        this.current = current;
    }

    @Transactional(readOnly = true)
    public FinancialReportResponse report(LocalDate from, LocalDate to) {
        LocalDate safeTo = to == null ? LocalDate.now() : to;
        LocalDate safeFrom = from == null ? safeTo.withDayOfMonth(1) : from;
        if (safeFrom.isAfter(safeTo)) throw new IllegalArgumentException("Report start date must not be after the end date.");
        Long churchId = current.church().getId();
        var incomeByType = new TreeMap<String, BigDecimal>();
        var expensesByCategory = new TreeMap<String, BigDecimal>();
        contributions.findAllByChurchIdAndContributionDateBetween(churchId, safeFrom, safeTo)
                .forEach(item -> incomeByType.merge(item.getType(), item.getAmount(), BigDecimal::add));
        expenses.findAllByChurchIdAndExpenseDateBetween(churchId, safeFrom, safeTo)
                .forEach(item -> expensesByCategory.merge(item.getCategory(), item.getAmount(), BigDecimal::add));
        BigDecimal income = incomeByType.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenseTotal = expensesByCategory.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new FinancialReportResponse(safeFrom, safeTo, income, expenseTotal, income.subtract(expenseTotal), incomeByType, expensesByCategory);
    }
}
