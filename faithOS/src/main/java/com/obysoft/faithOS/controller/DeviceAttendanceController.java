package com.obysoft.faithOS.controller;

import org.springframework.web.bind.annotation.*;
import com.obysoft.faithOS.dto.*;
import com.obysoft.faithOS.service.AttendanceDeviceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController @RequestMapping("/api/device/attendance")
public class DeviceAttendanceController {
 private final AttendanceDeviceService service;
 public DeviceAttendanceController(AttendanceDeviceService service){this.service=service;}
 @PostMapping("/check-in")
 public AttendanceCheckInResponse checkIn(@RequestHeader("X-Device-Id") String id,@RequestHeader("X-Device-Secret") String secret,@Valid @RequestBody DeviceCheckInRequest request,HttpServletRequest servletRequest){
  return service.checkIn(id,secret,request,servletRequest.getRemoteAddr());
 }
}
