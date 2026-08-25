package com.obysoft.faithOS.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import com.obysoft.faithOS.entity.AttendanceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        @NotBlank(message = "Title is required") String title,
        @NotNull(message = "Attendance type is required") AttendanceType type,
        @NotNull(message = "Session date is required") LocalDate sessionDate,
        LocalTime opensAt,
        LocalTime onTimeUntil,
        LocalTime closesAt,
        Set<Long> attendeeIds) {
    public AttendanceRequest(String title, AttendanceType type, LocalDate sessionDate, Set<Long> attendeeIds) {
        this(title, type, sessionDate, null, null, null, attendeeIds);
    }
}
