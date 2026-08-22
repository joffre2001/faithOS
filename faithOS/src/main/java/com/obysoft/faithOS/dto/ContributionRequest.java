package com.obysoft.faithOS.dto;
import java.math.BigDecimal; import java.time.LocalDate; import jakarta.validation.constraints.*;
public record ContributionRequest(String donorName,@NotNull @DecimalMin(value="0.01",message="Amount must be positive") BigDecimal amount,@NotNull LocalDate contributionDate,@NotBlank String type,String method,String notes){}
