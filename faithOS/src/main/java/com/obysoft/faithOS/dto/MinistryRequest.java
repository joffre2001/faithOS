package com.obysoft.faithOS.dto;
import jakarta.validation.constraints.NotBlank;
public record MinistryRequest(@NotBlank(message="Name is required") String name,String description,String leaderName,Boolean active){}
