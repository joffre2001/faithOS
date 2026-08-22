package com.obysoft.faithOS.dto;

import java.time.LocalDate;
import java.util.Map;

import com.obysoft.faithOS.entity.AttendanceType;

public record AttendanceReportResponse(
        LocalDate from,
        LocalDate to,
        int totalSessions,
        int totalCheckIns,
        int uniqueAttendees,
        double averageAttendance,
        Map<AttendanceType, Integer> sessionsByType,
        Map<AttendanceType, Integer> checkInsByType) {}
