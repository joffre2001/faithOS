package com.obysoft.faithOS.dto;

import java.time.LocalDate;
import java.util.List;

import com.obysoft.faithOS.entity.AttendanceType;

public record AttendanceResponse(
        Long id,
        String title,
        AttendanceType type,
        LocalDate sessionDate,
        List<MinistryMemberResponse> attendees) {}
