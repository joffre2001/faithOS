package com.obysoft.faithOS.dto;
import java.time.LocalDateTime;
public record EventResponse(Long id,String title,String description,LocalDateTime startsAt,LocalDateTime endsAt,String location,String category){}
