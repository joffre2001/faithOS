package com.obysoft.faithOS.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.AttendanceRequest;
import com.obysoft.faithOS.dto.AttendanceReportResponse;
import com.obysoft.faithOS.dto.AttendanceResponse;
import com.obysoft.faithOS.service.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping
    public List<AttendanceResponse> all() {
        return service.all();
    }

    @GetMapping("/report")
    public AttendanceReportResponse report(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return service.report(from, to);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    public ResponseEntity<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    public AttendanceResponse update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'CHURCH_ADMIN', 'PASTOR', 'LEADER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
