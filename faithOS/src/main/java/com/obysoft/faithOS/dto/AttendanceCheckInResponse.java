package com.obysoft.faithOS.dto;
import java.time.LocalDateTime;import com.obysoft.faithOS.entity.AttendanceStatus;import com.obysoft.faithOS.entity.CheckInSource;
public record AttendanceCheckInResponse(Long userId,String memberCode,String memberName,LocalDateTime checkedInAt,AttendanceStatus status,CheckInSource source){}
