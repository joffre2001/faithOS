package com.obysoft.faithOS.dto;

public record SuperAdminChurchResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        String cnpj,
        String principalPastor,
        boolean active,
        long userCount,
        Long administratorId,
        String administratorName,
        String administratorEmail) {}
