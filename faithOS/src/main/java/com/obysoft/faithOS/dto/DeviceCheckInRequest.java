package com.obysoft.faithOS.dto;
import jakarta.validation.constraints.NotBlank;
public record DeviceCheckInRequest(@NotBlank String memberCode) {}
