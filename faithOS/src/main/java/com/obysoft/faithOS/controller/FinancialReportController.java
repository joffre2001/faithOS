package com.obysoft.faithOS.controller;

import java.time.LocalDate;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.FinancialReportResponse;
import com.obysoft.faithOS.service.FinancialReportService;

@RestController
@RequestMapping("/api/finance")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN')")
public class FinancialReportController {
    private final FinancialReportService service;
    public FinancialReportController(FinancialReportService service) { this.service = service; }
    @GetMapping("/report")
    public FinancialReportResponse report(@RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return service.report(from, to);
    }
}
