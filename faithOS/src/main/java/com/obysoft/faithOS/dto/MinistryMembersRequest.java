package com.obysoft.faithOS.dto;
import java.util.Set;import jakarta.validation.constraints.NotNull;
public record MinistryMembersRequest(@NotNull Set<Long> memberIds){}
