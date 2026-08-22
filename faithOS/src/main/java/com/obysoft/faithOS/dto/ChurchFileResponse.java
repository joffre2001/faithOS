package com.obysoft.faithOS.dto;

import java.time.LocalDateTime;

public record ChurchFileResponse(Long id, String name, String contentType, Long size,
        LocalDateTime createdAt, String uploadedBy) {}
