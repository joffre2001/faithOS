package com.obysoft.faithOS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.obysoft.faithOS.dto.LoginRequest;
import com.obysoft.faithOS.dto.LoginResponse;
import com.obysoft.faithOS.dto.SetupRequest;
import com.obysoft.faithOS.service.AuthService;
import com.obysoft.faithOS.service.ChurchRegistrationService;
import org.springframework.http.HttpStatus;
import com.obysoft.faithOS.security.AuthCookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import com.obysoft.faithOS.security.AbuseProtectionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final ChurchRegistrationService registrationService;
    private final AuthCookieService authCookieService;
    private final AbuseProtectionService abuseProtectionService;

    public AuthController(AuthService authService, ChurchRegistrationService registrationService, AuthCookieService authCookieService, AbuseProtectionService abuseProtectionService) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.authCookieService = authCookieService;
        this.abuseProtectionService = abuseProtectionService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
           @Valid @RequestBody LoginRequest request, HttpServletResponse servletResponse, HttpServletRequest httpRequest) {

        String key=httpRequest.getRemoteAddr()+":"+request.getEmail().trim().toLowerCase();abuseProtectionService.requireLoginAllowed(key);
        LoginResponse response;
        try { response=authService.login(request);abuseProtectionService.loginSucceeded(key); }
        catch (RuntimeException exception) { abuseProtectionService.loginFailed(key);throw exception; }
        authCookieService.issue(servletResponse,response.getToken());
        response.setToken(null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-church")
    public ResponseEntity<LoginResponse> registerChurch(@Valid @RequestBody SetupRequest request, HttpServletResponse servletResponse,HttpServletRequest httpRequest) {
        abuseProtectionService.requireRegistrationAllowed(httpRequest.getRemoteAddr());
        LoginResponse response=registrationService.register(request);authCookieService.issue(servletResponse,response.getToken());response.setToken(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){authCookieService.clear(response);return ResponseEntity.noContent().build();}
}
