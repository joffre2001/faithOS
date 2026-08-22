package com.obysoft.faithOS.dto;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, String title, String message, String type,
        LocalDateTime createdAt, boolean read) {}
