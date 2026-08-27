package com.obysoft.faithOS.dto;

import com.obysoft.faithOS.entity.Role;

public record SuperAdminUserSummary(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean active) {}
