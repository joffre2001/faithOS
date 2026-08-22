package com.obysoft.faithOS.dto;
import java.time.LocalDateTime; import jakarta.validation.constraints.NotBlank; import jakarta.validation.constraints.NotNull;
public record EventRequest(@NotBlank(message="Title is required") String title,String description,@NotNull(message="Start date is required") LocalDateTime startsAt,LocalDateTime endsAt,String location,String category){}
