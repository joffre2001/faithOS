package com.obysoft.faithOS.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.LoginResponse;
import com.obysoft.faithOS.dto.SetupRequest;
import com.obysoft.faithOS.service.SetupService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import com.obysoft.faithOS.security.AuthCookieService;

@RestController
@RequestMapping("/api/setup")
public class SetupController {
    private final SetupService setupService;
    private final AuthCookieService authCookieService;
    public SetupController(SetupService setupService,AuthCookieService authCookieService) { this.setupService = setupService;this.authCookieService=authCookieService; }

    @GetMapping("/status")
    public Map<String, Boolean> status() { return Map.of("available", setupService.isAvailable()); }

    @PostMapping
    public ResponseEntity<LoginResponse> initialize(@Valid @RequestBody SetupRequest request,HttpServletResponse servletResponse) {
        LoginResponse response=setupService.initialize(request);authCookieService.issue(servletResponse,response.getToken());response.setToken(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
