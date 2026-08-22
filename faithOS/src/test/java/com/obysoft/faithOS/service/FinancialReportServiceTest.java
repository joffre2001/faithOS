package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.entity.Contribution;
import com.obysoft.faithOS.entity.Expense;
import com.obysoft.faithOS.repository.ContributionRepository;
import com.obysoft.faithOS.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceTest {
    @Mock ContributionRepository contributions;
    @Mock ExpenseRepository expenses;
    @Mock CurrentChurchService current;
    @InjectMocks FinancialReportService service;

    @Test
    void aggregatesIncomeExpensesAndNetBalanceForCurrentChurch() {
        Church church = mock(Church.class);
        when(church.getId()).thenReturn(10L);
        when(current.church()).thenReturn(church);
        LocalDate from = LocalDate.of(2026, 8, 1);
        LocalDate to = LocalDate.of(2026, 8, 31);
        Contribution tithe = contribution("Tithe", "100.00");
        Contribution offering = contribution("Offering", "50.00");
        Expense rent = expense("Facilities", "40.00");
        when(contributions.findAllByChurchIdAndContributionDateBetween(10L, from, to))
                .thenReturn(List.of(tithe, offering));
        when(expenses.findAllByChurchIdAndExpenseDateBetween(10L, from, to))
                .thenReturn(List.of(rent));

        var report = service.report(from, to);

        assertThat(report.income()).isEqualByComparingTo("150.00");
        assertThat(report.expenses()).isEqualByComparingTo("40.00");
        assertThat(report.netBalance()).isEqualByComparingTo("110.00");
        assertThat(report.incomeByType()).containsEntry("Tithe", new BigDecimal("100.00"));
        assertThat(report.expensesByCategory()).containsEntry("Facilities", new BigDecimal("40.00"));
    }

    private Contribution contribution(String type, String amount) {
        Contribution contribution = new Contribution();
        contribution.setType(type);
        contribution.setAmount(new BigDecimal(amount));
        return contribution;
    }

    private Expense expense(String category, String amount) {
        Expense expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(new BigDecimal(amount));
        return expense;
    }
}
