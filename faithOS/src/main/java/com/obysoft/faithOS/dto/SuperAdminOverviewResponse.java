package com.obysoft.faithOS.dto;

public record SuperAdminOverviewResponse(
        long totalChurches,
        long activeChurches,
        long totalUsers,
        long activeUsers,
        long totalMinistries,
        long auditEvents) {}
