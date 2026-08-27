package com.obysoft.faithOS.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.SuperAdminAssignAdminRequest;
import com.obysoft.faithOS.dto.SuperAdminAuditResponse;
import com.obysoft.faithOS.dto.SuperAdminChurchResponse;
import com.obysoft.faithOS.dto.SuperAdminChurchStatusRequest;
import com.obysoft.faithOS.dto.SuperAdminOverviewResponse;
import com.obysoft.faithOS.dto.SuperAdminUserSummary;
import com.obysoft.faithOS.service.SuperAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {
    private final SuperAdminService service;

    public SuperAdminController(SuperAdminService service) { this.service = service; }

    @GetMapping("/overview")
    public SuperAdminOverviewResponse overview() { return service.overview(); }

    @GetMapping("/churches")
    public List<SuperAdminChurchResponse> churches() { return service.churchList(); }

    @GetMapping("/churches/{id}/users")
    public List<SuperAdminUserSummary> churchUsers(@PathVariable Long id) {
        return service.churchUsers(id);
    }

    @PatchMapping("/churches/{id}/status")
    public SuperAdminChurchResponse status(@PathVariable Long id,
            @Valid @RequestBody SuperAdminChurchStatusRequest request) {
        return service.updateChurchStatus(id, request);
    }

    @PatchMapping("/churches/{id}/administrator")
    public SuperAdminChurchResponse administrator(@PathVariable Long id,
            @Valid @RequestBody SuperAdminAssignAdminRequest request) {
        return service.assignAdministrator(id, request);
    }

    @GetMapping("/audit-log")
    public List<SuperAdminAuditResponse> auditLog() { return service.auditLog(); }
}
