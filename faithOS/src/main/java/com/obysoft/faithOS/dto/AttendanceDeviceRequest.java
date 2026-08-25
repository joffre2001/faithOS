package com.obysoft.faithOS.dto;import jakarta.validation.constraints.*;public record AttendanceDeviceRequest(@NotBlank @Size(max=120)String name){}
