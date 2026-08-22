package com.obysoft.faithOS.dto;
import java.math.BigDecimal; import java.time.LocalDate;
public record ContributionResponse(Long id,String donorName,BigDecimal amount,LocalDate contributionDate,String type,String method,String notes){}
