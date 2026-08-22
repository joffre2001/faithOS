package com.obysoft.faithOS.dto;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
public record MinistryRequest(@NotBlank(message="Name is required") String name,String description,Long leaderId,Set<Long> memberIds,Boolean active){}
