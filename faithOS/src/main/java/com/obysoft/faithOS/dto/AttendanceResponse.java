package com.obysoft.faithOS.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.obysoft.faithOS.entity.AttendanceType;

public record AttendanceResponse(
        Long id,
        String title,
        AttendanceType type,
        LocalDate sessionDate,
        LocalTime opensAt,
        LocalTime onTimeUntil,
        LocalTime closesAt,
        List<MinistryMemberResponse> attendees,
        List<AttendanceCheckInResponse> checkIns) {}
