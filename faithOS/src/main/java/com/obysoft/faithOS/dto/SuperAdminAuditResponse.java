package com.obysoft.faithOS.dto;

import java.time.LocalDateTime;

public record SuperAdminAuditResponse(
        Long id,
        String actorEmail,
        String action,
        String targetType,
        Long targetId,
        String reason,
        LocalDateTime createdAt) {}
